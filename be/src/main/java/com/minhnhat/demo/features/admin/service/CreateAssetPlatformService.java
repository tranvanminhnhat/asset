package com.minhnhat.demo.features.admin.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.AssetPlatformEntity;
import com.minhnhat.demo.common.db.repository.AssetPlatformRepository;
import com.minhnhat.demo.features.admin.dto.request.CreateAssetPlatformRequest;
import com.minhnhat.demo.features.admin.dto.response.AssetPlatformResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateAssetPlatformService {
    private final AssetPlatformRepository platformRepository;
    private final MessageService messageService;

    public ApiResponse<AssetPlatformResponse> execute(CreateAssetPlatformRequest request) {
        Context ctx = new Context();
        ctx.request = request;
        this.validateRequest(ctx);
        this.processBusiness(ctx);
        return this.createResponse(ctx);
    }

    private void validateRequest(Context ctx) {
        if (ctx.request.getCode() == null || ctx.request.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Platform code is required");
        }
        if (ctx.request.getName() == null || ctx.request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Platform name is required");
        }
        if (platformRepository.findByCode(ctx.request.getCode()).isPresent()) {
            throw new IllegalArgumentException("Platform code already exists");
        }
    }

    private void processBusiness(Context ctx) {
        AssetPlatformEntity platform = AssetPlatformEntity.builder()
                .code(ctx.request.getCode())
                .name(ctx.request.getName())
                .description(ctx.request.getDescription())
                .isActive(true)
                .build();
        ctx.savedPlatform = platformRepository.save(platform);
        log.info("Asset platform created: code={}, name={}", ctx.request.getCode(), ctx.request.getName());
    }

    private ApiResponse<AssetPlatformResponse> createResponse(Context ctx) {
        AssetPlatformResponse response = AssetPlatformResponse.builder()
                .id(ctx.savedPlatform.getId())
                .code(ctx.savedPlatform.getCode())
                .name(ctx.savedPlatform.getName())
                .description(ctx.savedPlatform.getDescription())
                .isActive(ctx.savedPlatform.getIsActive())
                .createdAt(ctx.savedPlatform.getCreatedAt().toLocalDateTime())
                .updatedAt(ctx.savedPlatform.getUpdatedAt().toLocalDateTime())
                .build();

        return ApiResponse.<AssetPlatformResponse>builder()
                .data(response)
                .message("admin.platform.create.success", "Tao nen tang tai san thanh cong", "AdminService")
                .build();
    }

    private static class Context {
        CreateAssetPlatformRequest request;
        AssetPlatformEntity savedPlatform;
    }
}

