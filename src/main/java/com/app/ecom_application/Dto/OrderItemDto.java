package com.app.ecom_application.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OrderItemDto {

    @Id
    private String id;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subTotal;

    public OrderItemDto(String id, Integer quantity, Long productId, BigDecimal price, BigDecimal subTotal) {
        this.id = id;
        this.quantity = quantity;
        this.productId = productId;
        this.price = price;
        this.subTotal = subTotal;
    }
}
