package com.minhnhat.demo.common.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "investment_asset_selections", indexes = {
    @Index(name = "idx_session", columnList = "session_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentAssetSelectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "allocation_id", nullable = false)
    private Long allocationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allocation_id", nullable = false, insertable = false, updatable = false)
    private InvestmentSessionAllocationEntity allocation;

    @Column(name = "asset_code", length = 50, nullable = false)
    private String assetCode;

    @Column(name = "asset_name", length = 255)
    private String assetName;

    @Column(name = "quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    @Column(name = "buy_price", nullable = false, precision = 19, scale = 6)
    private BigDecimal buyPrice;

    @Column(name = "invested_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal investedAmount;

    // New fields for product hierarchy
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "platform_code", length = 50)
    private String platformCode;

    @Column(name = "product_code", length = 50)
    private String productCode;

    @Column(name = "product_name", length = 255)
    private String productName;

    @Column(name = "category_code", length = 50)
    private String categoryCode;

    @Column(name = "unit_name", length = 50)
    private String unitName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
    }
}

