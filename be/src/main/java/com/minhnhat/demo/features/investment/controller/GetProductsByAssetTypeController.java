package com.minhnhat.demo.features.investment.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.features.investment.dto.response.ProductWithPriceResponse;
import com.minhnhat.demo.features.investment.service.GetProductsByAssetTypeService;
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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class GetProductsByAssetTypeController {

    private final GetProductsByAssetTypeService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductWithPriceResponse>>> getProducts(
            @RequestParam(required = false) String type) {
        try {
            if (type == null || type.isBlank()) {
                log.error("Asset type parameter is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.<List<ProductWithPriceResponse>>builder()
                                .data(null)
                                .message("error", "Asset type parameter is required", "InvestmentWorkflow")
                                .build());
            }

            ApiResponse<List<ProductWithPriceResponse>> response = service.execute(type);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<List<ProductWithPriceResponse>>builder()
                            .data(null)
                            .message("error", e.getMessage(), "InvestmentWorkflow")
                            .build());
        } catch (Exception e) {
            log.error("Error fetching products", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<ProductWithPriceResponse>>builder()
                            .data(null)
                            .message("error", "Error: " + e.getMessage(), "InvestmentWorkflow")
                            .build());
        }
    }
}

