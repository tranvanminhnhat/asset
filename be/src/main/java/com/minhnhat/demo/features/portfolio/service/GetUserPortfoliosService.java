package com.minhnhat.demo.features.portfolio.service;

import com.minhnhat.demo.common.db.entity.PortfolioEntity;
import com.minhnhat.demo.common.db.repository.PortfolioRepository;
import com.minhnhat.demo.features.portfolio.dto.response.PortfolioSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserPortfoliosService {

    private final PortfolioRepository portfolioRepository;

    @Transactional(readOnly = true)
    public List<PortfolioSummaryResponse> execute(String userId) {
        log.debug("Fetching portfolios for user: {}", userId);
        return portfolioRepository.findByUserId(userId).stream()
                .map(this::createResponse)
                .toList();
    }

    private PortfolioSummaryResponse createResponse(PortfolioEntity entity) {
        return PortfolioSummaryResponse.builder()
                .portfolioId(entity.getId())
                .assetType(entity.getAssetType())
                .quantity(entity.getQuantity())
                .totalInvested(entity.getTotalInvested())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

