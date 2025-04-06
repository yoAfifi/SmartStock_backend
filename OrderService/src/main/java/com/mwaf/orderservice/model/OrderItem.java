package com.mwaf.orderservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mwaf.orderservice.model.Order;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter @Setter
public class OrderItem extends Auditable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    private Long productId;

    private Integer quantity;

    private BigDecimal unitPrice;
}
