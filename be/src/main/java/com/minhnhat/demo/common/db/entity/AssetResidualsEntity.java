package com.minhnhat.demo.common.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "asset_residuals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetResidualsEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "asset_type", nullable = false, length = 50)
    private String assetType;

    @Column(name = "residual_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal residualAmount;
}

