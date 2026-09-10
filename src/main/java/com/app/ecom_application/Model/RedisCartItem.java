package com.app.ecom_application.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisCartItem {

    private Long productId;

    private Integer quantity;

    private BigDecimal unitPrice;
}