package com.minhnhat.demo.features.admin.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.features.admin.dto.response.AssetProductResponse;
import com.minhnhat.demo.features.admin.service.GetAssetProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/product")
@RequiredArgsConstructor
@Slf4j
public class GetAssetProductController {
    private final GetAssetProductService service;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AssetProductResponse>> getById(@PathVariable Long id) {
        try {
            ApiResponse<AssetProductResponse> response = service.execute(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting asset product", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<AssetProductResponse>builder()
                            .data(null)
                            .message("error", "Error: " + e.getMessage(), "AdminService")
                            .build());
        }
    }
}

