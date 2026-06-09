package com.minhnhat.demo.common.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "investment_session_allocations", indexes = {
    @Index(name = "idx_session", columnList = "session_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentSessionAllocationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false, insertable = false, updatable = false)
    private InvestmentSessionEntity session;

    @Column(name = "asset_type", length = 50, nullable = false)
    private String assetType;

    @Column(name = "recommended_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal recommendedAmount;

    @Column(name = "percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(name = "status", length = 30, nullable = false)
    private String status;

    @Column(name = "invested_amount", precision = 19, scale = 2)
    private BigDecimal investedAmount;

    @Column(name = "residual_amount", precision = 19, scale = 2)
    private BigDecimal residualAmount;

    @Builder.Default
    @OneToMany(mappedBy = "allocation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<InvestmentAssetSelectionEntity> selections = new ArrayList<>();
}

