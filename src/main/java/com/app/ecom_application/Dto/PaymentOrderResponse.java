package com.app.ecom_application.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderResponse {
    private String orderId;
    private String currency;
    private int amount;
    private String key;
    private String receipt;
    private String status;
    private String message;
}
