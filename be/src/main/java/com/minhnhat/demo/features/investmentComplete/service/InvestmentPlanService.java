package com.minhnhat.demo.features.investmentComplete.service;

import com.minhnhat.demo.common.db.entity.InvestmentPlanAllocationEntity;
import com.minhnhat.demo.common.db.entity.InvestmentPlanEntity;
import com.minhnhat.demo.common.db.repository.InvestmentPlanAllocationRepository;
import com.minhnhat.demo.common.db.repository.InvestmentPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Investment Plan Service - Manages investment plans
 * Pattern: validate → loadData → process → persist → buildResponse
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InvestmentPlanService {

    private final InvestmentPlanRepository investmentPlanRepository;
    private final InvestmentPlanAllocationRepository allocationRepository;

    /**
     * Create investment plan
     * Stores snapshot of recommendations for audit trail
     */
    @Transactional(rollbackFor = Exception.class)
    public InvestmentPlanEntity createPlan(
            String userId,
            Long strategyId,
            BigDecimal totalAmount) {
        CreatePlanContext ctx = new CreatePlanContext(userId, strategyId, totalAmount);
        validateRequest(ctx);
        loadData(ctx);
        processBusiness(ctx);
        return createResponse(ctx);
    }

    /**
     * Get plan with allocations
     */
    @Transactional(readOnly = true)
    public Optional<InvestmentPlanEntity> getPlanWithAllocations(Long planId) {
        log.debug("Fetching plan with allocations - ID: {}", planId);
        return investmentPlanRepository.findByIdWithDetails(planId);
    }

    /**
     * Get all plans for user
     */
    @Transactional(readOnly = true)
    public List<InvestmentPlanEntity> getUserPlans(String userId) {
        log.debug("Fetching plans for user: {}", userId);
        return investmentPlanRepository.findByUserIdWithDetails(userId);
    }

    /**
     * Get plans by status
     */
    @Transactional(readOnly = true)
    public List<InvestmentPlanEntity> getPlansByStatus(String userId) {
        return investmentPlanRepository.findByUserIdAndStatus(userId);
    }

    /**
     * Update plan status
     */
    @Transactional(rollbackFor = Exception.class)
    public InvestmentPlanEntity updatePlanStatus(Long planId, String newStatus) {
        log.info("Updating plan status - ID: {}, NewStatus: {}", planId, newStatus);

        InvestmentPlanEntity plan = investmentPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found: " + planId));


        InvestmentPlanEntity updated = investmentPlanRepository.save(plan);
        log.info("Plan status updated");

        return updated;
    }

    /**
     * Add allocation to plan
     */
    @Transactional(rollbackFor = Exception.class)
    public InvestmentPlanAllocationEntity addAllocation(
            Long planId,
            String assetType,
            BigDecimal amount,
            BigDecimal percentage) {
        AddAllocationContext ctx = new AddAllocationContext(planId, assetType, amount, percentage);
        validateRequest(ctx);
        loadData(ctx);
        processBusiness(ctx);
        return createResponse(ctx);
    }

    /**
     * Get allocations for plan
     */
    @Transactional(readOnly = true)
    public List<InvestmentPlanAllocationEntity> getPlanAllocations(Long planId) {
        log.debug("Fetching allocations for plan: {}", planId);
        return allocationRepository.findByPlanId(planId);
    }

    /**
     * Validation helper
     */
    private void validatePlanInputs(String userId, Long strategyId, BigDecimal totalAmount) {
        if (userId == null || userId.isEmpty()) {
            log.error("Invalid user ID");
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (strategyId == null || strategyId <= 0) {
            log.error("Invalid strategy ID: {}", strategyId);
            throw new IllegalArgumentException("Strategy ID must be positive");
        }
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid total amount: {}", totalAmount);
            throw new IllegalArgumentException("Total amount must be positive");
        }
    }

    private void validateRequest(CreatePlanContext ctx) {
        validatePlanInputs(ctx.userId, ctx.strategyId, ctx.totalAmount);
    }

    private void loadData(CreatePlanContext ctx) {
        // No pre-load required in current create plan flow.
    }

    private void processBusiness(CreatePlanContext ctx) {
        validateBusiness(ctx);
        prepare(ctx);
        execute(ctx);
        persist(ctx);
        afterProcess(ctx);
    }

    private void validateBusiness(CreatePlanContext ctx) {
        // No additional business validation in current flow.
    }

    private void prepare(CreatePlanContext ctx) {
        ctx.plan = InvestmentPlanEntity.builder()
                .userId(ctx.userId)
                .totalAmount(ctx.totalAmount)
                .build();
    }

    private void execute(CreatePlanContext ctx) {
        // No additional execute step before persistence.
    }

    private void persist(CreatePlanContext ctx) {
        ctx.savedPlan = investmentPlanRepository.save(ctx.plan);
    }

    private void afterProcess(CreatePlanContext ctx) {
        log.info("Investment plan created with ID: {}", ctx.savedPlan.getId());
    }

    private InvestmentPlanEntity createResponse(CreatePlanContext ctx) {
        return ctx.savedPlan;
    }

    private void validateRequest(AddAllocationContext ctx) {
        log.info("Adding allocation to plan - Plan: {}, AssetType: {}, Amount: {}, Percentage: {}",
                ctx.planId, ctx.assetType, ctx.amount, ctx.percentage);
    }

    private void loadData(AddAllocationContext ctx) {
        investmentPlanRepository.findById(ctx.planId)
                .orElseThrow(() -> new RuntimeException("Plan not found: " + ctx.planId));
    }

    private void processBusiness(AddAllocationContext ctx) {
        validateBusiness(ctx);
        prepare(ctx);
        execute(ctx);
        persist(ctx);
        afterProcess(ctx);
    }

    private void validateBusiness(AddAllocationContext ctx) {
        // Keep behavior unchanged: no additional validation in current flow.
    }

    private void prepare(AddAllocationContext ctx) {
        ctx.allocation = InvestmentPlanAllocationEntity.builder()
                .planId(ctx.planId)
                .assetType(ctx.assetType)
                .amount(ctx.amount)
                .percentage(ctx.percentage)
                .build();
    }

    private void execute(AddAllocationContext ctx) {
        // No additional execute step before persistence.
    }

    private void persist(AddAllocationContext ctx) {
        ctx.savedAllocation = allocationRepository.save(ctx.allocation);
    }

    private void afterProcess(AddAllocationContext ctx) {
        log.info("Allocation added with ID: {}", ctx.savedAllocation.getId());
    }

    private InvestmentPlanAllocationEntity createResponse(AddAllocationContext ctx) {
        return ctx.savedAllocation;
    }

    private static class CreatePlanContext {
        final String userId;
        final Long strategyId;
        final BigDecimal totalAmount;
        InvestmentPlanEntity plan;
        InvestmentPlanEntity savedPlan;

        CreatePlanContext(String userId, Long strategyId, BigDecimal totalAmount) {
            this.userId = userId;
            this.strategyId = strategyId;
            this.totalAmount = totalAmount;
        }
    }

    private static class AddAllocationContext {
        final Long planId;
        final String assetType;
        final BigDecimal amount;
        final BigDecimal percentage;
        InvestmentPlanAllocationEntity allocation;
        InvestmentPlanAllocationEntity savedAllocation;

        AddAllocationContext(Long planId, String assetType, BigDecimal amount, BigDecimal percentage) {
            this.planId = planId;
            this.assetType = assetType;
            this.amount = amount;
            this.percentage = percentage;
        }
    }
}



