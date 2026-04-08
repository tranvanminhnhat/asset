package com.minhnhat.demo.features.dashboard.service;

import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.common.security.dto.CurrentUserInfo;
import com.minhnhat.demo.features.dashboard.dto.response.DashboardResponse;
import com.minhnhat.demo.features.portfolio.entity.PortfolioEntity;
import com.minhnhat.demo.features.portfolio.repository.PortfolioRepository;
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

    private final SecurityUtils currentUserService;

    public DashboardResponse getDashboard() {

        List<PortfolioEntity> portfolioEntity = loadData();

        return createResponse(portfolioEntity);
    }

    private List<PortfolioEntity> loadData(){
//        CurrentUserInfo currentUser = currentUserService.getCurrentUser();
        return portfolioRepository.findByUserId("1");
    }

    private DashboardResponse createResponse(List<PortfolioEntity> portfolioEntities){
        if (portfolioEntities == null || portfolioEntities.isEmpty()) {
            assert portfolioEntities != null;
            return DashboardResponse.builder()
                    .summary(createSummary(portfolioEntities))
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
                        .id(entity.getId())
                        .name(entity.getName())
                        .type(entity.getType())
                        .purchaseValue(entity.getPurchaseValue())
                        .currentValue(entity.getCurrentValue())
                        .profit(entity.getCurrentValue().subtract(entity.getPurchaseValue()))
                        .isProfit(entity.getCurrentValue().compareTo(entity.getPurchaseValue()) > 0)
                        .build())
                .toList();
    }

    private DashboardResponse.Summary createSummary(List<PortfolioEntity> portfolioEntities){

        BigDecimal totalCurrentValue = portfolioEntities.stream()
                .map(PortfolioEntity::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPurchaseValue = portfolioEntities.stream()
                .map(PortfolioEntity::getPurchaseValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalProfit = totalCurrentValue.subtract(totalPurchaseValue);

        BigDecimal profitPercentage = totalPurchaseValue.compareTo(BigDecimal.ZERO) > 0
                ? totalProfit.divide(totalPurchaseValue, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        return DashboardResponse.Summary.builder()
                .totalCurrentValue(totalCurrentValue)
                .totalPurchaseValue(totalPurchaseValue)
                .totalProfit(totalProfit)
                .profitPercentage(profitPercentage)
                .currency("USD")
                .build();
    }
}


