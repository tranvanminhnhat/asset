package com.minhnhat.demo.features.admin.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.features.admin.dto.request.UpdateAssetCategoryRequest;
import com.minhnhat.demo.features.admin.dto.response.AssetCategoryResponse;
import com.minhnhat.demo.features.admin.service.UpdateAssetCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/category")
@RequiredArgsConstructor
@Slf4j
public class UpdateAssetCategoryController {
    private final UpdateAssetCategoryService service;

    @PutMapping
    public ResponseEntity<ApiResponse<AssetCategoryResponse>> update(
            @RequestBody UpdateAssetCategoryRequest request) {
        try {
            ApiResponse<AssetCategoryResponse> response = service.execute(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating asset category", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<AssetCategoryResponse>builder()
                            .data(null)
                            .message("error", "Error: " + e.getMessage(), "AdminService")
                            .build());
        }
    }
}

