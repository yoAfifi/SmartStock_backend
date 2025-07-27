package com.mwaf.productservice.repository;

import com.mwaf.productservice.model.Product;
import com.mwaf.productservice.model.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    
    Page<ProductReview> findByProductOrderByCreatedDateDesc(Product product, Pageable pageable);
    
    Optional<ProductReview> findByProductAndAuthorUserId(Product product, Long authorUserId);
    
    @Query("SELECT AVG(r.rating) FROM ProductReview r WHERE r.product = :product")
    Double getAverageRatingByProduct(@Param("product") Product product);
    
    @Query("SELECT COUNT(r) FROM ProductReview r WHERE r.product = :product")
    Long getReviewCountByProduct(@Param("product") Product product);
    
    @Query("SELECT r.rating, COUNT(r) FROM ProductReview r WHERE r.product = :product GROUP BY r.rating ORDER BY r.rating")
    List<Object[]> getRatingBreakdownByProduct(@Param("product") Product product);
} 