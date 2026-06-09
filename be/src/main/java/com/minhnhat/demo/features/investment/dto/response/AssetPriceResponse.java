package com.minhnhat.demo.features.investment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetPriceResponse {
    // Legacy fields for backward compatibility
    private String assetCode;
    private String assetType;
    private String assetName;

    // New fields
    private Long id;
    private Long productId;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    private BigDecimal buyPrice;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    private BigDecimal sellPrice;
    private LocalDateTime priceDate;
    private Boolean isLatest;
    private Boolean isActive;
}

