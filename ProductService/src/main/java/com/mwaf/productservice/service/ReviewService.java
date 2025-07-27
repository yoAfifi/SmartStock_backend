package com.mwaf.productservice.service;

import com.mwaf.productservice.dto.ReviewRequest;
import com.mwaf.productservice.dto.ReviewSummaryResponse;
import com.mwaf.productservice.model.Product;
import com.mwaf.productservice.model.ProductReview;
import com.mwaf.productservice.repository.ProductRepository;
import com.mwaf.productservice.repository.ProductReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ReviewService(ProductReviewRepository reviewRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductReview createOrUpdateReview(Long productId, Long authorUserId, ReviewRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        // Check if user already has a review for this product
        ProductReview existingReview = reviewRepository.findByProductAndAuthorUserId(product, authorUserId)
                .orElse(null);

        if (existingReview != null) {
            // Update existing review
            existingReview.setRating(request.getRating());
            existingReview.setTitle(request.getTitle());
            existingReview.setComment(request.getComment());
            ProductReview savedReview = reviewRepository.save(existingReview);
            updateProductAverageRating(product);
            return savedReview;
        } else {
            // Create new review
            ProductReview newReview = new ProductReview();
            newReview.setProduct(product);
            newReview.setAuthorUserId(authorUserId);
            newReview.setRating(request.getRating());
            newReview.setTitle(request.getTitle());
            newReview.setComment(request.getComment());
            
            ProductReview savedReview = reviewRepository.save(newReview);
            updateProductAverageRating(product);
            return savedReview;
        }
    }

    public Page<ProductReview> getProductReviews(Long productId, Pageable pageable) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        return reviewRepository.findByProductOrderByCreatedDateDesc(product, pageable);
    }

    public ReviewSummaryResponse getReviewSummary(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        Long count = reviewRepository.getReviewCountByProduct(product);
        Double avgRating = reviewRepository.getAverageRatingByProduct(product);
        BigDecimal averageRating = avgRating != null ? 
                BigDecimal.valueOf(avgRating).setScale(1, RoundingMode.HALF_UP) : 
                BigDecimal.ZERO;

        // Get rating breakdown
        Map<Integer, Long> breakdown = new HashMap<>();
        List<Object[]> breakdownData = reviewRepository.getRatingBreakdownByProduct(product);
        
        for (Object[] data : breakdownData) {
            Integer rating = (Integer) data[0];
            Long countForRating = (Long) data[1];
            breakdown.put(rating, countForRating);
        }

        return new ReviewSummaryResponse(count, averageRating, breakdown);
    }

    @Transactional
    public void deleteReview(Long productId, Long reviewId, Long authorUserId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));

        // Check if the review belongs to the user
        if (!review.getAuthorUserId().equals(authorUserId)) {
            throw new RuntimeException("You can only delete your own reviews");
        }

        // Check if the review belongs to the specified product
        if (!review.getProduct().getId().equals(productId)) {
            throw new RuntimeException("Review does not belong to the specified product");
        }

        reviewRepository.delete(review);
        updateProductAverageRating(product);
    }

    private void updateProductAverageRating(Product product) {
        Double avgRating = reviewRepository.getAverageRatingByProduct(product);
        if (avgRating != null) {
            product.setAverageRating(BigDecimal.valueOf(avgRating).setScale(1, RoundingMode.HALF_UP));
        } else {
            product.setAverageRating(null);
        }
        productRepository.save(product);
    }
} 