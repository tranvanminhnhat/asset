package com.minhnhat.demo.features.master.service;

import com.minhnhat.demo.common.exception.LogicException;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.features.master.entity.AllocationStrategyEntity;
import com.minhnhat.demo.features.master.entity.AllocationStrategyDetailEntity;
import com.minhnhat.demo.features.master.dto.request.InvestmentAllocationMasterCreateRequest;
import com.minhnhat.demo.features.master.dto.request.InvestmentAllocationMasterUpdateRequest;
import com.minhnhat.demo.features.master.dto.response.InvestmentAllocationMasterResponse;
import com.minhnhat.demo.features.master.repository.AllocationStrategyRepository;
import com.minhnhat.demo.features.master.repository.AllocationStrategyDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.minhnhat.demo.features.master.constant.InvestmentAllocationMasterConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvestmentAllocationMasterService {

    private final AllocationStrategyRepository strategyRepository;
    private final AllocationStrategyDetailRepository detailRepository;
    private final MessageService messageService;

    public InvestmentAllocationMasterResponse create(InvestmentAllocationMasterCreateRequest request) {
        log.debug("Creating allocation strategy with code={}", request.getCode());

        if (strategyRepository.findByCode(request.getCode()).isPresent()) {
            log.error("Allocation strategy code already exists: {}", request.getCode());
            throw new LogicException(SERVICE_NAME, messageService.get(ERROR_DUPLICATE_CODE), ERROR_DUPLICATE_CODE);
        }

        validatePercentages(request.getStockPercent(), request.getEmergencyPercent(),
                request.getGoldPercent(), request.getEtfPercent());

        try {
            // Create strategy
            AllocationStrategyEntity strategy = AllocationStrategyEntity.builder()
                    .code(request.getCode())
                    .name(request.getCode()) // Use code as name if not provided
                    .createdAt(LocalDateTime.now())
                    .build();
            
            AllocationStrategyEntity savedStrategy = strategyRepository.save(strategy);
            
            // Create details
            createDetails(savedStrategy, request);
            
            log.info("Allocation strategy created successfully: {}", savedStrategy.getCode());
            return mapToResponse(savedStrategy, request);
        } catch (Exception e) {
            log.error("Failed to create allocation strategy", e);
            throw new LogicException(SERVICE_NAME, messageService.get("error.createFailed"), "error.createFailed");
        }
    }

    public InvestmentAllocationMasterResponse update(String code, InvestmentAllocationMasterUpdateRequest request) {
        log.debug("Updating allocation strategy with code={}", code);

        AllocationStrategyEntity strategy = strategyRepository.findByCode(code)
                .orElseThrow(() -> {
                    log.error("Allocation strategy not found: {}", code);
                    return new LogicException(SERVICE_NAME, messageService.get(ERROR_NOT_FOUND), ERROR_NOT_FOUND);
                });

        validatePercentages(request.getStockPercent(), request.getEmergencyPercent(),
                request.getGoldPercent(), request.getEtfPercent());

        try {
            // Delete old details
            List<AllocationStrategyDetailEntity> oldDetails = detailRepository.findByStrategyId(strategy.getId());
            detailRepository.deleteAll(oldDetails);
            
            // Create new details
            createDetails(strategy, request);
            
            strategy.setUpdatedAt(LocalDateTime.now());
            AllocationStrategyEntity updated = strategyRepository.save(strategy);
            
            log.info("Allocation strategy updated successfully: {}", updated.getCode());
            return mapToResponse(updated, request);
        } catch (Exception e) {
            log.error("Failed to update allocation strategy", e);
            throw new LogicException(SERVICE_NAME, messageService.get("error.updateFailed"), "error.updateFailed");
        }
    }

    public void delete(String code) {
        log.debug("Deleting allocation strategy with code={}", code);

        AllocationStrategyEntity strategy = strategyRepository.findByCode(code)
                .orElseThrow(() -> {
                    log.error("Allocation strategy not found: {}", code);
                    return new LogicException(SERVICE_NAME, messageService.get(ERROR_NOT_FOUND), ERROR_NOT_FOUND);
                });

        try {
            // Delete details first (cascade)
            List<AllocationStrategyDetailEntity> details = detailRepository.findByStrategyId(strategy.getId());
            detailRepository.deleteAll(details);
            
            // Delete strategy
            strategyRepository.delete(strategy);
            log.info("Allocation strategy deleted successfully: {}", code);
        } catch (Exception e) {
            log.error("Failed to delete allocation strategy", e);
            throw new LogicException(SERVICE_NAME, messageService.get("error.deleteFailed"), "error.deleteFailed");
        }
    }

    public InvestmentAllocationMasterResponse getById(String code) {
        log.debug("Getting allocation strategy with code={}", code);

        AllocationStrategyEntity strategy = strategyRepository.findByCode(code)
                .orElseThrow(() -> {
                    log.error("Allocation strategy not found: {}", code);
                    return new LogicException(SERVICE_NAME, messageService.get(ERROR_NOT_FOUND), ERROR_NOT_FOUND);
                });

        // Load details
        List<AllocationStrategyDetailEntity> details = detailRepository.findByStrategyId(strategy.getId());
        strategy.setDetails(details);
        
        return mapToResponse(strategy, details);
    }

    public List<InvestmentAllocationMasterResponse> getAll() {
        log.debug("Getting all allocation strategies");
        return strategyRepository.findAll().stream()
                .map(strategy -> {
                    List<AllocationStrategyDetailEntity> details = detailRepository.findByStrategyId(strategy.getId());
                    return mapToResponse(strategy, details);
                })
                .collect(Collectors.toList());
    }

    private void createDetails(AllocationStrategyEntity strategy, InvestmentAllocationMasterCreateRequest request) {
        detailRepository.save(AllocationStrategyDetailEntity.builder()
                .strategyId(strategy.getId())
                .strategy(strategy)
                .assetType("EMERGENCY")
                .percentage(request.getEmergencyPercent())
                .strategyCode(strategy.getCode())
                .build());

        detailRepository.save(AllocationStrategyDetailEntity.builder()
                .strategyId(strategy.getId())
                .strategy(strategy)
                .assetType("GOLD")
                .percentage(request.getGoldPercent())
                .strategyCode(strategy.getCode())
                .build());

        detailRepository.save(AllocationStrategyDetailEntity.builder()
                .strategyId(strategy.getId())
                .strategy(strategy)
                .assetType("ETF")
                .percentage(request.getEtfPercent())
                .strategyCode(strategy.getCode())
                .build());

        detailRepository.save(AllocationStrategyDetailEntity.builder()
                .strategyId(strategy.getId())
                .strategy(strategy)
                .assetType("STOCK")
                .percentage(request.getStockPercent())
                .strategyCode(strategy.getCode())
                .build());
    }

    private void createDetails(AllocationStrategyEntity strategy, InvestmentAllocationMasterUpdateRequest request) {
        detailRepository.save(AllocationStrategyDetailEntity.builder()
                .strategyId(strategy.getId())
                .strategy(strategy)
                .assetType("EMERGENCY")
                .percentage(request.getEmergencyPercent())
                .strategyCode(strategy.getCode())
                .build());

        detailRepository.save(AllocationStrategyDetailEntity.builder()
                .strategyId(strategy.getId())
                .strategy(strategy)
                .assetType("GOLD")
                .percentage(request.getGoldPercent())
                .strategyCode(strategy.getCode())
                .build());

        detailRepository.save(AllocationStrategyDetailEntity.builder()
                .strategyId(strategy.getId())
                .strategy(strategy)
                .assetType("ETF")
                .percentage(request.getEtfPercent())
                .strategyCode(strategy.getCode())
                .build());

        detailRepository.save(AllocationStrategyDetailEntity.builder()
                .strategyId(strategy.getId())
                .strategy(strategy)
                .assetType("STOCK")
                .percentage(request.getStockPercent())
                .strategyCode(strategy.getCode())
                .build());
    }

    private void validatePercentages(BigDecimal stock, BigDecimal emergency, BigDecimal gold, BigDecimal etf) {
        BigDecimal total = stock.add(emergency).add(gold).add(etf);
        if (!total.equals(new BigDecimal("100.00"))) {
            log.error("Percentages do not sum to 100: stock={}, emergency={}, gold={}, etf={}, total={}",
                    stock, emergency, gold, etf, total);
            throw new LogicException(SERVICE_NAME, messageService.get(ERROR_INVALID_PERCENTAGES), ERROR_INVALID_PERCENTAGES);
        }
    }

    private InvestmentAllocationMasterResponse mapToResponse(AllocationStrategyEntity strategy, InvestmentAllocationMasterCreateRequest request) {
        return InvestmentAllocationMasterResponse.builder()
                .code(strategy.getCode())
                .stockPercent(request.getStockPercent())
                .emergencyPercent(request.getEmergencyPercent())
                .goldPercent(request.getGoldPercent())
                .etfPercent(request.getEtfPercent())
                .createdAt(strategy.getCreatedAt())
                .updatedAt(strategy.getUpdatedAt())
                .build();
    }

    private InvestmentAllocationMasterResponse mapToResponse(AllocationStrategyEntity strategy, InvestmentAllocationMasterUpdateRequest request) {
        return InvestmentAllocationMasterResponse.builder()
                .code(strategy.getCode())
                .stockPercent(request.getStockPercent())
                .emergencyPercent(request.getEmergencyPercent())
                .goldPercent(request.getGoldPercent())
                .etfPercent(request.getEtfPercent())
                .createdAt(strategy.getCreatedAt())
                .updatedAt(strategy.getUpdatedAt())
                .build();
    }

    private InvestmentAllocationMasterResponse mapToResponse(AllocationStrategyEntity strategy, List<AllocationStrategyDetailEntity> details) {
        BigDecimal emergency = BigDecimal.ZERO;
        BigDecimal gold = BigDecimal.ZERO;
        BigDecimal etf = BigDecimal.ZERO;
        BigDecimal stock = BigDecimal.ZERO;

        for (AllocationStrategyDetailEntity detail : details) {
            switch (detail.getAssetType()) {
                case "EMERGENCY":
                    emergency = detail.getPercentage();
                    break;
                case "GOLD":
                    gold = detail.getPercentage();
                    break;
                case "ETF":
                    etf = detail.getPercentage();
                    break;
                case "STOCK":
                    stock = detail.getPercentage();
                    break;
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

