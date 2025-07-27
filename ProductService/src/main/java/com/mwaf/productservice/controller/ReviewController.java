package com.mwaf.productservice.controller;

import com.mwaf.productservice.dto.ReviewRequest;
import com.mwaf.productservice.dto.ReviewSummaryResponse;
import com.mwaf.productservice.model.ProductReview;
import com.mwaf.productservice.service.ReviewService;
import com.mwaf.productservice.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtUtil jwtUtil;

    public ReviewController(ReviewService reviewService, JwtUtil jwtUtil) {
        this.reviewService = reviewService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductReview> createOrUpdateReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewRequest request,
            HttpServletRequest httpRequest) {
        
        String token = extractToken(httpRequest);
        Long authorUserId = Long.parseLong(jwtUtil.getUserId(token));
        
        ProductReview review = reviewService.createOrUpdateReview(productId, authorUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    @GetMapping
    public ResponseEntity<Page<ProductReview>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ProductReview> reviews = reviewService.getProductReviews(productId, pageRequest);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/summary")
    public ResponseEntity<ReviewSummaryResponse> getReviewSummary(@PathVariable Long productId) {
        ReviewSummaryResponse summary = reviewService.getReviewSummary(productId);
        return ResponseEntity.ok(summary);
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId,
            HttpServletRequest httpRequest) {
        
        String token = extractToken(httpRequest);
        Long authorUserId = Long.parseLong(jwtUtil.getUserId(token));
        
        reviewService.deleteReview(productId, reviewId, authorUserId);
        return ResponseEntity.noContent().build();
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("No valid JWT token found");
    }
} 