package com.minhnhat.demo.common.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "asset_prices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetPriceEntity {

    @Id
    @Column(name = "asset_type", length = 50, nullable = false)
    private String assetType;

    @Column(name = "buy_price", nullable = false, precision = 19, scale = 6)
    private BigDecimal buyPrice;

    @Column(name = "sell_price", precision = 10, scale = 0)
    private BigDecimal sellPrice;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
