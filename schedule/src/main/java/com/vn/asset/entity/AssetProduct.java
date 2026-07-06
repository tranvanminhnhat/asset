package com.vn.asset.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "asset_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "platform_id", nullable = false)
    private Long platformId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "minimum_quantity")
    private BigDecimal minimumQuantity;

    @Column(name = "unit_name", length = 50)
    private String unitName;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

