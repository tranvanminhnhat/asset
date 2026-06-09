package com.minhnhat.demo.features.investment.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.features.investment.dto.response.AssetPriceResponse;
import com.minhnhat.demo.features.investment.service.GetAvailableAssetsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
@Slf4j
public class GetAvailableAssetsController {

    private final GetAvailableAssetsService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AssetPriceResponse>>> getAssets(
            @RequestParam(required = false) String type) {
        try {
            ApiResponse<List<AssetPriceResponse>> response = service.execute(type);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching assets", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<List<AssetPriceResponse>>builder()
                            .data(null)
                            .message("error", "Error: " + e.getMessage(), "InvestmentWorkflow")
                            .build());
        }
    }
}


