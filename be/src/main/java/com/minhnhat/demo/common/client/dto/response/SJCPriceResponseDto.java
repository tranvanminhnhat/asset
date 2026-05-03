package com.minhnhat.demo.common.client.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class SJCPriceResponseDto {

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("latestDate")
    private String latestDate;

    @JsonProperty("data")
    private List<SJCPriceItemDto> data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SJCPriceItemDto {
        @JsonProperty("Id")
        private Integer id;

        @JsonProperty("TypeName")
        private String typeName;

        @JsonProperty("BranchName")
        private String branchName;

        @JsonProperty("Buy")
        private String buy;

        @JsonProperty("BuyValue")
        private BigDecimal buyValue;

        @JsonProperty("Sell")
        private String sell;

        @JsonProperty("SellValue")
        private BigDecimal sellValue;

        @JsonProperty("BuyDiffer")
        private String buyDiffer;

        @JsonProperty("BuyDifferValue")
        private BigDecimal buyDifferValue;

        @JsonProperty("SellDiffer")
        private String sellDiffer;

        @JsonProperty("SellDifferValue")
        private BigDecimal sellDifferValue;

        @JsonProperty("GroupDate")
        private String groupDate;
    }
}
