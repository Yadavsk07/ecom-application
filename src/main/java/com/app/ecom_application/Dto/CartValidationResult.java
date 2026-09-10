package com.app.ecom_application.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartValidationResult {

    private boolean valid;

    private String message;

    private BigDecimal totalAmount;
}