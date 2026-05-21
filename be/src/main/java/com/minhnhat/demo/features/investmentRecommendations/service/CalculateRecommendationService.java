package com.minhnhat.demo.features.investmentRecommendations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculateRecommendationService {

    @Transactional(readOnly = true)
    public String execute(String userId, BigDecimal investmentAmount) {
        log.debug("Calculating recommendation for user: {}, amount: {}", userId, investmentAmount);
        return "RECOMMENDATION_PLACEHOLDER";
    }
}

