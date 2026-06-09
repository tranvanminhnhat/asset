package com.minhnhat.demo.features.investment.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.features.investment.dto.request.SelectAssetRequest;
import com.minhnhat.demo.features.investment.dto.response.SelectAssetResponse;
import com.minhnhat.demo.features.investment.service.SelectAssetForAllocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/investment/{sessionId}/allocation/{allocationId}/select")
@RequiredArgsConstructor
@Slf4j
public class SelectAssetForAllocationController {

    private final SelectAssetForAllocationService service;

    @PostMapping
    public ResponseEntity<ApiResponse<SelectAssetResponse>> selectAsset(
            @PathVariable Long sessionId,
            @PathVariable Long allocationId,
            @RequestBody SelectAssetRequest request) {
        try {
            ApiResponse<SelectAssetResponse> response = service.execute(sessionId, allocationId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error selecting asset", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<SelectAssetResponse>builder()
                            .data(null)
                            .message("error", "Error: " + e.getMessage(), "InvestmentWorkflow")
                            .build());
        }
    }
}


