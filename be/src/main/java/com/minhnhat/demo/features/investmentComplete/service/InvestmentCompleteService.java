package com.minhnhat.demo.features.investmentComplete.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.features.investmentComplete.dto.request.InvestmentCompleteRequest;
import com.minhnhat.demo.features.investmentComplete.dto.response.InvestmentCompleteResponse;
import com.minhnhat.demo.features.investmentComplete.entity.InvestmentPlanAllocationEntity;
import com.minhnhat.demo.features.investmentComplete.entity.InvestmentPlanEntity;
import com.minhnhat.demo.features.investmentComplete.repository.InvestmentPlanAllocationRepository;
import com.minhnhat.demo.features.investmentComplete.repository.InvestmentPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvestmentCompleteService {

    private final InvestmentPlanRepository planRepository;
    private final InvestmentPlanAllocationRepository allocationRepository;
    private final SecurityUtils securityUtils;
    private final ObjectMapper objectMapper;

    public InvestmentCompleteResponse createSession(final InvestmentCompleteRequest request) {
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
        plan.setStrategyId(request.getStrategyId()); // Now requires strategyId
        plan.setTotalAmount(request.getAmount());
        plan.setStatus("CONFIRMED");

        try {
            plan.setRecommendationSnapshot(objectMapper.writeValueAsString(request.getAllocations()));
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize allocations to JSON", e);
            plan.setRecommendationSnapshot(null);
        }

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
            e.setPercentage(a.getPercent()); // Changed from percent to percentage
            allocEntities.add(e);
        }

        allocationRepository.saveAll(allocEntities);

        List<InvestmentCompleteResponse.Allocation> allocations = allocEntities.stream()
                .map(e -> InvestmentCompleteResponse.Allocation.builder()
                        .type(e.getAssetType())
                        .amount(e.getAmount())
                        .percent(e.getPercentage())
                        .build())
                .toList();

        return InvestmentCompleteResponse.builder()
                .sessionId(UUID.randomUUID().toString())
                .planId(plan.getId())
                .userId(plan.getUserId())
                .type(request.getType())
                .totalAmount(plan.getTotalAmount())
                .status(plan.getStatus())
                .recommendationSnapshot(plan.getRecommendationSnapshot())
                .createdAt(plan.getCreatedAt())
                .allocations(allocations)
                .build();
    }
}


