package com.app.ecom_application.Controller;

import com.app.ecom_application.Dto.OrderResponse;
import com.app.ecom_application.Service.OrderService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import com.app.ecom_application.Dto.PaymentVerificationRequest;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Data
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody PaymentVerificationRequest paymentRequest)
    {
        return orderService.createOrder(userId, paymentRequest)
                .map(orderResponse -> new ResponseEntity<>(orderResponse , HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrderHistory(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(orderService.getOrdersForUser(userId));
    }
}
