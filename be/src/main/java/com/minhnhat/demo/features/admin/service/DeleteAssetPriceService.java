package com.minhnhat.demo.features.admin.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.AssetPriceEntity;
import com.minhnhat.demo.common.db.repository.AssetPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeleteAssetPriceService {
    private final AssetPriceRepository priceRepository;
    private final MessageService messageService;

    public ApiResponse<Void> execute(Long id) {
        Context ctx = new Context();
        ctx.id = id;
        this.loadData(ctx);
        this.processBusiness(ctx);
        return this.createResponse();
    }

    private void loadData(Context ctx) {
        ctx.price = priceRepository.findById(ctx.id)
                .orElseThrow(() -> new IllegalArgumentException("Price not found with id: " + ctx.id));
    }

    private void processBusiness(Context ctx) {
        ctx.price.setIsActive(false);
        priceRepository.save(ctx.price);
        log.info("Asset price deleted: id={}, productId={}", ctx.id, ctx.price.getProductId());
    }

    private ApiResponse<Void> createResponse() {
        return ApiResponse.<Void>builder()
                .data(null)
                .message("admin.price.delete.success", "Xoa gia tai san thanh cong", "AdminService")
                .build();
    }

    private static class Context {
        Long id;
        AssetPriceEntity price;
    }
}

