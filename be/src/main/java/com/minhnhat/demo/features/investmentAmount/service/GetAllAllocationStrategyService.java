package com.minhnhat.demo.features.investmentAmount.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.AllocationStrategyDetailEntity;
import com.minhnhat.demo.common.db.entity.AllocationStrategyEntity;
import com.minhnhat.demo.common.db.repository.AllocationStrategyRepository;
import com.minhnhat.demo.features.investmentAmount.constant.InvestmentAmountConstant;
import com.minhnhat.demo.features.investmentAmount.dto.response.InvestmentAmountStrategyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAllAllocationStrategyService {

    private final AllocationStrategyRepository allocationStrategyRepository;
    private final MessageService messageService;

    public ApiResponse<List<InvestmentAmountStrategyResponse>> execute() {
        Context ctx = new Context();
        this.loadData(ctx);
        this.processBusiness(ctx);
        return this.createResponse(ctx);
    }

    private void loadData(Context ctx) {
        ctx.strategies = allocationStrategyRepository.findAllWithDetails();
    }

    private void processBusiness(Context ctx) {
        ctx.response = ctx.strategies.stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    private InvestmentAmountStrategyResponse mapEntityToResponse(AllocationStrategyEntity entity) {
        return InvestmentAmountStrategyResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .details(mapDetailsToResponse(entity.getDetails()))
                .build();
    }

    private List<InvestmentAmountStrategyResponse.DetailResponse> mapDetailsToResponse(List<AllocationStrategyDetailEntity> details) {
        return details.stream()
                .map(detail -> InvestmentAmountStrategyResponse.DetailResponse.builder()
                        .id(detail.getId())
                        .strategyId(detail.getStrategyId())
                        .assetType(detail.getAssetType())
                        .percentage(detail.getPercentage())
                        .minimumLotSizeAmount(detail.getMinimumLotSizeAmount())
                        .createdAt(detail.getCreatedAt())
                        .updatedAt(detail.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    private ApiResponse<List<InvestmentAmountStrategyResponse>> createResponse(Context ctx) {
        return ApiResponse.<List<InvestmentAmountStrategyResponse>>builder()
                .data(ctx.response)
                .message(
                        InvestmentAmountConstant.GET_ALL_INFO_001,
                        messageService.get(InvestmentAmountConstant.GET_ALL_INFO_001),
                        InvestmentAmountConstant.SERVICE_NAME
                ).build();
    }

    private static class Context {
        List<AllocationStrategyEntity> strategies;
        List<InvestmentAmountStrategyResponse> response;
    }
}
