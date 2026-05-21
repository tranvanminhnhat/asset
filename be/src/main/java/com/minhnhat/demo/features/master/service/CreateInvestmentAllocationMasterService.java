package com.minhnhat.demo.features.master.service;

import com.minhnhat.demo.common.db.entity.AllocationStrategyEntity;
import com.minhnhat.demo.common.db.repository.AllocationStrategyDetailRepository;
import com.minhnhat.demo.common.db.repository.AllocationStrategyRepository;
import com.minhnhat.demo.common.exception.LogicException;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.features.master.dto.request.InvestmentAllocationMasterCreateRequest;
import com.minhnhat.demo.features.master.dto.response.InvestmentAllocationMasterResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.minhnhat.demo.features.master.constant.InvestmentAllocationMasterConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateInvestmentAllocationMasterService {

    private final AllocationStrategyRepository strategyRepository;
    private final AllocationStrategyDetailRepository detailRepository;
    private final MessageService messageService;

    public InvestmentAllocationMasterResponse execute(InvestmentAllocationMasterCreateRequest request) {
        log.debug("Creating allocation strategy with code={}", request.getCode());

        if (strategyRepository.findByCode(request.getCode()).isPresent()) {
            log.error("Allocation strategy code already exists: {}", request.getCode());
            throw new LogicException(SERVICE_NAME, messageService.get(ERROR_DUPLICATE_CODE), ERROR_DUPLICATE_CODE);
        }

        validatePercentages(request.getStockPercent(), request.getEmergencyPercent(), request.getGoldPercent(), request.getEtfPercent());

        try {
            AllocationStrategyEntity strategy = AllocationStrategyEntity.builder()
                    .code(request.getCode())
                    .name(request.getCode())
                    .createdAt(LocalDateTime.now())
                    .build();

            AllocationStrategyEntity savedStrategy = strategyRepository.save(strategy);
            saveDetails(savedStrategy, request.getEmergencyPercent(), request.getGoldPercent(), request.getEtfPercent(), request.getStockPercent());

            log.info("Allocation strategy created successfully: {}", savedStrategy.getCode());
            return InvestmentAllocationMasterResponse.builder()
                    .code(savedStrategy.getCode())
                    .stockPercent(request.getStockPercent())
                    .emergencyPercent(request.getEmergencyPercent())
                    .goldPercent(request.getGoldPercent())
                    .etfPercent(request.getEtfPercent())
                    .createdAt(savedStrategy.getCreatedAt())
                    .updatedAt(savedStrategy.getUpdatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Failed to create allocation strategy", e);
            throw new LogicException(SERVICE_NAME, messageService.get("error.createFailed"), "error.createFailed");
        }
    }

    private void validatePercentages(BigDecimal stock, BigDecimal emergency, BigDecimal gold, BigDecimal etf) {
        BigDecimal total = stock.add(emergency).add(gold).add(etf);
        if (!total.equals(new BigDecimal("100.00"))) {
            log.error("Percentages do not sum to 100: stock={}, emergency={}, gold={}, etf={}, total={}", stock, emergency, gold, etf, total);
            throw new LogicException(SERVICE_NAME, messageService.get(ERROR_INVALID_PERCENTAGES), ERROR_INVALID_PERCENTAGES);
        }
    }

    private void saveDetails(AllocationStrategyEntity strategy, BigDecimal emergency, BigDecimal gold, BigDecimal etf, BigDecimal stock) {
        detailRepository.save(com.minhnhat.demo.common.db.entity.AllocationStrategyDetailEntity.builder().strategyId(strategy.getId()).strategy(strategy).assetType("EMERGENCY").percentage(emergency).strategyCode(strategy.getCode()).build());
        detailRepository.save(com.minhnhat.demo.common.db.entity.AllocationStrategyDetailEntity.builder().strategyId(strategy.getId()).strategy(strategy).assetType("GOLD").percentage(gold).strategyCode(strategy.getCode()).build());
        detailRepository.save(com.minhnhat.demo.common.db.entity.AllocationStrategyDetailEntity.builder().strategyId(strategy.getId()).strategy(strategy).assetType("ETF").percentage(etf).strategyCode(strategy.getCode()).build());
        detailRepository.save(com.minhnhat.demo.common.db.entity.AllocationStrategyDetailEntity.builder().strategyId(strategy.getId()).strategy(strategy).assetType("STOCK").percentage(stock).strategyCode(strategy.getCode()).build());
    }
}

