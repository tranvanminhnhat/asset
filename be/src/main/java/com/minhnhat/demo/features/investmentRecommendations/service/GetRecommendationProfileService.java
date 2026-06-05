package com.minhnhat.demo.features.investmentRecommendations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetRecommendationProfileService {

    @Transactional(readOnly = true)
    public String execute(String userId) {
        log.debug("Fetching recommendation profile for user: {}", userId);
        return "PROFILE_PLACEHOLDER";
    }
}

