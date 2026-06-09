package com.minhnhat.demo.features.admin.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.features.admin.dto.request.CreateAssetPlatformRequest;
import com.minhnhat.demo.features.admin.dto.response.AssetPlatformResponse;
import com.minhnhat.demo.features.admin.service.CreateAssetPlatformService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/platform")
@RequiredArgsConstructor
@Slf4j
public class CreateAssetPlatformController {
    private final CreateAssetPlatformService service;

    @PostMapping
    public ResponseEntity<ApiResponse<AssetPlatformResponse>> create(
            @RequestBody CreateAssetPlatformRequest request) {
        try {
            ApiResponse<AssetPlatformResponse> response = service.execute(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating asset platform", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<AssetPlatformResponse>builder()
                            .data(null)
                            .message("error", "Error: " + e.getMessage(), "AdminService")
                            .build());
        }
    }
}

