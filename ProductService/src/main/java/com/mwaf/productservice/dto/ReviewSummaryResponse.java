package com.mwaf.productservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter
public class ReviewSummaryResponse {
    private Long count;
    private BigDecimal averageRating;
    private Map<Integer, Long> breakdown;
    
    public ReviewSummaryResponse(Long count, BigDecimal averageRating, Map<Integer, Long> breakdown) {
        this.count = count;
        this.averageRating = averageRating;
        this.breakdown = breakdown;
    }
} 