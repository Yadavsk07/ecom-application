package com.app.ecom_application.Dto;

import com.app.ecom_application.Model.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;


@Data
public class OrderResponse {

     private String id;
     private BigDecimal totalAmount;
     private String paymentOrderId;
     private String paymentId;
     private String paymentStatus;
     private OrderStatus status;
     private List<OrderItemDto> items;
     private LocalDateTime createdAt;


     public OrderResponse(
             String id,
             BigDecimal totalAmount,
             String paymentOrderId,
             String paymentId,
             String paymentStatus,
             OrderStatus status,
             List<OrderItemDto> items,
             LocalDateTime createdAt
             ) {

          this.id = id;
          this.totalAmount = totalAmount;
          this.paymentOrderId = paymentOrderId;
          this.paymentId = paymentId;
          this.paymentStatus = paymentStatus;
          this.status = status;
          this.items = items;
          this.createdAt = createdAt;

     }
}
