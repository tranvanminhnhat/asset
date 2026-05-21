package com.minhnhat.demo.features.investmentComplete.service;

import com.minhnhat.demo.common.db.entity.InvestmentPlanEntity;
import com.minhnhat.demo.common.db.repository.InvestmentPlanRepository;
import com.minhnhat.demo.features.investmentComplete.dto.response.InvestmentPlanResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserInvestmentPlansService {

    private final InvestmentPlanRepository investmentPlanRepository;

    @Transactional(readOnly = true)
    public List<InvestmentPlanResponse> execute(String userId) {
        log.debug("Fetching plans for user: {}", userId);
        return investmentPlanRepository.findByUserIdWithDetails(userId).stream()
                .map(this::createResponse)
                .toList();
    }

    private InvestmentPlanResponse createResponse(InvestmentPlanEntity entity) {
        List<InvestmentPlanResponse.AllocationDetail> allocations = entity.getAllocations().stream()
                .map(a -> InvestmentPlanResponse.AllocationDetail.builder()
                        .allocationId(a.getId())
                        .assetType(a.getAssetType())
                        .amount(a.getAmount())
                        .percentage(a.getPercentage())
                        .build())
                .toList();

        return InvestmentPlanResponse.builder()
                .planId(entity.getId())
                .userId(entity.getUserId())
                .totalAmount(entity.getTotalAmount())
                .allocations(allocations)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

