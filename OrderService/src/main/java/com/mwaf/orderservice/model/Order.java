package com.mwaf.orderservice.model;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends Auditable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // For example, the customer who placed the order
    private Long customerId;

    // The date/time when the order was placed
    private LocalDateTime orderDate;

    // Order status: e.g., "NEW", "PROCESSING", "COMPLETED", etc.
    private String status;

    // Total amount for the order
    private BigDecimal totalAmount;

    // One-to-many relationship with OrderItem
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> orderItems;
}
