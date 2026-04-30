package com.minhnhat.demo.features.investmentComplete.service;

import com.minhnhat.demo.features.investmentComplete.entity.InvestmentPlanEntity;
import com.minhnhat.demo.features.investmentComplete.entity.InvestmentPlanAllocationEntity;
import com.minhnhat.demo.features.investmentComplete.repository.InvestmentPlanRepository;
import com.minhnhat.demo.features.investmentComplete.repository.InvestmentPlanAllocationRepository;
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


        // Validate inputs
        validatePlanInputs(userId, strategyId, totalAmount);

        // Create entity
        InvestmentPlanEntity plan = InvestmentPlanEntity.builder()
                .userId(userId)
                .totalAmount(totalAmount)
                .build();

        // Persist
        InvestmentPlanEntity saved = investmentPlanRepository.save(plan);
        log.info("Investment plan created with ID: {}", saved.getId());

        return saved;
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

        log.info("Adding allocation to plan - Plan: {}, AssetType: {}, Amount: {}, Percentage: {}",
                planId, assetType, amount, percentage);

        // Verify plan exists
        investmentPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found: " + planId));

        InvestmentPlanAllocationEntity allocation = InvestmentPlanAllocationEntity.builder()
                .planId(planId)
                .assetType(assetType)
                .amount(amount)
                .percentage(percentage)
                .build();

        InvestmentPlanAllocationEntity saved = allocationRepository.save(allocation);
        log.info("Allocation added with ID: {}", saved.getId());

        return saved;
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
}



