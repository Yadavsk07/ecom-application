package com.app.ecom_application.Service;

import com.app.ecom_application.Dto.CartItemResponse;
import com.app.ecom_application.Dto.CartValidationResult;
import com.app.ecom_application.Dto.OrderResponse;
import com.app.ecom_application.Dto.OrderItemDto;
import com.app.ecom_application.Model.Order;
import com.app.ecom_application.Model.OrderItem;
import com.app.ecom_application.Model.OrderStatus;
import com.app.ecom_application.Model.User;
import com.app.ecom_application.Repository.OrderRepository;
import com.app.ecom_application.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderTransactionService {

    private final CartService cartService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductInventoryService productInventoryService;


    @Transactional
    public Optional<OrderResponse> createOrderTransactionally(
            Long userId,
            String paymentOrderId,
            String paymentId) {

        // =====================================================
        // 1. PREVENT DUPLICATE ORDER
        // =====================================================

        if (orderRepository.existsByPaymentOrderId(paymentOrderId)) {
            return Optional.empty();
        }


        // =====================================================
        // 2. READ CART FROM MONGODB
        // =====================================================

        List<CartItemResponse> cartItems =
                cartService.getCartFromMongoDB(userId);

        if (cartItems.isEmpty()) {
            return Optional.empty();
        }


        // =====================================================
        // 3. GET USER
        // =====================================================

        Optional<User> userOptional =
                userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();


        // =====================================================
        // 4. VALIDATE CART AGAINST CURRENT DATABASE STATE
        // =====================================================
        /*
         * This validation is performed inside the transaction
         * before modifying inventory.
         */

        CartValidationResult validation =
                cartService.validateCartForCheckoutFromMongoDB(userId);

        if (!validation.isValid()) {

            throw new IllegalStateException(
                    validation.getMessage()
            );
        }


        // =====================================================
        // 5. GET AUTHORITATIVE TOTAL
        // =====================================================

        BigDecimal totalPrice =
                validation.getTotalAmount();


        // =====================================================
        // 6. CREATE ORDER
        // =====================================================

        Order order = new Order();

        order.setUserId(userId);
        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);
        order.setPaymentOrderId(paymentOrderId);
        order.setPaymentId(paymentId);
        order.setPaymentStatus("PAID");


        // =====================================================
        // 7. CREATE ORDER ITEMS
        // =====================================================

        List<OrderItem> orderItems =
                cartItems.stream()
                        .map(item -> {

                            OrderItem orderItem =
                                    new OrderItem();

                            orderItem.setId(
                                    UUID.randomUUID().toString()
                            );

                            orderItem.setProductId(
                                    item.getProductId()
                            );

                            orderItem.setProduct(
                                    item.getProduct()
                            );

                            orderItem.setQuantity(
                                    item.getQuantity()
                            );

                            BigDecimal unitPrice =
                                    item.getProduct().getPrice();

                            orderItem.setPrice(
                                    unitPrice
                            );

                            BigDecimal subtotal =
                                    unitPrice.multiply(
                                            BigDecimal.valueOf(
                                                    item.getQuantity()
                                            )
                                    );

                            orderItem.setSubTotal(
                                    subtotal
                            );

                            return orderItem;

                        })
                        .toList();

        order.setItems(orderItems);


        // =====================================================
        // 8. DECREASE INVENTORY
        // =====================================================

        for (CartItemResponse item : cartItems) {

            boolean stockDecreased =
                    productInventoryService.decreaseStock(
                            item.getProductId(),
                            item.getQuantity()
                    );

            if (!stockDecreased) {

                throw new IllegalStateException(
                        "Insufficient stock for product ID: "
                                + item.getProductId()
                );
            }
        }


        // =====================================================
        // 9. SAVE ORDER
        // =====================================================

        Order savedOrder =
                orderRepository.save(order);


        // =====================================================
        // 10. DELETE MONGODB CART
        // =====================================================

        cartService.clearMongoCart(userId);


        // =====================================================
        // 11. TRANSACTION COMMIT
        // =====================================================
        /*
         * Because this method is @Transactional:
         *
         * - Stock changes
         * - Order creation
         * - MongoDB cart deletion
         *
         * are committed together.
         *
         * If an exception occurs, MongoDB rolls everything back.
         */

        return Optional.of(
                mapToOrderResponse(savedOrder)
        );
    }


    // =========================================================
    // MAP ORDER TO RESPONSE
    // =========================================================

    private OrderResponse mapToOrderResponse(
            Order order) {

        return new OrderResponse(

                order.getId(),

                order.getTotalAmount(),

                order.getPaymentOrderId(),

                order.getPaymentId(),

                order.getPaymentStatus(),

                order.getStatus(),

                order.getItems()
                        .stream()
                        .map(orderItem ->
                                new OrderItemDto(

                                        orderItem.getId(),

                                        orderItem.getProductId(),

                                        orderItem.getQuantity(),

                                        orderItem.getPrice(),

                                        orderItem.getSubTotal()

                                )
                        )
                        .toList(),

                order.getCreatedAt()
        );
    }
}