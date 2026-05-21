package com.minhnhat.demo.features.master.service;

import com.minhnhat.demo.common.db.entity.AllocationStrategyDetailEntity;
import com.minhnhat.demo.common.db.entity.AllocationStrategyEntity;
import com.minhnhat.demo.common.db.repository.AllocationStrategyDetailRepository;
import com.minhnhat.demo.common.db.repository.AllocationStrategyRepository;
import com.minhnhat.demo.features.master.dto.response.InvestmentAllocationMasterResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAllInvestmentAllocationMasterService {

    private final AllocationStrategyRepository strategyRepository;
    private final AllocationStrategyDetailRepository detailRepository;

    public List<InvestmentAllocationMasterResponse> execute() {
        log.debug("Getting all allocation strategies");
        return strategyRepository.findAll().stream()
                .map(strategy -> createResponse(strategy, detailRepository.findByStrategyId(strategy.getId())))
                .toList();
    }

    private InvestmentAllocationMasterResponse createResponse(AllocationStrategyEntity strategy, List<AllocationStrategyDetailEntity> details) {
        BigDecimal emergency = BigDecimal.ZERO;
        BigDecimal gold = BigDecimal.ZERO;
        BigDecimal etf = BigDecimal.ZERO;
        BigDecimal stock = BigDecimal.ZERO;

        for (AllocationStrategyDetailEntity detail : details) {
            switch (detail.getAssetType()) {
                case "EMERGENCY" -> emergency = detail.getPercentage();
                case "GOLD" -> gold = detail.getPercentage();
                case "ETF" -> etf = detail.getPercentage();
                case "STOCK" -> stock = detail.getPercentage();
            }
        }

        return InvestmentAllocationMasterResponse.builder()
                .code(strategy.getCode())
                .stockPercent(stock)
                .emergencyPercent(emergency)
                .goldPercent(gold)
                .etfPercent(etf)
                .createdAt(strategy.getCreatedAt())
                .updatedAt(strategy.getUpdatedAt())
                .build();
    }
}

