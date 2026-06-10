package com.minhnhat.demo.features.investment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinalizeSessionResponse {
    private Long sessionId;
    private String status;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    private BigDecimal totalInvested;
    private LocalDateTime completedAt;
    
    // Detailed allocation information
    private List<AllocationDetail> allocations;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllocationDetail {
        private Long allocationId;
        private String assetType;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
        private BigDecimal recommendedAmount;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
        private BigDecimal investedAmount;
        private String status;
    }
}

