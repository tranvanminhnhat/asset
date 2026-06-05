package com.minhnhat.demo.features.portfolio.service;

import com.minhnhat.demo.common.db.entity.PortfolioEntity;
import com.minhnhat.demo.common.db.repository.PortfolioRepository;
import com.minhnhat.demo.features.portfolio.dto.response.PortfolioSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetPortfolioByIdService {

    private final PortfolioRepository portfolioRepository;

    @Transactional(readOnly = true)
    public Optional<PortfolioSummaryResponse> execute(Long portfolioId) {
        log.debug("Fetching portfolio with ID: {}", portfolioId);
        return portfolioRepository.findById(portfolioId)
                .map(this::createResponse);
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

