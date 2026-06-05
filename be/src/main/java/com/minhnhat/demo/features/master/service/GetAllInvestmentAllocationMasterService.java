package com.minhnhat.demo.features.master.service;

import com.minhnhat.demo.common.db.entity.AllocationStrategyDetailEntity;
import com.minhnhat.demo.common.db.entity.AllocationStrategyEntity;
import com.minhnhat.demo.common.db.repository.AllocationStrategyDetailRepository;
import com.minhnhat.demo.common.db.repository.AllocationStrategyRepository;
import com.minhnhat.demo.features.master.dto.response.InvestmentAllocationMasterResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAllInvestmentAllocationMasterService {

    private final AllocationStrategyRepository strategyRepository;
    private final AllocationStrategyDetailRepository detailRepository;

    public List<InvestmentAllocationMasterResponse> execute() {
        GetAllContext ctx = new GetAllContext();
        validateRequest(ctx);
        loadData(ctx);
        processBusiness(ctx);
        return createResponse(ctx);
    }

    private void validateRequest(GetAllContext ctx) {
        // No request payload in get-all flow.
    }

    private void loadData(GetAllContext ctx) {
        ctx.strategies = strategyRepository.findAll();
    }

    private void processBusiness(GetAllContext ctx) {
        validateBusiness(ctx);
        prepare(ctx);
        executeCore(ctx);
        persist(ctx);
        afterProcess(ctx);
    }

    private void validateBusiness(GetAllContext ctx) {
        // No extra business validation for read flow.
    }

    private void prepare(GetAllContext ctx) {
        // No-op.
    }

    private void executeCore(GetAllContext ctx) {
        for (AllocationStrategyEntity strategy : ctx.strategies) {
            List<AllocationStrategyDetailEntity> details = detailRepository.findByStrategyId(strategy.getId());
            ctx.responses.add(mapToResponse(strategy, details));
        }
    }

    private void persist(GetAllContext ctx) {
        // No-op: read flow does not persist.
    }

    private void afterProcess(GetAllContext ctx) {
        log.debug("Fetched {} allocation strategy aggregates", ctx.responses.size());
    }

    private List<InvestmentAllocationMasterResponse> createResponse(GetAllContext ctx) {
        return ctx.responses;
    }

    private InvestmentAllocationMasterResponse mapToResponse(AllocationStrategyEntity strategy, List<AllocationStrategyDetailEntity> details) {
        return InvestmentAllocationMasterResponse.builder()
                .id(strategy.getId())
                .code(strategy.getCode())
                .name(strategy.getName())
                .createdAt(strategy.getCreatedAt())
                .updatedAt(strategy.getUpdatedAt())
                .details(details.stream().map(detail -> InvestmentAllocationMasterResponse.DetailResponse.builder()
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

    private static class GetAllContext {
        List<AllocationStrategyEntity> strategies = new ArrayList<>();
        List<InvestmentAllocationMasterResponse> responses = new ArrayList<>();
    }
}
