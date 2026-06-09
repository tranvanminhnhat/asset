package com.minhnhat.demo.features.investment.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.features.investment.dto.response.CompleteAllocationResponse;
import com.minhnhat.demo.features.investment.service.CompleteAllocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/investment/{sessionId}/allocation/{allocationId}/complete")
@RequiredArgsConstructor
@Slf4j
public class CompleteAllocationController {

    private final CompleteAllocationService service;

    @PostMapping
    public ResponseEntity<ApiResponse<CompleteAllocationResponse>> completeAllocation(
            @PathVariable Long sessionId,
            @PathVariable Long allocationId) {
        try {
            ApiResponse<CompleteAllocationResponse> response = service.execute(sessionId, allocationId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error completing allocation", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<CompleteAllocationResponse>builder()
                            .data(null)
                            .message("error", "Error: " + e.getMessage(), "InvestmentWorkflow")
                            .build());
        }
    }
}


