package com.mwaf.productservice.controller;

import com.mwaf.productservice.model.AdminNotification;
import com.mwaf.productservice.service.AdminNotificationService;
import com.mwaf.productservice.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/notifications")
@PreAuthorize("hasRole('ADMIN')")
public class AdminNotificationController {

    private final AdminNotificationService notificationService;
    private final ProductService productService;

    public AdminNotificationController(AdminNotificationService notificationService, ProductService productService) {
        this.notificationService = notificationService;
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Page<AdminNotification>> getNotifications(
            @RequestParam(required = false) Boolean read,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<AdminNotification> notifications = notificationService.getNotifications(read, pageRequest);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getUnreadCount() {
        Long count = notificationService.getUnreadCount();
        return ResponseEntity.ok(count);
    }

    @PostMapping("/check-low-stock")
    public ResponseEntity<String> checkLowStockForAllProducts() {
        try {
            notificationService.checkLowStockForAllProducts();
            return ResponseEntity.ok("Low stock check completed successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error checking low stock: " + e.getMessage());
        }
    }

    // Temporary test endpoint without authentication
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        try {
            long unreadCount = notificationService.getUnreadCount();
            return ResponseEntity.ok("Notification controller is working! Current unread count: " + unreadCount);
        } catch (Exception e) {
            return ResponseEntity.ok("Notification controller is working! Error getting unread count: " + e.getMessage());
        }
    }

} 