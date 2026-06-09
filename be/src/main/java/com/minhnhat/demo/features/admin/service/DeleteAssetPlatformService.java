package com.minhnhat.demo.features.admin.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.AssetPlatformEntity;
import com.minhnhat.demo.common.db.repository.AssetPlatformRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeleteAssetPlatformService {
    private final AssetPlatformRepository platformRepository;
    private final MessageService messageService;

    public ApiResponse<Void> execute(Long id) {
        Context ctx = new Context();
        ctx.id = id;
        this.loadData(ctx);
        this.processBusiness(ctx);
        return this.createResponse();
    }

    private void loadData(Context ctx) {
        ctx.platform = platformRepository.findById(ctx.id)
                .orElseThrow(() -> new IllegalArgumentException("Platform not found with id: " + ctx.id));
    }

    private void processBusiness(Context ctx) {
        ctx.platform.setIsActive(false);
        platformRepository.save(ctx.platform);
        log.info("Asset platform deleted: id={}, code={}", ctx.id, ctx.platform.getCode());
    }

    private ApiResponse<Void> createResponse() {
        return ApiResponse.<Void>builder()
                .data(null)
                .message("admin.platform.delete.success", "Xoa nen tang tai san thanh cong", "AdminService")
                .build();
    }

    private static class Context {
        Long id;
        AssetPlatformEntity platform;
    }
}

