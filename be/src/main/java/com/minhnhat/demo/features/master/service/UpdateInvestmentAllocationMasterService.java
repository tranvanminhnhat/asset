package com.minhnhat.demo.features.master.service;

import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.AllocationStrategyDetailEntity;
import com.minhnhat.demo.common.db.entity.AllocationStrategyEntity;
import com.minhnhat.demo.common.db.repository.AllocationStrategyDetailRepository;
import com.minhnhat.demo.common.db.repository.AllocationStrategyRepository;
import com.minhnhat.demo.common.exception.LogicException;
import com.minhnhat.demo.features.master.dto.request.InvestmentAllocationMasterUpdateRequest;
import com.minhnhat.demo.features.master.dto.response.InvestmentAllocationMasterResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.minhnhat.demo.features.master.constant.InvestmentAllocationMasterConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateInvestmentAllocationMasterService {

    private final AllocationStrategyRepository strategyRepository;
    private final AllocationStrategyDetailRepository detailRepository;
    private final MessageService messageService;

    @Transactional(rollbackFor = Exception.class)
    public InvestmentAllocationMasterResponse execute(Long id, InvestmentAllocationMasterUpdateRequest request) {
        UpdateContext ctx = new UpdateContext(id, request);
        validateRequest(ctx);
        loadData(ctx);
        processBusiness(ctx);
        return createResponse(ctx);
    }

    private void validateRequest(UpdateContext ctx) {
        if (ctx.request == null) {
            throw new IllegalArgumentException("Request must not be null");
        }
    }

    private void loadData(UpdateContext ctx) {
        ctx.strategy = strategyRepository.findById(ctx.id)
                .orElseThrow(() -> new LogicException(SERVICE_NAME, messageService.get(ERROR_NOT_FOUND), ERROR_NOT_FOUND));
        ctx.duplicateCode = strategyRepository.existsByCodeAndIdNot(ctx.request.getCode(), ctx.id);
    }

    private void processBusiness(UpdateContext ctx) {
        validateBusiness(ctx);
        prepare(ctx);
        executeCore(ctx);
        persist(ctx);
        afterProcess(ctx);
    }

    private void validateBusiness(UpdateContext ctx) {
        if (ctx.duplicateCode) {
            throw new LogicException(SERVICE_NAME, messageService.get(ERROR_DUPLICATE_CODE), ERROR_DUPLICATE_CODE);
        }

        BigDecimal total = BigDecimal.ZERO;
        Set<String> assetTypes = new HashSet<>();
        for (InvestmentAllocationMasterUpdateRequest.DetailRequest detail : ctx.request.getDetails()) {
            total = total.add(detail.getPercentage());
            if (!assetTypes.add(detail.getAssetType())) {
                throw new LogicException(SERVICE_NAME, messageService.get(ERROR_INVALID_PERCENTAGES), ERROR_INVALID_PERCENTAGES);
            }
        }

        if (total.compareTo(new BigDecimal("100.00")) != 0) {
            throw new LogicException(SERVICE_NAME, messageService.get(ERROR_INVALID_PERCENTAGES), ERROR_INVALID_PERCENTAGES);
        }
    }

    private void prepare(UpdateContext ctx) {
        ctx.strategy.setCode(ctx.request.getCode());
        ctx.strategy.setName(ctx.request.getName());
        ctx.strategy.setUpdatedAt(LocalDateTime.now());
    }

    private void executeCore(UpdateContext ctx) {
        // No-op: update flow does not require additional execute step.
    }

    private void persist(UpdateContext ctx) {
        try {
            ctx.updatedStrategy = strategyRepository.save(ctx.strategy);
            detailRepository.deleteByStrategyId(ctx.updatedStrategy.getId());
            detailRepository.flush();

            for (InvestmentAllocationMasterUpdateRequest.DetailRequest detail : ctx.request.getDetails()) {
                AllocationStrategyDetailEntity entity = AllocationStrategyDetailEntity.builder()
                        .strategyId(ctx.updatedStrategy.getId())
                        .strategy(ctx.updatedStrategy)
                        .strategyCode(ctx.updatedStrategy.getCode())
                        .assetType(detail.getAssetType())
                        .percentage(detail.getPercentage())
                        .minimumLotSizeAmount(detail.getMinimumLotSizeAmount())
                        .build();
                ctx.savedDetails.add(detailRepository.save(entity));
            }
        } catch (Exception e) {
            log.error("Failed to update allocation strategy aggregate", e);
            throw new LogicException(SERVICE_NAME, messageService.get("error.updateFailed"), "error.updateFailed");
        }
    }

    private void afterProcess(UpdateContext ctx) {
        log.info("Allocation strategy aggregate updated successfully: id={}, code={}", ctx.updatedStrategy.getId(), ctx.updatedStrategy.getCode());
    }

    private InvestmentAllocationMasterResponse createResponse(UpdateContext ctx) {
        return InvestmentAllocationMasterResponse.builder()
                .id(ctx.updatedStrategy.getId())
                .code(ctx.updatedStrategy.getCode())
                .name(ctx.updatedStrategy.getName())
                .createdAt(ctx.updatedStrategy.getCreatedAt())
                .updatedAt(ctx.updatedStrategy.getUpdatedAt())
                .details(ctx.savedDetails.stream().map(detail -> InvestmentAllocationMasterResponse.DetailResponse.builder()
                        .id(detail.getId())
                        .strategyId(detail.getStrategyId())
                        .assetType(detail.getAssetType())
                        .percentage(detail.getPercentage())
                        .minimumLotSizeAmount(detail.getMinimumLotSizeAmount())
                        .createdAt(detail.getCreatedAt())
                        .updatedAt(detail.getUpdatedAt())
                        .build()).toList())
                .build();
    }

    private static class UpdateContext {
        final Long id;
        final InvestmentAllocationMasterUpdateRequest request;
        AllocationStrategyEntity strategy;
        boolean duplicateCode;
        AllocationStrategyEntity updatedStrategy;
        List<AllocationStrategyDetailEntity> savedDetails = new ArrayList<>();

        UpdateContext(Long id, InvestmentAllocationMasterUpdateRequest request) {
            this.id = id;
            this.request = request;
        }
    }
}
