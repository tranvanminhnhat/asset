package com.minhnhat.demo.features.admin.dto.request;

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
public class UpdateAssetPriceRequest {
    private Long id;
    private Long productId;
    private BigDecimal currentPrice;
    private LocalDateTime priceDate;
}

