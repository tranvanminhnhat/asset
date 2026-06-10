package com.minhnhat.demo.features.investment.dto.request;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinalizeSessionRequest {
    private List<FinalizeAllocationItem> allocations;
}
