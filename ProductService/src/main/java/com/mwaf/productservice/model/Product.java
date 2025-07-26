package com.mwaf.productservice.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "products")
@Getter @Setter
public class Product extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private BigDecimal price;

    // Represents the available quantity in stock.
    @JsonProperty("stock_quantity")
    private Integer stockQuantity;
    @Column(name = "image_url", length = 512)   // generous length
    private String imageUrl;
    @OneToMany(mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();
}
