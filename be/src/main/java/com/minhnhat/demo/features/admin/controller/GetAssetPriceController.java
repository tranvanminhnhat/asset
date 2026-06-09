package com.minhnhat.demo.features.admin.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.features.admin.dto.response.AssetPriceResponse;
import com.minhnhat.demo.features.admin.service.AdminGetAssetPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/price")
@RequiredArgsConstructor
@Slf4j
public class GetAssetPriceController {
    private final AdminGetAssetPriceService service;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AssetPriceResponse>> getById(@PathVariable Long id) {
        try {
            ApiResponse<AssetPriceResponse> response = service.execute(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting asset price", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<AssetPriceResponse>builder()
                            .data(null)
                            .message("error", "Error: " + e.getMessage(), "AdminService")
                            .build());
        }
    }
}

