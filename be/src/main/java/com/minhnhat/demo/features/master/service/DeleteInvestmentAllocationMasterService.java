package com.minhnhat.demo.features.master.service;

import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.AllocationStrategyEntity;
import com.minhnhat.demo.common.db.repository.AllocationStrategyDetailRepository;
import com.minhnhat.demo.common.db.repository.AllocationStrategyRepository;
import com.minhnhat.demo.common.exception.LogicException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.minhnhat.demo.features.master.constant.InvestmentAllocationMasterConstant.ERROR_NOT_FOUND;
import static com.minhnhat.demo.features.master.constant.InvestmentAllocationMasterConstant.SERVICE_NAME;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteInvestmentAllocationMasterService {

    private final AllocationStrategyRepository strategyRepository;
    private final AllocationStrategyDetailRepository detailRepository;
    private final MessageService messageService;

    @Transactional(rollbackFor = Exception.class)
    public void execute(Long id) {
        DeleteContext ctx = new DeleteContext(id);
        validateRequest(ctx);
        loadData(ctx);
        processBusiness(ctx);
        createResponse(ctx);
    }

    private void validateRequest(DeleteContext ctx) {
        if (ctx.id == null || ctx.id <= 0) {
            throw new IllegalArgumentException("Strategy id must be positive");
        }
    }

    private void loadData(DeleteContext ctx) {
        ctx.strategy = strategyRepository.findById(ctx.id)
                .orElseThrow(() -> new LogicException(SERVICE_NAME, messageService.get(ERROR_NOT_FOUND), ERROR_NOT_FOUND));
    }

    private void processBusiness(DeleteContext ctx) {
        validateBusiness(ctx);
        prepare(ctx);
        executeCore(ctx);
        persist(ctx);
        afterProcess(ctx);
    }

    private void validateBusiness(DeleteContext ctx) {
        // No extra business validation in delete flow.
    }

    private void prepare(DeleteContext ctx) {
        // No-op.
    }

    private void executeCore(DeleteContext ctx) {
        // No-op.
    }

    private void persist(DeleteContext ctx) {
        try {
            detailRepository.deleteByStrategyId(ctx.strategy.getId());
            strategyRepository.delete(ctx.strategy);
        } catch (Exception e) {
            log.error("Failed to delete allocation strategy aggregate", e);
            throw new LogicException(SERVICE_NAME, messageService.get("error.deleteFailed"), "error.deleteFailed");
        }
    }

    private void afterProcess(DeleteContext ctx) {
        log.info("Allocation strategy aggregate deleted successfully: id={}, code={}",
                ctx.strategy.getId(), ctx.strategy.getCode());
    }

    private Void createResponse(DeleteContext ctx) {
        return null;
    }

    private static class DeleteContext {
        final Long id;
        AllocationStrategyEntity strategy;

        DeleteContext(Long id) {
            this.id = id;
        }
    }
}
