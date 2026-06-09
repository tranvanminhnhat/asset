package com.minhnhat.demo.features.investment.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.features.investment.dto.response.FinalizeSessionResponse;
import com.minhnhat.demo.features.investment.service.FinalizeInvestmentSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/investment/{sessionId}/finalize")
@RequiredArgsConstructor
@Slf4j
public class FinalizeInvestmentSessionController {

    private final FinalizeInvestmentSessionService service;

    @PostMapping
    public ResponseEntity<ApiResponse<FinalizeSessionResponse>> finalizeSession(
            @PathVariable Long sessionId) {
        try {
            ApiResponse<FinalizeSessionResponse> response = service.execute(sessionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error finalizing session", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<FinalizeSessionResponse>builder()
                            .data(null)
                            .message("error", "Error: " + e.getMessage(), "InvestmentWorkflow")
                            .build());
        }
    }
}

