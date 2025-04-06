package com.mwaf.orderservice.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter


public class OrderItemRequest {
    private Long productId;
    private Integer quantity;
    // Optionally include the unitPrice if needed; otherwise, fetch from Product Service.

    // Getters and setters

}
