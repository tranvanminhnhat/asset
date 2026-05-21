package com.minhnhat.demo.features.investmentComplete.service;

import com.minhnhat.demo.common.db.entity.InvestmentPlanAllocationEntity;
import com.minhnhat.demo.common.db.repository.InvestmentPlanAllocationRepository;
import com.minhnhat.demo.common.db.repository.InvestmentPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddInvestmentPlanAllocationService {

    private final InvestmentPlanRepository investmentPlanRepository;
    private final InvestmentPlanAllocationRepository allocationRepository;

    @Transactional(rollbackFor = Exception.class)
    public InvestmentPlanAllocationEntity execute(Long planId, String assetType, BigDecimal amount, BigDecimal percentage) {
        Context ctx = new Context(planId, assetType, amount, percentage);
        validateRequest(ctx);
        loadData(ctx);
        processBusiness(ctx);
        return createResponse(ctx);
    }

    private void validateRequest(Context ctx) {
        log.info("Adding allocation to plan - Plan: {}, AssetType: {}, Amount: {}, Percentage: {}",
                ctx.planId, ctx.assetType, ctx.amount, ctx.percentage);
    }

    private void loadData(Context ctx) {
        investmentPlanRepository.findById(ctx.planId)
                .orElseThrow(() -> new RuntimeException("Plan not found: " + ctx.planId));
    }

    private void processBusiness(Context ctx) {
        validateBusiness(ctx);
        prepare(ctx);
        execute(ctx);
        persist(ctx);
        afterProcess(ctx);
    }

    private void validateBusiness(Context ctx) {
        // Keep behavior unchanged.
    }

    private void prepare(Context ctx) {
        ctx.allocation = InvestmentPlanAllocationEntity.builder()
                .planId(ctx.planId)
                .assetType(ctx.assetType)
                .amount(ctx.amount)
                .percentage(ctx.percentage)
                .build();
    }

    private void execute(Context ctx) {
        // No additional execute step before persistence.
    }

    private void persist(Context ctx) {
        ctx.savedAllocation = allocationRepository.save(ctx.allocation);
    }

    private void afterProcess(Context ctx) {
        log.info("Allocation added with ID: {}", ctx.savedAllocation.getId());
    }

    private InvestmentPlanAllocationEntity createResponse(Context ctx) {
        return ctx.savedAllocation;
    }

    private static class Context {
        final Long planId;
        final String assetType;
        final BigDecimal amount;
        final BigDecimal percentage;
        InvestmentPlanAllocationEntity allocation;
        InvestmentPlanAllocationEntity savedAllocation;

        Context(Long planId, String assetType, BigDecimal amount, BigDecimal percentage) {
            this.planId = planId;
            this.assetType = assetType;
            this.amount = amount;
            this.percentage = percentage;
        }
    }
}

