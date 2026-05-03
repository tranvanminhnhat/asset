package com.minhnhat.demo.features.master.entity;

import com.minhnhat.demo.common.base.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "mst_allocation_strategy_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllocationStrategyDetailEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "strategy_id", nullable = false)
    private Long strategyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_id", nullable = false, insertable = false, updatable = false)
    private AllocationStrategyEntity strategy;

    @Column(name = "asset_type", length = 50, nullable = false)
    private String assetType;

    @Column(name = "percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal percentage;

    @Column(name = "strategy_code", length = 45, nullable = false)
    private String strategyCode;

    @Column(name = "minimum_lot_size_amount", precision = 19, scale = 6, nullable = false)
    private BigDecimal minimumLotSizeAmount;
}

