package com.minhnhat.demo.features.master.service;

import com.minhnhat.demo.common.db.entity.AllocationStrategyDetailEntity;
import com.minhnhat.demo.common.db.entity.AllocationStrategyEntity;
import com.minhnhat.demo.common.db.repository.AllocationStrategyDetailRepository;
import com.minhnhat.demo.common.db.repository.AllocationStrategyRepository;
import com.minhnhat.demo.common.exception.LogicException;
import com.minhnhat.demo.common.config.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.minhnhat.demo.features.master.constant.InvestmentAllocationMasterConstant.ERROR_NOT_FOUND;
import static com.minhnhat.demo.features.master.constant.InvestmentAllocationMasterConstant.SERVICE_NAME;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteInvestmentAllocationMasterService {

    private final AllocationStrategyRepository strategyRepository;
    private final AllocationStrategyDetailRepository detailRepository;
    private final MessageService messageService;

    public void execute(String code) {
        log.debug("Deleting allocation strategy with code={}", code);

        AllocationStrategyEntity strategy = strategyRepository.findByCode(code)
                .orElseThrow(() -> {
                    log.error("Allocation strategy not found: {}", code);
                    return new LogicException(SERVICE_NAME, messageService.get(ERROR_NOT_FOUND), ERROR_NOT_FOUND);
                });

        try {
            List<AllocationStrategyDetailEntity> details = detailRepository.findByStrategyId(strategy.getId());
            detailRepository.deleteAll(details);
            strategyRepository.delete(strategy);
            log.info("Allocation strategy deleted successfully: {}", code);
        } catch (Exception e) {
            log.error("Failed to delete allocation strategy", e);
            throw new LogicException(SERVICE_NAME, messageService.get("error.deleteFailed"), "error.deleteFailed");
        }
    }
}

