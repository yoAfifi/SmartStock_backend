package com.mwaf.orderservice.dto;





import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO
{

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    @JsonProperty("stock_quantity")
    private Integer stockQuantity;



}