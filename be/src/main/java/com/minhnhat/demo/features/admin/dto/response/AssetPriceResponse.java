package com.minhnhat.demo.features.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetPriceResponse {
    private Long id;
    private Long productId;
    private BigDecimal buyPrice;
    private BigDecimal sellPrice;
    private LocalDateTime priceDate;
    private Boolean isLatest;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

