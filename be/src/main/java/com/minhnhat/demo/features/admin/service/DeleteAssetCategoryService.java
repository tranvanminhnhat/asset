package com.minhnhat.demo.features.admin.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.AssetCategoryEntity;
import com.minhnhat.demo.common.db.repository.AssetCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeleteAssetCategoryService {
    private final AssetCategoryRepository categoryRepository;
    private final MessageService messageService;

    public ApiResponse<Void> execute(Integer id) {
        Context ctx = new Context();
        ctx.id = id;
        this.loadData(ctx);
        this.processBusiness(ctx);
        return this.createResponse();
    }

    private void loadData(Context ctx) {
        ctx.category = categoryRepository.findById(ctx.id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + ctx.id));
    }

    private void processBusiness(Context ctx) {
        ctx.category.setIsActive(false);
        categoryRepository.save(ctx.category);
        log.info("Asset category deleted: id={}, code={}", ctx.id, ctx.category.getCode());
    }

    private ApiResponse<Void> createResponse() {
        return ApiResponse.<Void>builder()
                .data(null)
                .message("admin.category.delete.success", "Xoa danh muc tai san thanh cong", "AdminService")
                .build();
    }

    private static class Context {
        Integer id;
        AssetCategoryEntity category;
    }
}

