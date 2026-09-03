package com.app.ecom_application.Service;

import com.app.ecom_application.Dto.CartItemResponse;
import com.app.ecom_application.Dto.OrderItemDto;
import com.app.ecom_application.Dto.OrderResponse;
import com.app.ecom_application.Model.*;
import com.app.ecom_application.Repository.OrderRepository;
import com.app.ecom_application.Repository.UserRepository;
import com.app.ecom_application.Dto.PaymentVerificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartService cartService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final RazorpayService razorpayService;

    public Optional<OrderResponse> createOrder(Long userId, PaymentVerificationRequest paymentRequest) {

        if (!razorpayService.verifySignature(
                paymentRequest.getOrderId(), paymentRequest.getPaymentId(), paymentRequest.getSignature())) {
            return Optional.empty();
        }
        if (orderRepository.existsByPaymentOrderId(paymentRequest.getOrderId())) {
            return Optional.empty();
        }

        List<CartItemResponse> cartItems = cartService.getCart(userId);

        if(cartItems.isEmpty())
        {
            return Optional.empty();
        }

        Optional<User> userOptional = userRepository.findById(userId);

        if(userOptional.isEmpty())
        {
            return Optional.empty();
        }

        User user = userOptional.get();

        BigDecimal totalPrice = cartItems.stream()
                .map(CartItemResponse::getPrice)
                .reduce(BigDecimal.ZERO , BigDecimal::add);


        Order order = new Order();

        order.setUserId(userId);
        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);
        order.setPaymentOrderId(paymentRequest.getOrderId());
        order.setPaymentId(paymentRequest.getPaymentId());
        order.setPaymentStatus("PAID");

        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> {

                    OrderItem orderItem = new OrderItem();

                    orderItem.setId(UUID.randomUUID().toString());
                    orderItem.setProductId(item.getProductId());
                    orderItem.setProduct(item.getProduct());
                    orderItem.setQuantity(item.getQuantity());
                        BigDecimal unitPrice = item.getPrice()
                            .divide(BigDecimal.valueOf(item.getQuantity()), 2, java.math.RoundingMode.HALF_UP);
                        orderItem.setPrice(unitPrice);

                    if (item.getPrice() != null && item.getQuantity() != null) {
                        orderItem.setSubTotal(
                                item.getPrice()
                        );
                    }

                    return orderItem;
                })
                .toList();

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(userId);

        return Optional.of(mapToOrderResponse(savedOrder));
    }

    public List<OrderResponse> getOrdersForUser(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    private OrderResponse mapToOrderResponse(Order order) {

        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getPaymentOrderId(),
                order.getPaymentId(),
                order.getPaymentStatus(),
                order.getStatus(),
                order.getItems().stream()
                        .map(orderItem -> new OrderItemDto(
                                orderItem.getId(),
                                orderItem.getProductId(),
                                orderItem.getQuantity(),
                                orderItem.getPrice(),
                                orderItem.getSubTotal()
                        )).toList(),
                order.getCreatedAt()
        );

    }
}
