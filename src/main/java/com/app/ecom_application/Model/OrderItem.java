package com.app.ecom_application.Model;

import com.app.ecom_application.Dto.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    private String id;

    private Long productId;

    private ProductResponse product;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal subTotal;
}