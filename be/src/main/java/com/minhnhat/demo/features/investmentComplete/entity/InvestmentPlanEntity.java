package com.minhnhat.demo.features.investmentComplete.entity;

import com.minhnhat.demo.common.base.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Entity
@Table(name = "investment_plan")
@Getter
@Setter
@NoArgsConstructor
public class InvestmentPlanEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "total_amount", precision = 18, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "status", length = 30, nullable = false)
    private String status;

    @Column(name = "recommendation_snapshot", columnDefinition = "JSON")
    private String recommendationSnapshot;

    @Column(name = "note", length = 500)
    private String note;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    // allocations are stored in separate table `investment_plan_allocation` and linked by plan_id (no JPA relationship configured)
}

