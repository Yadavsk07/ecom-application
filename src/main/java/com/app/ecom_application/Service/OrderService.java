package com.app.ecom_application.Service;

import com.app.ecom_application.Dto.OrderResponse;
import com.app.ecom_application.Dto.PaymentVerificationRequest;
import com.app.ecom_application.Model.Order;
import com.app.ecom_application.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final RazorpayService razorpayService;
    private final OrderRepository orderRepository;
    private final OrderTransactionService orderTransactionService;
    private final CartService cartService;


    // =========================================================
    // CREATE ORDER
    // =========================================================

    public Optional<OrderResponse> createOrder(
            Long userId,
            PaymentVerificationRequest paymentRequest) {


        // =====================================================
        // 1. VERIFY RAZORPAY PAYMENT
        // =====================================================

        boolean signatureValid =
                razorpayService.verifySignature(
                        paymentRequest.getOrderId(),
                        paymentRequest.getPaymentId(),
                        paymentRequest.getSignature()
                );

        if (!signatureValid) {
            return Optional.empty();
        }


        // =====================================================
        // 2. CHECK DUPLICATE PAYMENT
        // =====================================================

        if (orderRepository.existsByPaymentOrderId(
                paymentRequest.getOrderId())) {

            return Optional.empty();
        }


        // =====================================================
        // 3. EXECUTE MONGODB TRANSACTION
        // =====================================================

        Optional<OrderResponse> response =
                orderTransactionService
                        .createOrderTransactionally(
                                userId,
                                paymentRequest.getOrderId(),
                                paymentRequest.getPaymentId()
                        );


        // =====================================================
        // 4. CLEAR REDIS ONLY AFTER TRANSACTION SUCCESS
        // =====================================================

        if (response.isPresent()) {

            // MongoDB transaction has already committed.
            // Redis is not part of the MongoDB transaction.

            // We need CartService here.
            cartService.clearRedisCart(userId);
        }

        return response;
    }


    // =========================================================
    // GET USER ORDERS
    // =========================================================

    public List<OrderResponse> getOrdersForUser(
            Long userId) {

        return orderRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
    }


    // =========================================================
    // MAP ORDER
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
                                new com.app.ecom_application.Dto.OrderItemDto(

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