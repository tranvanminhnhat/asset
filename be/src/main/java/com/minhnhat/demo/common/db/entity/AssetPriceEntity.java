package com.minhnhat.demo.common.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "asset_prices", indexes = {
    @Index(name = "idx_price_product", columnList = "product_id"),
    @Index(name = "idx_price_latest", columnList = "is_latest"),
    @Index(name = "idx_price_date", columnList = "price_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetPriceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "buy_price", nullable = false, precision = 19, scale = 6)
    private BigDecimal buyPrice;

    @Column(name = "sell_price", precision = 19, scale = 6)
    private BigDecimal sellPrice;

    @Column(name = "price_date")
    private LocalDateTime priceDate;

    @Column(name = "is_latest")
    private Boolean isLatest;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
