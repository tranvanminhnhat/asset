package com.minhnhat.demo.features.dashboard.service;

import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.common.security.dto.CurrentUserInfo;
import com.minhnhat.demo.features.dashboard.dto.response.DashboardResponse;
import com.minhnhat.demo.features.portfolio.entity.PortfolioEntity;
import com.minhnhat.demo.features.portfolio.repository.PortfolioRepository;
import com.minhnhat.demo.features.portfolio.repository.PortfolioTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetDashboardService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioTransactionRepository transactionRepository;
    private final SecurityUtils currentUserService;

    public DashboardResponse getDashboard() {
        List<PortfolioEntity> portfolioEntities = loadData();
        return createResponse(portfolioEntities);
    }

    private List<PortfolioEntity> loadData() {
        CurrentUserInfo currentUser = currentUserService.getCurrentUser();
        return portfolioRepository.findByUserId(currentUser.getUserId());
    }

    private DashboardResponse createResponse(List<PortfolioEntity> portfolioEntities) {
        if (portfolioEntities == null || portfolioEntities.isEmpty()) {
            return DashboardResponse.builder()
                    .summary(createEmptySummary())
                    .portfolios(List.of())
                    .build();
        }
        return DashboardResponse.builder()
                .summary(createSummary(portfolioEntities))
                .portfolios(createPortfolios(portfolioEntities))
                .build();
    }

    private List<DashboardResponse.PortfolioItem> createPortfolios(List<PortfolioEntity> portfolioEntities) {
        return portfolioEntities.stream()
                .map(entity -> DashboardResponse.PortfolioItem.builder()
                        .portfolioId(entity.getId())
                        .assetType(entity.getAssetType())
                        .quantity(entity.getQuantity())
                        .totalInvested(entity.getTotalInvested())
                        .createdAt(entity.getCreatedAt())
                        .updatedAt(entity.getUpdatedAt())
                        .build())
                .toList();
    }

    private DashboardResponse.Summary createSummary(List<PortfolioEntity> portfolioEntities) {
        BigDecimal totalInvested = portfolioEntities.stream()
                .map(PortfolioEntity::getTotalInvested)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalAssetTypes = portfolioEntities.size();
        long totalTransactions = transactionRepository.findAll().size();

        return DashboardResponse.Summary.builder()
                .totalInvested(totalInvested)
                .totalAssetTypes(totalAssetTypes)
                .totalTransactions(totalTransactions)
                .currency("USD")
                .build();
    }

    private DashboardResponse.Summary createEmptySummary() {
        return DashboardResponse.Summary.builder()
                .totalInvested(BigDecimal.ZERO)
                .totalAssetTypes(0L)
                .totalTransactions(0L)
                .currency("USD")
                .build();
    }
}


