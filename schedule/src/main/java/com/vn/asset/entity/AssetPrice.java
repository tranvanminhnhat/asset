package com.vn.asset.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "asset_prices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "current_price", nullable = false, precision = 19, scale = 6)
    private BigDecimal currentPrice;

    @Column(name = "price_date")
    private LocalDateTime priceDate;

    @Builder.Default
    @Column(name = "is_latest")
    private Boolean isLatest = true;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "asset_type", nullable = false, length = 50)
    private String assetType;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (priceDate == null) {
            priceDate = LocalDateTime.now();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

