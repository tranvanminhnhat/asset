package com.minhnhat.demo.features.investment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelectAssetResponse {
    private Long selectionId;
    private Long allocationId;
    private String assetCode;
    private String assetName;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    private BigDecimal quantity;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    private BigDecimal buyPrice;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    private BigDecimal investedAmount;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    private BigDecimal residualAmount;
    private LocalDateTime createdAt;
}

