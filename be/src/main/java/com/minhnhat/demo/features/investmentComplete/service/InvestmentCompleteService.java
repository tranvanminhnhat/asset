package com.minhnhat.demo.features.investmentComplete.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.common.db.entity.AssetResidualsEntity;
import com.minhnhat.demo.common.db.entity.InvestmentPlanAllocationEntity;
import com.minhnhat.demo.common.db.entity.InvestmentPlanEntity;
import com.minhnhat.demo.common.db.repository.AssetResidualsRepository;
import com.minhnhat.demo.common.db.repository.InvestmentPlanAllocationRepository;
import com.minhnhat.demo.common.db.repository.InvestmentPlanRepository;
import com.minhnhat.demo.features.investmentComplete.dto.request.InvestmentCompleteRequest;
import com.minhnhat.demo.features.investmentComplete.dto.response.InvestmentCompleteResponse;
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
        validateRequest(request);
        Context ctx = loadData(request);
        processBusiness(ctx);
        return createResponse(ctx);
    }

    private void validateRequest(InvestmentCompleteRequest request) {
        // Keep behavior unchanged: no strict request validation in current flow.
    }

    private Context loadData(InvestmentCompleteRequest request) {
        Context ctx = new Context();
        ctx.request = request;
        try {
            var cu = securityUtils.getCurrentUser();
            if (cu != null) {
                ctx.userId = cu.getUserId();
            }
        } catch (Exception e) {
            throw new RuntimeException("User must be authenticated to create investment plan", e);
        }
        return ctx;
    }

    private void processBusiness(Context ctx) {
        validateBusiness(ctx);
        prepare(ctx);
        execute(ctx);
        persist(ctx);
        afterProcess(ctx);
    }

    private void validateBusiness(Context ctx) {
        // Keep behavior unchanged: no additional business validation in current flow.
    }

    private void prepare(Context ctx) {
        InvestmentPlanEntity plan = new InvestmentPlanEntity();
        plan.setUserId(ctx.userId);
        plan.setTotalAmount(ctx.request.getAmount());
        ctx.plan = plan;
    }

    private void execute(Context ctx) {
        // Keep behavior unchanged: no in-memory execute step before persistence.
    }

    private void persist(Context ctx) {
        try {
            ctx.plan = planRepository.save(ctx.plan);
            log.info("Investment plan saved successfully: planId={}, userId={}", ctx.plan.getId(), ctx.plan.getUserId());
        } catch (Exception e) {
            log.error("Failed to save investment plan for userId={}", ctx.userId, e);
            throw new RuntimeException("Failed to save investment plan: " + e.getMessage(), e);
        }

        ctx.allocEntities = new ArrayList<>();
        for (var a : ctx.request.getAllocations()) {
            InvestmentPlanAllocationEntity allocation = new InvestmentPlanAllocationEntity();
            allocation.setPlanId(ctx.plan.getId());
            allocation.setAssetType(a.getType());
            allocation.setAmount(a.getAmount());
            allocation.setQuantity(a.getQuantity());
            allocation.setPercentage(a.getPercent());
            ctx.allocEntities.add(allocation);
        }
        allocationRepository.saveAll(ctx.allocEntities);

        ctx.responseAllocations = ctx.request.getAllocations().stream()
                .map(e -> {
                    BigDecimal residualAmount = e.getResidualAmount();
                    Optional<AssetResidualsEntity> residualOpt = assetResidualsRepository
                            .findByUserIdAndAssetType(ctx.userId, e.getType());

                    if (residualOpt.isPresent()) {
                        AssetResidualsEntity residual = residualOpt.get();
                        residual.setResidualAmount(residualAmount);
                        assetResidualsRepository.save(residual);
                        log.info("Updated asset residual: userId={}, assetType={}, residualAmount={}",
                                ctx.userId, e.getType(), residualAmount);
                    } else {
                        AssetResidualsEntity newResidual = AssetResidualsEntity.builder()
                                .userId(ctx.userId)
                                .assetType(e.getType())
                                .residualAmount(residualAmount)
                                .build();
                        assetResidualsRepository.save(newResidual);
                        log.info("Created new asset residual: userId={}, assetType={}, residualAmount={}",
                                ctx.userId, e.getType(), residualAmount);
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
    }

    private void afterProcess(Context ctx) {
        // No side-effects in current flow.
    }

    private InvestmentCompleteResponse createResponse(Context ctx) {
        return InvestmentCompleteResponse.builder()
                .sessionId(UUID.randomUUID().toString())
                .planId(ctx.plan.getId())
                .userId(ctx.plan.getUserId())
                .type(ctx.request.getType())
                .totalAmount(ctx.plan.getTotalAmount())
                .createdAt(ctx.plan.getCreatedAt())
                .allocations(ctx.responseAllocations)
                .build();
    }

    private static class Context {
        InvestmentCompleteRequest request;
        String userId;
        InvestmentPlanEntity plan;
        List<InvestmentPlanAllocationEntity> allocEntities;
        List<InvestmentCompleteResponse.Allocation> responseAllocations;
    }
}


