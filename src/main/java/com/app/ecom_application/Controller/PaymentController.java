package com.app.ecom_application.Controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.app.ecom_application.Dto.CartValidationResult;
import com.app.ecom_application.Dto.PaymentOrderResponse;
import com.app.ecom_application.Dto.PaymentVerificationRequest;
import com.app.ecom_application.Service.CartService;
import com.app.ecom_application.Service.RazorpayService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://127.0.0.1:5173"
})
public class PaymentController {

    private final RazorpayService razorpayService;
    private final CartService cartService;

    public PaymentController(
            RazorpayService razorpayService,
            CartService cartService) {

        this.razorpayService = razorpayService;
        this.cartService = cartService;
    }


    // =========================================================
    // CREATE RAZORPAY ORDER
    // =========================================================

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "INR") String currency,
            @RequestParam(required = false) String receipt) {

        // -----------------------------------------------------
        // 1. Validate currency
        // -----------------------------------------------------

        if (!"INR".equalsIgnoreCase(currency)) {

            return ResponseEntity
                    .badRequest()
                    .body("Only INR currency is supported");
        }


        // -----------------------------------------------------
        // 2. Validate cart
        // -----------------------------------------------------

        CartValidationResult validation =
                cartService.validateCartForCheckout(userId);

        if (!validation.isValid()) {

            return ResponseEntity
                    .badRequest()
                    .body(validation.getMessage());
        }


        // -----------------------------------------------------
        // 3. Get validated amount
        // -----------------------------------------------------

        BigDecimal amount =
                validation.getTotalAmount();

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            return ResponseEntity
                    .badRequest()
                    .body("Invalid payment amount");
        }


        // -----------------------------------------------------
        // 4. Generate receipt if not provided
        // -----------------------------------------------------

        if (receipt == null || receipt.isBlank()) {

            receipt = "ecom-"
                    + userId
                    + "-"
                    + System.currentTimeMillis();
        }


        // -----------------------------------------------------
        // 5. Create Razorpay order
        // -----------------------------------------------------

        PaymentOrderResponse response =
                razorpayService.createOrder(
                        amount,
                        currency.toUpperCase(),
                        receipt
                );

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // VERIFY RAZORPAY PAYMENT
    // =========================================================

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyPayment(
            @RequestAttribute("userId") Long userId,
            @RequestBody PaymentVerificationRequest request) {

        boolean valid =
                razorpayService.verifySignature(
                        request.getOrderId(),
                        request.getPaymentId(),
                        request.getSignature()
                );


        // -----------------------------------------------------
        // Prepare response
        // -----------------------------------------------------

        Map<String, Object> payload =
                new HashMap<>();

        payload.put("valid", valid);
        payload.put("orderId", request.getOrderId());
        payload.put("paymentId", request.getPaymentId());
        payload.put("userId", userId);

        payload.put(
                "status",
                valid ? "paid" : "failed"
        );

        payload.put(
                "message",
                valid
                        ? "Payment verified successfully."
                        : "Payment verification failed."
        );


        // -----------------------------------------------------
        // Return response
        // -----------------------------------------------------

        if (valid) {
            return ResponseEntity.ok(payload);
        }

        return ResponseEntity
                .badRequest()
                .body(payload);
    }
}