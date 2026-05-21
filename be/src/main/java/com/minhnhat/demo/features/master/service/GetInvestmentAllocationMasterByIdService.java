package com.minhnhat.demo.features.master.service;

import com.minhnhat.demo.common.db.entity.AllocationStrategyDetailEntity;
import com.minhnhat.demo.common.db.entity.AllocationStrategyEntity;
import com.minhnhat.demo.common.db.repository.AllocationStrategyDetailRepository;
import com.minhnhat.demo.common.db.repository.AllocationStrategyRepository;
import com.minhnhat.demo.common.exception.LogicException;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.features.master.dto.response.InvestmentAllocationMasterResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.minhnhat.demo.features.master.constant.InvestmentAllocationMasterConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetInvestmentAllocationMasterByIdService {

    private final AllocationStrategyRepository strategyRepository;
    private final AllocationStrategyDetailRepository detailRepository;
    private final MessageService messageService;

    public InvestmentAllocationMasterResponse execute(String code) {
        log.debug("Getting allocation strategy with code={}", code);

        AllocationStrategyEntity strategy = strategyRepository.findByCode(code)
                .orElseThrow(() -> {
                    log.error("Allocation strategy not found: {}", code);
                    return new LogicException(SERVICE_NAME, messageService.get(ERROR_NOT_FOUND), ERROR_NOT_FOUND);
                });

        List<AllocationStrategyDetailEntity> details = detailRepository.findByStrategyId(strategy.getId());
        return createResponse(strategy, details);
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

