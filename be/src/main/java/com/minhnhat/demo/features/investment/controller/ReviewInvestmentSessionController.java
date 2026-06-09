package com.minhnhat.demo.features.investment.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.features.investment.dto.response.ReviewSessionResponse;
import com.minhnhat.demo.features.investment.service.ReviewInvestmentSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/investment/{sessionId}/review")
@RequiredArgsConstructor
@Slf4j
public class ReviewInvestmentSessionController {

    private final ReviewInvestmentSessionService service;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewSessionResponse>> reviewSession(
            @PathVariable Long sessionId) {
        try {
            ApiResponse<ReviewSessionResponse> response = service.execute(sessionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error reviewing session", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<ReviewSessionResponse>builder()
                            .data(null)
                            .message("error", "Error: " + e.getMessage(), "InvestmentWorkflow")
                            .build());
        }
    }
}

