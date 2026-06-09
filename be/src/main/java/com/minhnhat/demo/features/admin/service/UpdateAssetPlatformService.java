package com.minhnhat.demo.features.admin.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.AssetPlatformEntity;
import com.minhnhat.demo.common.db.repository.AssetPlatformRepository;
import com.minhnhat.demo.features.admin.dto.request.UpdateAssetPlatformRequest;
import com.minhnhat.demo.features.admin.dto.response.AssetPlatformResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateAssetPlatformService {
    private final AssetPlatformRepository platformRepository;
    private final MessageService messageService;

    public ApiResponse<AssetPlatformResponse> execute(UpdateAssetPlatformRequest request) {
        Context ctx = new Context();
        ctx.request = request;
        this.validateRequest(ctx);
        this.loadData(ctx);
        this.processBusiness(ctx);
        return this.createResponse(ctx);
    }

    private void validateRequest(Context ctx) {
        if (ctx.request.getId() == null) {
            throw new IllegalArgumentException("Platform id is required");
        }
        if (ctx.request.getCode() == null || ctx.request.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Platform code is required");
        }
        if (ctx.request.getName() == null || ctx.request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Platform name is required");
        }
    }

    private void loadData(Context ctx) {
        ctx.platform = platformRepository.findById(ctx.request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Platform not found with id: " + ctx.request.getId()));

        platformRepository.findByCode(ctx.request.getCode()).ifPresent(existing -> {
            if (!existing.getId().equals(ctx.request.getId())) {
                throw new IllegalArgumentException("Platform code already exists for another platform");
            }
        });
    }

    private void processBusiness(Context ctx) {
        ctx.platform.setCode(ctx.request.getCode());
        ctx.platform.setName(ctx.request.getName());
        ctx.platform.setDescription(ctx.request.getDescription());
        ctx.platform = platformRepository.save(ctx.platform);
        log.info("Asset platform updated: id={}, code={}, name={}", ctx.request.getId(), ctx.request.getCode(), ctx.request.getName());
    }

    private ApiResponse<AssetPlatformResponse> createResponse(Context ctx) {
        AssetPlatformResponse response = AssetPlatformResponse.builder()
                .id(ctx.platform.getId())
                .code(ctx.platform.getCode())
                .name(ctx.platform.getName())
                .description(ctx.platform.getDescription())
                .isActive(ctx.platform.getIsActive())
                .createdAt(ctx.platform.getCreatedAt().toLocalDateTime())
                .updatedAt(ctx.platform.getUpdatedAt().toLocalDateTime())
                .build();

        return ApiResponse.<AssetPlatformResponse>builder()
                .data(response)
                .message("admin.platform.update.success", "Update nen tang tai san thanh cong", "AdminService")
                .build();
    }

    private static class Context {
        UpdateAssetPlatformRequest request;
        AssetPlatformEntity platform;
    }
}

