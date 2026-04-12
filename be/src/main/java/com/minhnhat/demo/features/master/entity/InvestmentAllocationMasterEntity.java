package com.minhnhat.demo.features.master.entity;

import com.minhnhat.demo.common.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "investment_allocation_master")
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class InvestmentAllocationMasterEntity extends BaseEntity {

	@Id
	@Column(name = "code", length = 30)
	private String code;

	@Column(name = "stock_percentage", nullable = false, precision = 5, scale = 2)
	private BigDecimal stockPercent;

	@Column(name = "emergency_percentage", nullable = false, precision = 5, scale = 2)
	private BigDecimal emergencyPercent;

	@Column(name = "gold_percentage", nullable = false, precision = 5, scale = 2)
	private BigDecimal goldPercent;

	@Column(name = "etf_percentage", nullable = false, precision = 5, scale = 2)
	private BigDecimal etfPercent;
}
