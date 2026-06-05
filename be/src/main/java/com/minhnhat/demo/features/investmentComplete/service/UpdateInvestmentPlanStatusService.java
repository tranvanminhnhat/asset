package com.minhnhat.demo.features.investmentComplete.service;

import com.minhnhat.demo.common.db.entity.InvestmentPlanEntity;
import com.minhnhat.demo.common.db.repository.InvestmentPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateInvestmentPlanStatusService {

    private final InvestmentPlanRepository investmentPlanRepository;

    @Transactional(rollbackFor = Exception.class)
    public InvestmentPlanEntity execute(Long planId, String newStatus) {
        log.info("Updating plan status - ID: {}, NewStatus: {}", planId, newStatus);

        InvestmentPlanEntity plan = investmentPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found: " + planId));

        InvestmentPlanEntity updated = investmentPlanRepository.save(plan);
        log.info("Plan status updated");
        return updated;
    }
}

