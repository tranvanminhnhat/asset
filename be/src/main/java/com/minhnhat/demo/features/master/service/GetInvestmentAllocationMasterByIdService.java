package com.minhnhat.demo.features.master.service;

import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.AllocationStrategyDetailEntity;
import com.minhnhat.demo.common.db.entity.AllocationStrategyEntity;
import com.minhnhat.demo.common.db.repository.AllocationStrategyDetailRepository;
import com.minhnhat.demo.common.db.repository.AllocationStrategyRepository;
import com.minhnhat.demo.common.exception.LogicException;
import com.minhnhat.demo.features.master.dto.response.InvestmentAllocationMasterResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.minhnhat.demo.features.master.constant.InvestmentAllocationMasterConstant.ERROR_NOT_FOUND;
import static com.minhnhat.demo.features.master.constant.InvestmentAllocationMasterConstant.SERVICE_NAME;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetInvestmentAllocationMasterByIdService {

    private final AllocationStrategyRepository strategyRepository;
    private final AllocationStrategyDetailRepository detailRepository;
    private final MessageService messageService;

    public InvestmentAllocationMasterResponse execute(Long id) {
        GetByIdContext ctx = new GetByIdContext(id);
        validateRequest(ctx);
        loadData(ctx);
        processBusiness(ctx);
        return createResponse(ctx);
    }

    private void validateRequest(GetByIdContext ctx) {
        if (ctx.id == null || ctx.id <= 0) {
            throw new IllegalArgumentException("Strategy id must be positive");
        }
    }

    private void loadData(GetByIdContext ctx) {
        ctx.strategy = strategyRepository.findById(ctx.id)
                .orElseThrow(() -> new LogicException(SERVICE_NAME, messageService.get(ERROR_NOT_FOUND), ERROR_NOT_FOUND));
        ctx.details = detailRepository.findByStrategyId(ctx.strategy.getId());
    }

    private void processBusiness(GetByIdContext ctx) {
        validateBusiness(ctx);
        prepare(ctx);
        executeCore(ctx);
        persist(ctx);
        afterProcess(ctx);
    }

    private void validateBusiness(GetByIdContext ctx) {
        // No extra business validation for read flow.
    }

    private void prepare(GetByIdContext ctx) {
        // No-op: read flow does not require preparation.
    }

    private void executeCore(GetByIdContext ctx) {
        // No-op: read flow does not require execute step.
    }

    private void persist(GetByIdContext ctx) {
        // No-op: read flow does not persist.
    }

    private void afterProcess(GetByIdContext ctx) {
        log.debug("Fetched allocation strategy aggregate: id={}, code={}", ctx.strategy.getId(), ctx.strategy.getCode());
    }

    private InvestmentAllocationMasterResponse createResponse(GetByIdContext ctx) {
        return InvestmentAllocationMasterResponse.builder()
                .id(ctx.strategy.getId())
                .code(ctx.strategy.getCode())
                .name(ctx.strategy.getName())
                .createdAt(ctx.strategy.getCreatedAt())
                .updatedAt(ctx.strategy.getUpdatedAt())
                .details(ctx.details.stream().map(detail -> InvestmentAllocationMasterResponse.DetailResponse.builder()
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

    private static class GetByIdContext {
        final Long id;
        AllocationStrategyEntity strategy;
        List<AllocationStrategyDetailEntity> details;

        GetByIdContext(Long id) {
            this.id = id;
        }
    }
}
