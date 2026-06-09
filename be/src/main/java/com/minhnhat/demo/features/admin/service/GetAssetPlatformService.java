package com.minhnhat.demo.features.admin.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.AssetPlatformEntity;
import com.minhnhat.demo.common.db.repository.AssetPlatformRepository;
import com.minhnhat.demo.features.admin.dto.response.AssetPlatformResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAssetPlatformService {
    private final AssetPlatformRepository platformRepository;
    private final MessageService messageService;

    public ApiResponse<AssetPlatformResponse> execute(Long id) {
        Context ctx = new Context();
        ctx.id = id;
        this.loadData(ctx);
        return this.createResponse(ctx);
    }

    private void loadData(Context ctx) {
        ctx.platform = platformRepository.findById(ctx.id)
                .orElseThrow(() -> new IllegalArgumentException("Platform not found with id: " + ctx.id));
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
                .message("admin.platform.get.success", "Get nen tang tai san thanh cong", "AdminService")
                .build();
    }

    private static class Context {
        Long id;
        AssetPlatformEntity platform;
    }
}

