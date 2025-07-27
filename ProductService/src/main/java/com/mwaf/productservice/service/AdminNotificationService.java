package com.mwaf.productservice.service;

import com.mwaf.productservice.model.AdminNotification;
import com.mwaf.productservice.model.Product;
import com.mwaf.productservice.repository.AdminNotificationRepository;
import com.mwaf.productservice.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(AdminNotificationService.class);

    private final AdminNotificationRepository notificationRepository;
    private final ProductRepository productRepository;
    
    @Value("${product.lowstock.threshold:5}")
    private int lowStockThreshold;

    public AdminNotificationService(AdminNotificationRepository notificationRepository, ProductRepository productRepository) {
        this.notificationRepository = notificationRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void checkLowStockAndNotify(Product product) {
        logger.info("Checking low stock for product: {} (ID: {}) with quantity: {}", 
                   product.getName(), product.getId(), product.getStockQuantity());
        
        if (product.getStockQuantity() < lowStockThreshold) {
            logger.info("Product {} has low stock ({} < {}), checking for existing notifications", 
                       product.getName(), product.getStockQuantity(), lowStockThreshold);
            
            // Check if there's already an unread notification for this product in the last 24h
            LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
            Long existingNotifications = notificationRepository.countUnreadLowStockNotificationsSince(product, twentyFourHoursAgo);
            
            logger.info("Found {} existing notifications for product {} in the last 24 hours", 
                       existingNotifications, product.getName());
            
            if (existingNotifications == 0) {
                logger.info("Creating new notification for product: {}", product.getName());
                
                // Create new notification
                AdminNotification notification = new AdminNotification();
                notification.setType(AdminNotification.NotificationType.LOW_STOCK);
                notification.setProduct(product);
                notification.setCurrentStock(product.getStockQuantity());
                notification.setLevel(AdminNotification.NotificationLevel.WARN);
                notification.setMessage(String.format("Product '%s' (ID: %d) has low stock: %d units remaining", 
                        product.getName(), product.getId(), product.getStockQuantity()));
                notification.setRead(false);
                
                AdminNotification savedNotification = notificationRepository.save(notification);
                logger.info("Successfully created notification with ID: {}", savedNotification.getId());
            } else {
                logger.info("Skipping notification creation - existing notification found for product: {}", product.getName());
            }
        } else {
            logger.info("Product {} has sufficient stock ({} >= {}), no notification needed", 
                       product.getName(), product.getStockQuantity(), lowStockThreshold);
        }
    }

    @Transactional
    public void checkLowStockForAllProducts() {
        logger.info("Starting low stock check for all products with threshold: {}", lowStockThreshold);
        
        // Get all products with stock less than threshold
        List<Product> lowStockProducts = productRepository.findByStockQuantityLessThan(lowStockThreshold);
        
        logger.info("Found {} products with low stock", lowStockProducts.size());
        
        int notificationsCreated = 0;
        for (Product product : lowStockProducts) {
            try {
                checkLowStockAndNotify(product);
                notificationsCreated++;
            } catch (Exception e) {
                logger.error("Error checking low stock for product {} (ID: {}): {}", 
                           product.getName(), product.getId(), e.getMessage());
            }
        }
        
        logger.info("Low stock check completed. Created {} new notifications", notificationsCreated);
    }

    public Page<AdminNotification> getNotifications(Boolean read, Pageable pageable) {
        return notificationRepository.findByReadOrderByCreatedDateDesc(read, pageable);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        AdminNotification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));
        
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public Long getUnreadCount() {
        return notificationRepository.countUnreadNotifications();
    }
} 