package com.mwaf.productservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "admin_notifications")
@Getter @Setter
public class AdminNotification extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "current_stock", nullable = false)
    private Integer currentStock;

    @Column(name = "message", length = 500, nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private NotificationLevel level;

    @Column(name = "read", nullable = false)
    private Boolean read = false;

    public enum NotificationType {
        LOW_STOCK
    }

    public enum NotificationLevel {
        INFO, WARN, ERROR
    }
} 