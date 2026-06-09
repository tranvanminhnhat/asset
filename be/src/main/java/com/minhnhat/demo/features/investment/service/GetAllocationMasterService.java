package com.minhnhat.demo.features.investment.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.AllocationStrategyDetailEntity;
import com.minhnhat.demo.common.db.entity.AllocationStrategyEntity;
import com.minhnhat.demo.common.db.repository.AllocationStrategyDetailRepository;
import com.minhnhat.demo.common.db.repository.AllocationStrategyRepository;
import com.minhnhat.demo.features.investment.constant.InvestmentWorkflowConstant;
import com.minhnhat.demo.features.investment.dto.response.AllocationMasterResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAllocationMasterService {

    private final AllocationStrategyRepository strategyRepository;
    private final AllocationStrategyDetailRepository detailRepository;
    private final MessageService messageService;

    public ApiResponse<AllocationMasterResponse> execute() {
        Context ctx = new Context();
        this.loadData(ctx);
        this.processBusiness(ctx);
        return this.createResponse(ctx);
    }

    private void loadData(Context ctx) {
        ctx.strategies = strategyRepository.findAll();
        log.debug("Loaded {} allocation strategies", ctx.strategies.size());
    }

    private void processBusiness(Context ctx) {
        ctx.allocationMasters = ctx.strategies.stream()
                .map(strategy -> {
                    List<AllocationStrategyDetailEntity> details = detailRepository.findByStrategyId(strategy.getId());
                    List<AllocationMasterResponse.AllocationDetail> detailResponses = details.stream()
                            .map(detail -> AllocationMasterResponse.AllocationDetail.builder()
                                    .assetType(detail.getAssetType())
                                    .percentage(detail.getPercentage())
                                    .minimumLotSizeAmount(detail.getMinimumLotSizeAmount())
                                    .build())
                            .collect(Collectors.toList());

                    return AllocationMasterResponse.AllocationMaster.builder()
                            .strategyCode(strategy.getCode())
                            .strategyName(strategy.getName())
                            .details(detailResponses)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private ApiResponse<AllocationMasterResponse> createResponse(Context ctx) {
        AllocationMasterResponse response = AllocationMasterResponse.builder()
                .allocations(ctx.allocationMasters)
                .build();

        return ApiResponse.<AllocationMasterResponse>builder()
                .data(response)
                .message("investment.master.info",
                        messageService.get("investment.master.info"),
                        InvestmentWorkflowConstant.SERVICE_NAME)
                .build();
    }

    private static class Context {
        List<AllocationStrategyEntity> strategies;
        List<AllocationMasterResponse.AllocationMaster> allocationMasters;
    }
}

