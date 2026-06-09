package com.minhnhat.demo.features.admin.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.features.admin.service.DeleteAssetProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/product")
@RequiredArgsConstructor
@Slf4j
public class DeleteAssetProductController {
    private final DeleteAssetProductService service;

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            ApiResponse<Void> response = service.execute(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting asset product", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Void>builder()
                            .data(null)
                            .message("error", "Error: " + e.getMessage(), "AdminService")
                            .build());
        }
    }
}

