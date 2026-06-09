package com.minhnhat.demo.features.investment.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.features.investment.dto.response.InvestmentRecommendationResponse;
import com.minhnhat.demo.features.investment.service.GetInvestmentRecommendationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/investment/{sessionId}/recommendations")
@RequiredArgsConstructor
@Slf4j
public class GetInvestmentRecommendationsController {

    private final GetInvestmentRecommendationsService service;

    @GetMapping
    public ResponseEntity<ApiResponse<InvestmentRecommendationResponse>> getRecommendations(
            @PathVariable Long sessionId) {
        try {
            ApiResponse<InvestmentRecommendationResponse> response = service.execute(sessionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching recommendations", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<InvestmentRecommendationResponse>builder()
                            .data(null)
                            .message("error", "Error: " + e.getMessage(), "InvestmentWorkflow")
                            .build());
        }
    }
}


