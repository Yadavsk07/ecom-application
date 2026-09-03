package com.app.ecom_application.Dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class PaymentVerificationRequest {
    @NotBlank
    private String orderId;
    @NotBlank
    private String paymentId;
    @NotBlank
    private String signature;
    private String receipt;
    private String status;
}
