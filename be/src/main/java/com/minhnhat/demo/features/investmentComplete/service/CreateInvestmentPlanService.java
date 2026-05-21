package com.minhnhat.demo.features.investmentComplete.service;

import com.minhnhat.demo.common.db.entity.InvestmentPlanEntity;
import com.minhnhat.demo.common.db.repository.InvestmentPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateInvestmentPlanService {

    private final InvestmentPlanRepository investmentPlanRepository;

    @Transactional(rollbackFor = Exception.class)
    public InvestmentPlanEntity execute(String userId, Long strategyId, BigDecimal totalAmount) {
        Context ctx = new Context(userId, strategyId, totalAmount);
        validateRequest(ctx);
        loadData(ctx);
        processBusiness(ctx);
        return createResponse(ctx);
    }

    private void validateRequest(Context ctx) {
        if (ctx.userId == null || ctx.userId.isEmpty()) {
            log.error("Invalid user ID");
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (ctx.strategyId == null || ctx.strategyId <= 0) {
            log.error("Invalid strategy ID: {}", ctx.strategyId);
            throw new IllegalArgumentException("Strategy ID must be positive");
        }
        if (ctx.totalAmount == null || ctx.totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid total amount: {}", ctx.totalAmount);
            throw new IllegalArgumentException("Total amount must be positive");
        }
    }

    private void loadData(Context ctx) {
        // No pre-load required in current create plan flow.
    }

    private void processBusiness(Context ctx) {
        validateBusiness(ctx);
        prepare(ctx);
        execute(ctx);
        persist(ctx);
        afterProcess(ctx);
    }

    private void validateBusiness(Context ctx) {
        // No additional business validation in current flow.
    }

    private void prepare(Context ctx) {
        ctx.plan = InvestmentPlanEntity.builder()
                .userId(ctx.userId)
                .totalAmount(ctx.totalAmount)
                .build();
    }

    private void execute(Context ctx) {
        // No additional execute step before persistence.
    }

    private void persist(Context ctx) {
        ctx.savedPlan = investmentPlanRepository.save(ctx.plan);
    }

    private void afterProcess(Context ctx) {
        log.info("Investment plan created with ID: {}", ctx.savedPlan.getId());
    }

    private InvestmentPlanEntity createResponse(Context ctx) {
        return ctx.savedPlan;
    }

    private static class Context {
        final String userId;
        final Long strategyId;
        final BigDecimal totalAmount;
        InvestmentPlanEntity plan;
        InvestmentPlanEntity savedPlan;

        Context(String userId, Long strategyId, BigDecimal totalAmount) {
            this.userId = userId;
            this.strategyId = strategyId;
            this.totalAmount = totalAmount;
        }
    }
}

