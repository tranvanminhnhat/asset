package com.minhnhat.demo.features.investment.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelectAssetRequest {
    private Long productId;           // Product ID from FE selection
    private String assetType;         // Asset type/category code (e.g., GOLD, STOCK)
    private String assetCode;         // Legacy support: product code
    private BigDecimal quantity;      // Quantity to invest
}

