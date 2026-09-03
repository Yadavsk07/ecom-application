package com.app.ecom_application.Dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;

@JsonPropertyOrder({
        "id",
        "name",
        "description",
        "price",
        "stockQuantity",
        "category",
        "imageUrl",
        "active"
})
@Data
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String category;
    private String imageUrl;
    private Boolean active;
}
