package com.minhnhat.demo.features.master.service;

import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.AllocationStrategyDetailEntity;
import com.minhnhat.demo.common.db.entity.AllocationStrategyEntity;
import com.minhnhat.demo.common.db.repository.AllocationStrategyDetailRepository;
import com.minhnhat.demo.common.db.repository.AllocationStrategyRepository;
import com.minhnhat.demo.common.exception.LogicException;
import com.minhnhat.demo.features.master.dto.request.InvestmentAllocationMasterCreateRequest;
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
public class CreateInvestmentAllocationMasterService {

    private final AllocationStrategyRepository strategyRepository;
    private final AllocationStrategyDetailRepository detailRepository;
    private final MessageService messageService;

    @Transactional(rollbackFor = Exception.class)
    public InvestmentAllocationMasterResponse execute(InvestmentAllocationMasterCreateRequest request) {
        CreateContext ctx = new CreateContext(request);
        validateRequest(ctx);
        loadData(ctx);
        processBusiness(ctx);
        return createResponse(ctx);
    }

    private void validateRequest(CreateContext ctx) {
        if (ctx.request == null) {
            throw new IllegalArgumentException("Request must not be null");
        }
    }

    private void loadData(CreateContext ctx) {
        ctx.duplicateCode = strategyRepository.existsByCode(ctx.request.getCode());
    }

    private void processBusiness(CreateContext ctx) {
        validateBusiness(ctx);
        prepare(ctx);
        executeCore(ctx);
        persist(ctx);
        afterProcess(ctx);
    }

    private void validateBusiness(CreateContext ctx) {
        if (ctx.duplicateCode) {
            throw new LogicException(SERVICE_NAME, messageService.get(ERROR_DUPLICATE_CODE), ERROR_DUPLICATE_CODE);
        }

        BigDecimal total = BigDecimal.ZERO;
        Set<String> assetTypes = new HashSet<>();
        for (InvestmentAllocationMasterCreateRequest.DetailRequest detail : ctx.request.getDetails()) {
            total = total.add(detail.getPercentage());
            if (!assetTypes.add(detail.getAssetType())) {
                throw new LogicException(SERVICE_NAME, messageService.get(ERROR_INVALID_PERCENTAGES), ERROR_INVALID_PERCENTAGES);
            }
        }

        if (total.compareTo(new BigDecimal("100.00")) != 0) {
            throw new LogicException(SERVICE_NAME, messageService.get(ERROR_INVALID_PERCENTAGES), ERROR_INVALID_PERCENTAGES);
        }
    }

    private void prepare(CreateContext ctx) {
        ctx.strategy = AllocationStrategyEntity.builder()
                .code(ctx.request.getCode())
                .name(ctx.request.getName())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private void executeCore(CreateContext ctx) {
        // No-op: create flow does not require additional execute step.
    }

    private void persist(CreateContext ctx) {
        try {
            ctx.savedStrategy = strategyRepository.save(ctx.strategy);
            for (InvestmentAllocationMasterCreateRequest.DetailRequest detail : ctx.request.getDetails()) {
                AllocationStrategyDetailEntity entity = AllocationStrategyDetailEntity.builder()
                        .strategyId(ctx.savedStrategy.getId())
                        .strategy(ctx.savedStrategy)
                        .strategyCode(ctx.savedStrategy.getCode())
                        .assetType(detail.getAssetType())
                        .percentage(detail.getPercentage())
                        .minimumLotSizeAmount(detail.getMinimumLotSizeAmount())
                        .build();
                ctx.savedDetails.add(detailRepository.save(entity));
            }
        } catch (Exception e) {
            log.error("Failed to create allocation strategy aggregate", e);
            throw new LogicException(SERVICE_NAME, messageService.get("error.createFailed"), "error.createFailed");
        }
    }

    private void afterProcess(CreateContext ctx) {
        log.info("Allocation strategy aggregate created successfully: id={}, code={}", ctx.savedStrategy.getId(), ctx.savedStrategy.getCode());
    }

    private InvestmentAllocationMasterResponse createResponse(CreateContext ctx) {
        return InvestmentAllocationMasterResponse.builder()
                .id(ctx.savedStrategy.getId())
                .code(ctx.savedStrategy.getCode())
                .name(ctx.savedStrategy.getName())
                .createdAt(ctx.savedStrategy.getCreatedAt())
                .updatedAt(ctx.savedStrategy.getUpdatedAt())
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

    private static class CreateContext {
        final InvestmentAllocationMasterCreateRequest request;
        boolean duplicateCode;
        AllocationStrategyEntity strategy;
        AllocationStrategyEntity savedStrategy;
        List<AllocationStrategyDetailEntity> savedDetails = new ArrayList<>();

        CreateContext(InvestmentAllocationMasterCreateRequest request) {
            this.request = request;
        }
    }
}
