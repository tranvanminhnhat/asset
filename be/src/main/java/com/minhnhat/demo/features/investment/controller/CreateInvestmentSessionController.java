package com.minhnhat.demo.features.investment.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.features.investment.dto.request.CreateInvestmentSessionRequest;
import com.minhnhat.demo.features.investment.dto.response.CreateInvestmentSessionResponse;
import com.minhnhat.demo.features.investment.service.CreateInvestmentSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/investment/session")
@RequiredArgsConstructor
@Slf4j
public class CreateInvestmentSessionController {

    private final CreateInvestmentSessionService service;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateInvestmentSessionResponse>> createSession(
            @RequestBody CreateInvestmentSessionRequest request) {
        try {
            ApiResponse<CreateInvestmentSessionResponse> response = service.execute(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating investment session", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<CreateInvestmentSessionResponse>builder()
                            .data(null)
                            .message("error", "Error: " + e.getMessage(), "InvestmentWorkflow")
                            .build());
        }
    }
}


