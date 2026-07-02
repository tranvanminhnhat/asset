package com.minhnhat.demo.features.dashboard.service;

import com.minhnhat.demo.common.db.entity.InvestmentAssetSelectionEntity;
import com.minhnhat.demo.common.db.entity.PortfolioEntity;
import com.minhnhat.demo.common.db.repository.AssetPriceRepository;
import com.minhnhat.demo.common.db.repository.InvestmentAssetSelectionRepository;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.common.security.dto.CurrentUserInfo;
import com.minhnhat.demo.features.dashboard.dto.response.DashboardResponse;
import com.minhnhat.demo.common.db.repository.PortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Dashboard Service Test
 *
 * Test cases for financial calculations and data aggregation
 */
@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private AssetPriceRepository assetPriceRepository;

    @Mock
    private InvestmentAssetSelectionRepository selectionRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private GetCurrentUserDashboardService dashboardService;

    private static final String TEST_USER_ID = "test-user-123";

    @BeforeEach
    void setUp() {
        CurrentUserInfo currentUser = CurrentUserInfo.builder()
                .userId(TEST_USER_ID)
                .email("test@example.com")
                .build();
        when(securityUtils.getCurrentUser()).thenReturn(currentUser);
        // Default mock: no selections (not used with buyPrice logic)
        when(selectionRepository.findByAssetType(anyString()))
                .thenReturn(new ArrayList<>());
    }

    /**
     * Test case 1: Empty data returns empty dashboard
     */
    @Test
    public void testGetDashboard_EmptyData_ReturnsEmptyDashboard() {
        when(portfolioRepository.findByUserId(TEST_USER_ID))
                .thenReturn(new ArrayList<>());

        DashboardResponse response = dashboardService.execute();

        assertNotNull(response);
        assertNotNull(response.getSummary());
        assertEquals(BigDecimal.ZERO, response.getSummary().getTotalInvestedCost());
        assertEquals(BigDecimal.ZERO, response.getSummary().getTotalCurrentValue());
        assertEquals(0, response.getPortfolios().size());
    }

    /**
     * Test case 2: Single asset type calculation
     * When buyPrice is null, use cost-basis: currentValue = totalInvested → profit = 0
     */
    @Test
    public void testGetDashboard_SingleAsset_CorrectCalculation() {
        List<PortfolioEntity> mockData = new ArrayList<>();
        PortfolioEntity portfolio = PortfolioEntity.builder()
                .id(1L)
                .userId(TEST_USER_ID)
                .assetType("STOCK")
                .quantity(new BigDecimal("100"))
                .totalInvested(new BigDecimal("10000.00"))
                .buyPrice(null)  // Backward compatibility: null means calculate from cost basis
                .build();
        mockData.add(portfolio);

        when(portfolioRepository.findByUserId(TEST_USER_ID))
                .thenReturn(mockData);

        DashboardResponse response = dashboardService.execute();

        assertNotNull(response);
        assertEquals(1, response.getPortfolios().size());

        DashboardResponse.PortfolioItem item = response.getPortfolios().get(0);
        assertEquals("STOCK", item.getAssetType());
        assertEquals(new BigDecimal("100.00"), item.getQuantity());
        assertEquals(new BigDecimal("10000.00"), item.getInvestedCost());
        // When buyPrice is null, currentValue = totalInvested → profit = 0
        assertEquals(new BigDecimal("10000.00"), item.getCurrentValue());
        assertEquals(BigDecimal.ZERO, item.getProfit());
    }

    /**
     * Test case 3: Multiple asset types aggregation
     * When buyPrice is null, use cost-basis: currentValue = totalInvested → profit = 0
     */
    @Test
    public void testGetDashboard_MultipleAssets_CorrectAggregation() {
        List<PortfolioEntity> mockData = new ArrayList<>();

        // STOCK: 100 units @ cost basis = 100 (invested 10,000) → currentValue = 10,000, profit = 0
        PortfolioEntity stock = PortfolioEntity.builder()
                .id(1L)
                .userId(TEST_USER_ID)
                .assetType("STOCK")
                .quantity(new BigDecimal("100"))
                .totalInvested(new BigDecimal("10000.00"))
                .buyPrice(null)
                .build();
        mockData.add(stock);

        // GOLD: 50 units @ cost basis = 1800 (invested 90,000) → currentValue = 90,000, profit = 0
        PortfolioEntity gold = PortfolioEntity.builder()
                .id(2L)
                .userId(TEST_USER_ID)
                .assetType("GOLD")
                .quantity(new BigDecimal("50"))
                .totalInvested(new BigDecimal("90000.00"))
                .buyPrice(null)
                .build();
        mockData.add(gold);

        when(portfolioRepository.findByUserId(TEST_USER_ID))
                .thenReturn(mockData);

        DashboardResponse response = dashboardService.execute();

        assertEquals(2, response.getPortfolios().size());

        // Total invested = 10,000 + 90,000 = 100,000
        assertEquals(new BigDecimal("100000.00"), response.getSummary().getTotalInvestedCost());

        // Total current = 10,000 + 90,000 = 100,000 (cost basis = invested)
        assertEquals(new BigDecimal("100000.00"), response.getSummary().getTotalCurrentValue());

        // Total profit = 0 (when buyPrice is null, currentValue = totalInvested)
        assertEquals(BigDecimal.ZERO, response.getSummary().getTotalProfit());
    }

    /**
     * Test case 4: Null price handling
     * When buyPrice is null, use cost-basis calculation: currentValue = totalInvested
     */
    @Test
    public void testGetDashboard_NullPrice_HandledGracefully() {
        List<PortfolioEntity> mockData = new ArrayList<>();
        PortfolioEntity portfolio = PortfolioEntity.builder()
                .id(1L)
                .userId(TEST_USER_ID)
                .assetType("ETF")
                .quantity(new BigDecimal("50"))
                .totalInvested(new BigDecimal("5000.00"))
                .buyPrice(null)  // Null buyPrice should fallback to cost-basis
                .build();
        mockData.add(portfolio);

        when(portfolioRepository.findByUserId(TEST_USER_ID))
                .thenReturn(mockData);

        DashboardResponse response = dashboardService.execute();

        // Should still process successfully without errors
        assertNotNull(response);
        assertEquals(1, response.getPortfolios().size());

        DashboardResponse.PortfolioItem item = response.getPortfolios().get(0);
        assertEquals(new BigDecimal("5000.00"), item.getCurrentValue()); // cost-basis: 5000/50 * 50 = 5000
        assertEquals(BigDecimal.ZERO, item.getProfit());  // profit = 5000 - 5000 = 0
    }

    /**
     * Test case 5: Division by zero handling
     */
    @Test
    public void testGetDashboard_ZeroInvestedCost_SafelyHandled() {
        List<PortfolioEntity> mockData = new ArrayList<>();
        PortfolioEntity portfolio = PortfolioEntity.builder()
                .id(1L)
                .userId(TEST_USER_ID)
                .assetType("EMERGENCY")
                .quantity(BigDecimal.ZERO)
                .totalInvested(BigDecimal.ZERO)
                .build();
        mockData.add(portfolio);

        when(portfolioRepository.findByUserId(TEST_USER_ID))
                .thenReturn(mockData);

        DashboardResponse response = dashboardService.execute();

        assertNotNull(response);
        // profitRate should be 0 when investedCost is 0
        assertEquals(BigDecimal.ZERO, response.getSummary().getTotalProfitRate());
    }

    /**
     * Test case 6: Negative profit (loss) calculation
     * When buyPrice is set to a value lower than cost-basis, generates negative profit
     */
    @Test
    public void testGetDashboard_NegativeProfit_CorrectCalculation() {
        List<PortfolioEntity> mockData = new ArrayList<>();
        // Invested 12,000 for 100 units = 120 cost per unit
        // But buying price was 100 = loss of 20 per unit = -2,000 total
        PortfolioEntity portfolio = PortfolioEntity.builder()
                .id(1L)
                .userId(TEST_USER_ID)
                .assetType("STOCK")
                .quantity(new BigDecimal("100"))
                .totalInvested(new BigDecimal("12000.00"))
                .buyPrice(new BigDecimal("100.00"))  // Price dropped below cost basis
                .build();
        mockData.add(portfolio);

        when(portfolioRepository.findByUserId(TEST_USER_ID))
                .thenReturn(mockData);

        DashboardResponse response = dashboardService.execute();

        DashboardResponse.PortfolioItem item = response.getPortfolios().get(0);

        // currentValue = 100 * 100 = 10,000
        assertEquals(new BigDecimal("10000.00"), item.getCurrentValue());

        // profit = 10,000 - 12,000 = -2,000 (loss)
        assertEquals(new BigDecimal("-2000.00"), item.getProfit());

        // profitRate should be negative
        assertTrue(item.getProfitRate().compareTo(BigDecimal.ZERO) < 0);
    }
}

