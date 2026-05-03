package com.minhnhat.demo.features.investmentComplete.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.features.investmentComplete.dto.request.InvestmentCompleteRequest;
import com.minhnhat.demo.features.investmentComplete.dto.response.InvestmentCompleteResponse;
import com.minhnhat.demo.features.investmentComplete.entity.InvestmentPlanAllocationEntity;
import com.minhnhat.demo.features.investmentComplete.entity.InvestmentPlanEntity;
import com.minhnhat.demo.features.investmentComplete.repository.InvestmentPlanAllocationRepository;
import com.minhnhat.demo.features.investmentComplete.repository.InvestmentPlanRepository;
import com.minhnhat.demo.features.investmentRecommendations.entity.AssetResidualsEntity;
import com.minhnhat.demo.features.investmentRecommendations.repository.AssetResidualsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvestmentCompleteService {

    private final InvestmentPlanRepository planRepository;
    private final InvestmentPlanAllocationRepository allocationRepository;
    private final AssetResidualsRepository assetResidualsRepository;
    private final SecurityUtils securityUtils;
    private final ObjectMapper objectMapper;

    public InvestmentCompleteResponse createInvestmentSession(final InvestmentCompleteRequest request) {
        // Get current user
        String userId = null;
        try {
            var cu = securityUtils.getCurrentUser();
            if (cu != null) {
                userId = cu.getUserId();
            }
        } catch (Exception e) {
            throw new RuntimeException("User must be authenticated to create investment plan", e);
        }

        // Create investment plan
        InvestmentPlanEntity plan = new InvestmentPlanEntity();
        plan.setUserId(userId);
        plan.setTotalAmount(request.getAmount());

        try {
            plan = planRepository.save(plan);
            log.info("Investment plan saved successfully: planId={}, userId={}", plan.getId(), plan.getUserId());
        } catch (Exception e) {
            log.error("Failed to save investment plan for userId={}", userId, e);
            throw new RuntimeException("Failed to save investment plan: " + e.getMessage(), e);
        }

        // Create allocations
        List<InvestmentPlanAllocationEntity> allocEntities = new ArrayList<>();
        for (var a : request.getAllocations()) {
            InvestmentPlanAllocationEntity e = new InvestmentPlanAllocationEntity();
            e.setPlanId(plan.getId());
            e.setAssetType(a.getType()); // Changed from allocationType to assetType
            e.setAmount(a.getAmount());
            e.setQuantity(a.getQuantity());
            e.setPercentage(a.getPercent()); // Changed from percent to percentage
            allocEntities.add(e);
        }

        allocationRepository.saveAll(allocEntities);

        String finalUserId = userId;
        List<InvestmentCompleteResponse.Allocation> allocations = request.getAllocations().stream()
                .map(e -> {
                    // Get or create residual amount from AssetResidualsEntity
                    BigDecimal residualAmount = e.getResidualAmount();
                    Optional<AssetResidualsEntity> residualOpt = assetResidualsRepository.findByUserIdAndAssetType(
                            finalUserId, e.getType());
                    
                    if (residualOpt.isPresent()) {
                        // Update existing residual
                        AssetResidualsEntity residual = residualOpt.get();
                        residual.setResidualAmount(residualAmount);
                        assetResidualsRepository.save(residual);
                        log.info("Updated asset residual: userId={}, assetType={}, residualAmount={}", 
                            finalUserId, e.getType(), residualAmount);
                    } else {
                        // Create new residual record
                        AssetResidualsEntity newResidual = AssetResidualsEntity.builder()
                                .userId(finalUserId)
                                .assetType(e.getType())
                                .residualAmount(residualAmount)
                                .build();
                        assetResidualsRepository.save(newResidual);
                        log.info("Created new asset residual: userId={}, assetType={}, residualAmount={}", 
                            finalUserId, e.getType(), residualAmount);
                    }

                    return InvestmentCompleteResponse.Allocation.builder()
                            .type(e.getType())
                            .amount(e.getAmount())
                            .quantity(e.getQuantity())
                            .percent(e.getPercent())
                            .residualAmount(residualAmount)
                            .build();
                })
                .toList();

        return InvestmentCompleteResponse.builder()
                .sessionId(UUID.randomUUID().toString())
                .planId(plan.getId())
                .userId(plan.getUserId())
                .type(request.getType())
                .totalAmount(plan.getTotalAmount())
                .createdAt(plan.getCreatedAt())
                .allocations(allocations)
                .build();
    }
}


