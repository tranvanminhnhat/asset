package com.minhnhat.demo.features.investmentComplete.entity;

import com.minhnhat.demo.common.base.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "investment_plan_allocations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentPlanAllocationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "plan_id", nullable = false, updatable = false)
    private Long planId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false, updatable = false, insertable = false)
    private InvestmentPlanEntity plan;

    @Column(name = "asset_type", length = 50, nullable = false)
    private String assetType;

    @Column(name = "amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "quantity", precision = 19, scale = 6)
    private Integer quantity;

    @Column(name = "percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal percentage;
}

