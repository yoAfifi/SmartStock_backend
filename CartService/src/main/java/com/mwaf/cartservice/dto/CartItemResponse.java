package com.mwaf.cartservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String imageUrl;
    private Integer quantity;
    private Integer stockQuantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
} 