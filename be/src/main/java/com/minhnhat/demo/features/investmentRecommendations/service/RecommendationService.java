package com.minhnhat.demo.features.investmentRecommendations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Recommendation Service - Read-only service for investment recommendations
 * This service calculates recommendations based on user profile and market conditions
 * NO DATABASE WRITES in this service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    /**
     * Calculate investment recommendation (read-only, no DB writes)
     * This is a placeholder for business logic
     */
    @Transactional(readOnly = true)
    public String calculateRecommendation(String userId, BigDecimal investmentAmount) {
        log.debug("Calculating recommendation for user: {}, amount: {}", userId, investmentAmount);
        // TODO: Implement recommendation logic
        return "RECOMMENDATION_PLACEHOLDER";
    }

    /**
     * Get recommendation profile
     */
    @Transactional(readOnly = true)
    public String getRecommendationProfile(String userId) {
        log.debug("Fetching recommendation profile for user: {}", userId);
        // TODO: Implement profile fetch
        return "PROFILE_PLACEHOLDER";
    }
}

