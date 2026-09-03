package com.app.ecom_application.Dto;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CartItemResponse {


    private String id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
    private UserResponse user;
    private ProductResponse product;

}
