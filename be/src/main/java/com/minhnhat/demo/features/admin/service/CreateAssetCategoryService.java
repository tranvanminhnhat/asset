package com.minhnhat.demo.features.admin.service;
import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.AssetCategoryEntity;
import com.minhnhat.demo.common.db.repository.AssetCategoryRepository;
import com.minhnhat.demo.features.admin.dto.request.CreateAssetCategoryRequest;
import com.minhnhat.demo.features.admin.dto.response.AssetCategoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
@Slf4j
public class CreateAssetCategoryService {
    private final AssetCategoryRepository categoryRepository;
    private final MessageService messageService;
    public ApiResponse<AssetCategoryResponse> execute(CreateAssetCategoryRequest request) {
        Context ctx = new Context();
        ctx.request = request;
        this.validateRequest(ctx);
        this.processBusiness(ctx);
        return this.createResponse(ctx);
    }
    private void validateRequest(Context ctx) {
        if (ctx.request.getCode() == null || ctx.request.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Category code is required");
        }
        if (ctx.request.getName() == null || ctx.request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required");
        }
        if (categoryRepository.findByCode(ctx.request.getCode()).isPresent()) {
            throw new IllegalArgumentException("Category code already exists");
        }
    }
    private void processBusiness(Context ctx) {
        AssetCategoryEntity category = AssetCategoryEntity.builder()
                .code(ctx.request.getCode())
                .name(ctx.request.getName())
                .description(ctx.request.getDescription())
                .isActive(true)
                .build();
        ctx.savedCategory = categoryRepository.save(category);
        log.info("Asset category created: code={}, name={}", ctx.request.getCode(), ctx.request.getName());
    }
    private ApiResponse<AssetCategoryResponse> createResponse(Context ctx) {
        AssetCategoryResponse response = AssetCategoryResponse.builder()
                .id(ctx.savedCategory.getId())
                .code(ctx.savedCategory.getCode())
                .name(ctx.savedCategory.getName())
                .description(ctx.savedCategory.getDescription())
                .isActive(ctx.savedCategory.getIsActive())
                .build();
        return ApiResponse.<AssetCategoryResponse>builder()
                .data(response)
                .message("admin.category.create.success", "Tao danh muc tai san thanh cong", "AdminService")
                .build();
    }
    private static class Context {
        CreateAssetCategoryRequest request;
        AssetCategoryEntity savedCategory;
    }
}
