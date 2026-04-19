package com.minhnhat.demo.features.portfolio.entity;

import com.minhnhat.demo.common.base.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Portfolio Transaction Entity (Append-Only)
 * Matches schema: portfolio_transactions table
 * This table records all portfolio transactions with immutable state.
 * No updates or deletes allowed - only inserts for audit trail.
 */
@Entity
@Table(name = "portfolio_transactions", indexes = {
        @Index(name = "idx_tx_user", columnList = "user_id"),
        @Index(name = "idx_tx_asset", columnList = "asset_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioTransactionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false, updatable = false, length = 36)
    private String userId;

    @Column(name = "asset_type", nullable = false, updatable = false, length = 50)
    private String assetType;

    @Column(name = "transaction_type", nullable = false, updatable = false, length = 20)
    private String transactionType; // BUY, SELL, DIVIDEND, ADJUSTMENT

    @Column(name = "quantity", precision = 19, scale = 6)
    private BigDecimal quantity;

    @Column(name = "price", precision = 19, scale = 6)
    private BigDecimal price;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2, updatable = false)
    private BigDecimal totalAmount;

    @Column(name = "fee", precision = 19, scale = 2)
    private BigDecimal fee;

    @Column(name = "tax", precision = 19, scale = 2)
    private BigDecimal tax;

    @Column(name = "reference_id", length = 100)
    private String referenceId;

    @Column(name = "note", length = 255)
    private String note;
}

