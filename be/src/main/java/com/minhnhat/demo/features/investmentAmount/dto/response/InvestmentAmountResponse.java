package com.minhnhat.demo.features.investmentAmount.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentAmountResponse {

    private List<Allocation> allocations;

    // Loại tài sản (ví dụ: "EMERGENCY", "GOLD", "ETF", "STOCK")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Allocation {
        private String type;

        // Số tiền phân bổ (theo tiền tệ)
        private BigDecimal amount;

        // Tiền dư kỳ trước
        private BigDecimal carryOverAmount;

        // Số tiền có thể đầu tư thực tế (sau khi trừ lot size)
        private BigDecimal investableAmount;

        // Số tiền dư lại sau khi đầu tư
        private BigDecimal residualAmount;

        // Tỷ lệ phần trăm (dạng thập phân, ví dụ: 0.25 = 25%)
        private BigDecimal percent;
    }
}

