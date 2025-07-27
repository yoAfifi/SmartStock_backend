package com.mwaf.productservice.repository;

import com.mwaf.productservice.model.AdminNotification;
import com.mwaf.productservice.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AdminNotificationRepository extends JpaRepository<AdminNotification, Long> {
    
    Page<AdminNotification> findByReadOrderByCreatedDateDesc(Boolean read, Pageable pageable);
    
    @Query("SELECT COUNT(n) FROM AdminNotification n WHERE n.read = false")
    Long countUnreadNotifications();
    
    @Query("SELECT COUNT(n) FROM AdminNotification n WHERE n.product = :product AND n.type = 'LOW_STOCK' AND n.read = false AND n.createdDate > :since")
    Long countUnreadLowStockNotificationsSince(@Param("product") Product product, @Param("since") LocalDateTime since);
} 