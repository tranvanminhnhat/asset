package com.minhnhat.demo.features.portfolio.entity;

import com.minhnhat.demo.common.base.entity.BaseEntity;
import com.minhnhat.demo.features.auth.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "portfolio", indexes = {
        @Index(name = "uk_user_asset", columnList = "user_id,asset_type", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false, updatable = false, length = 36)
    private String userId;

    @Column(name = "asset_type", nullable = false, length = 50)
    private String assetType;

    @Column(name = "quantity", nullable = false, precision = 19, scale = 6)
    private BigDecimal quantity;

    @Column(name = "total_invested", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalInvested;
}

