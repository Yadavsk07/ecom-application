package com.app.ecom_application.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

import com.app.ecom_application.Dto.PaymentOrderResponse;
import com.app.ecom_application.Dto.PaymentVerificationRequest;
import com.app.ecom_application.Service.RazorpayService;
import com.app.ecom_application.Service.CartService;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class PaymentController {

    private final RazorpayService razorpayService;
    private final CartService cartService;

    public PaymentController(RazorpayService razorpayService, CartService cartService) {
        this.razorpayService = razorpayService;
        this.cartService = cartService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<PaymentOrderResponse> createOrder(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "INR") String currency,
            @RequestParam(required = false) String receipt) {

        long amount = cartService.getCartTotal(userId).movePointRight(2).longValueExact();
        if (amount <= 0) {
            return ResponseEntity.badRequest().build();
        }
        PaymentOrderResponse response = razorpayService.createOrder(amount, currency, receipt);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyPayment(@RequestBody PaymentVerificationRequest request) {
        boolean valid = razorpayService.verifySignature(request.getOrderId(), request.getPaymentId(), request.getSignature());
        Map<String, Object> payload = new HashMap<>();
        payload.put("valid", valid);
        payload.put("orderId", request.getOrderId());
        payload.put("paymentId", request.getPaymentId());
        payload.put("status", valid ? "paid" : "failed");
        payload.put("message", valid ? "Payment verified successfully." : "Payment verification failed.");
        return valid ? ResponseEntity.ok(payload) : ResponseEntity.badRequest().body(payload);
    }
}
