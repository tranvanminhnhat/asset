package com.minhnhat.demo.features.investmentComplete.entity;

import com.minhnhat.demo.common.base.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "investment_plan_allocation")
@Getter
@Setter
@NoArgsConstructor
public class InvestmentPlanAllocationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(name = "allocation_type", length = 50, nullable = false)
    private String allocationType;

    @Column(name = "amount", precision = 18, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "percent", precision = 10, scale = 4, nullable = false)
    private BigDecimal percent;

    @Column(name = "display_order")
    private Integer displayOrder;
}

