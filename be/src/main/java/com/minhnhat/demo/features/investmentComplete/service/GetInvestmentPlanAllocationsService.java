package com.minhnhat.demo.features.investmentComplete.service;

import com.minhnhat.demo.common.db.entity.InvestmentPlanAllocationEntity;
import com.minhnhat.demo.common.db.repository.InvestmentPlanAllocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetInvestmentPlanAllocationsService {

    private final InvestmentPlanAllocationRepository allocationRepository;

    @Transactional(readOnly = true)
    public List<InvestmentPlanAllocationEntity> execute(Long planId) {
        log.debug("Fetching allocations for plan: {}", planId);
        return allocationRepository.findByPlanId(planId);
    }
}

