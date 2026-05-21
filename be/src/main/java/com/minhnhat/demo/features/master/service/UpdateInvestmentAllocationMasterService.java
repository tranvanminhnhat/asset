package com.minhnhat.demo.features.master.service;

import com.minhnhat.demo.common.db.entity.AllocationStrategyDetailEntity;
import com.minhnhat.demo.common.db.entity.AllocationStrategyEntity;
import com.minhnhat.demo.common.db.repository.AllocationStrategyDetailRepository;
import com.minhnhat.demo.common.db.repository.AllocationStrategyRepository;
import com.minhnhat.demo.common.exception.LogicException;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.features.master.dto.request.InvestmentAllocationMasterUpdateRequest;
import com.minhnhat.demo.features.master.dto.response.InvestmentAllocationMasterResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.minhnhat.demo.features.master.constant.InvestmentAllocationMasterConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateInvestmentAllocationMasterService {

    private final AllocationStrategyRepository strategyRepository;
    private final AllocationStrategyDetailRepository detailRepository;
    private final MessageService messageService;

    public InvestmentAllocationMasterResponse execute(String code, InvestmentAllocationMasterUpdateRequest request) {
        log.debug("Updating allocation strategy with code={}", code);

        AllocationStrategyEntity strategy = strategyRepository.findByCode(code)
                .orElseThrow(() -> {
                    log.error("Allocation strategy not found: {}", code);
                    return new LogicException(SERVICE_NAME, messageService.get(ERROR_NOT_FOUND), ERROR_NOT_FOUND);
                });

        validatePercentages(request.getStockPercent(), request.getEmergencyPercent(), request.getGoldPercent(), request.getEtfPercent());

        try {
            List<AllocationStrategyDetailEntity> oldDetails = detailRepository.findByStrategyId(strategy.getId());
            detailRepository.deleteAll(oldDetails);
            saveDetails(strategy, request.getEmergencyPercent(), request.getGoldPercent(), request.getEtfPercent(), request.getStockPercent());
            strategy.setUpdatedAt(LocalDateTime.now());
            AllocationStrategyEntity updated = strategyRepository.save(strategy);

            log.info("Allocation strategy updated successfully: {}", updated.getCode());
            return InvestmentAllocationMasterResponse.builder()
                    .code(updated.getCode())
                    .stockPercent(request.getStockPercent())
                    .emergencyPercent(request.getEmergencyPercent())
                    .goldPercent(request.getGoldPercent())
                    .etfPercent(request.getEtfPercent())
                    .createdAt(updated.getCreatedAt())
                    .updatedAt(updated.getUpdatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Failed to update allocation strategy", e);
            throw new LogicException(SERVICE_NAME, messageService.get("error.updateFailed"), "error.updateFailed");
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

