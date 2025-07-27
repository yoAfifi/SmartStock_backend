package com.mwaf.productservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CategoryRequest {
    
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
    private String name;
    
    @NotBlank(message = "Category slug is required")
    @Size(min = 2, max = 50, message = "Category slug must be between 2 and 50 characters")
    private String slug;
} 