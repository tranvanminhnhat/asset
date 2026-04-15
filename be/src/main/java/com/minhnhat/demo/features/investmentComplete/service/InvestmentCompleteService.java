package com.minhnhat.demo.features.investmentComplete.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.features.investmentComplete.dto.response.InvestmentCompleteResponse;
import com.minhnhat.demo.features.investmentComplete.entity.InvestmentPlanAllocationEntity;
import com.minhnhat.demo.features.investmentComplete.entity.InvestmentPlanEntity;
import com.minhnhat.demo.features.investmentComplete.repository.InvestmentPlanAllocationRepository;
import com.minhnhat.demo.features.investmentComplete.repository.InvestmentPlanRepository;
import com.minhnhat.demo.features.investmentRecommendations.dto.request.InvestmentRecommendationsRequest;
import com.minhnhat.demo.features.investmentRecommendations.service.InvestmentRecommendationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvestmentCompleteService {

    private final InvestmentRecommendationsService recommendationsService;

    private final InvestmentPlanRepository planRepository;

    private final InvestmentPlanAllocationRepository allocationRepository;

    private final SecurityUtils securityUtils;

    private final ObjectMapper objectMapper;

    public InvestmentCompleteResponse createSession(final InvestmentRecommendationsRequest request) {
        var rec = recommendationsService.allocate(request);

        // current user
        Long userId = null;
        try {
            var cu = securityUtils.getCurrentUser();
            if (cu != null && cu.getUserId() != null) {
                userId = Long.parseLong(cu.getUserId());
            }
        } catch (Exception ignored) {
            // keep userId null if parsing fails
        }

        InvestmentPlanEntity plan = new InvestmentPlanEntity();
        plan.setUserId(userId);
        plan.setTotalAmount(request.getAmount());
        plan.setStatus("CONFIRMED");
        plan.setCreatedBy(userId);
        plan.setUpdatedBy(userId);

        try {
            plan.setRecommendationSnapshot(objectMapper.writeValueAsString(rec));
        } catch (JsonProcessingException e) {
            plan.setRecommendationSnapshot(null);
        }

        plan = planRepository.save(plan);

        List<InvestmentPlanAllocationEntity> allocEntities = new ArrayList<>();
        int idx = 0;
        for (var a : rec.getAllocations()) {
            InvestmentPlanAllocationEntity e = new InvestmentPlanAllocationEntity();
            e.setPlanId(plan.getId());
            e.setAllocationType(a.getType());
            e.setAmount(a.getAmount());
            e.setPercent(a.getPercent());
            e.setDisplayOrder(idx++);
            allocEntities.add(e);
        }

        allocationRepository.saveAll(allocEntities);

        List<InvestmentCompleteResponse.Allocation> allocations = allocEntities.stream()
                .map(e -> InvestmentCompleteResponse.Allocation.builder()
                        .type(e.getAllocationType())
                        .amount(e.getAmount())
                        .percent(e.getPercent())
                        .build())
                .collect(Collectors.toList());

        return InvestmentCompleteResponse.builder()
                .sessionId(UUID.randomUUID().toString())
                .planId(plan.getId())
                .userId(plan.getUserId())
                .type(request.getType())
                .totalAmount(plan.getTotalAmount())
                .status(plan.getStatus())
                .recommendationSnapshot(plan.getRecommendationSnapshot())
                .note(plan.getNote())
                .createdAt(plan.getCreatedAt())
                .allocations(allocations)
                .build();
    }
}

