package com.minhnhat.demo.features.portfolio.entity;

import com.minhnhat.demo.common.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "PORTFOLIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioEntity extends BaseEntity {

    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    private Long id;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "TYPE", nullable = false)
    private String type;

    @Column(name = "PURCHASE_VALUE", nullable = false)
    private BigDecimal purchaseValue;

    @Column(name = "CURRENT_VALUE", nullable = false)
    private BigDecimal currentValue;
}

