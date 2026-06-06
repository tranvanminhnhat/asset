package com.minhnhat.demo.features.investmentComplete.service;

import com.minhnhat.demo.features.investmentComplete.entity.InvestmentPlanEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetInvestmentPlansByStatusUseCaseService {

    private final InvestmentPlanService investmentPlanService;

    public List<InvestmentPlanEntity> execute(String userId) {
        return investmentPlanService.getPlansByStatus(userId);
    }
}

