package com.minhnhat.demo.features.investment.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelectAssetRequest {
    private String assetCode;
    private BigDecimal quantity;
}

