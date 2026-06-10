package com.minhnhat.demo.features.dashboard.service;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    }

    /**
     * Test case 1: Empty data returns empty dashboard
     */
    @Test
    public void testGetDashboard_EmptyData_ReturnsEmptyDashboard() {
        when(portfolioRepository.findDashboardDataByUserId(TEST_USER_ID))
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
     */
    @Test
    public void testGetDashboard_SingleAsset_CorrectCalculation() {
        List<Map<String, Object>> mockData = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("assetType", "STOCK");
        row.put("totalQuantity", new BigDecimal("100"));           // 100 units
        row.put("totalInvested", new BigDecimal("10000.00"));      // invested 10,000
        row.put("currentPrice", new BigDecimal("120.00"));         // current price 120 per unit
        mockData.add(row);

        when(portfolioRepository.findDashboardDataByUserId(TEST_USER_ID))
                .thenReturn(mockData);

        DashboardResponse response = dashboardService.execute();

        assertNotNull(response);
        assertEquals(1, response.getPortfolios().size());

        DashboardResponse.PortfolioItem item = response.getPortfolios().get(0);
        assertEquals("STOCK", item.getAssetType());
        assertEquals(new BigDecimal("100.00"), item.getQuantity());
        assertEquals(new BigDecimal("10000.00"), item.getInvestedCost());

        // currentValue = 100 * 120 = 12,000
        assertEquals(new BigDecimal("12000.00"), item.getCurrentValue());

        // profit = 12,000 - 10,000 = 2,000
        assertEquals(new BigDecimal("2000.00"), item.getProfit());

        // profitRate = (2,000 / 10,000) * 100 = 20.00%
        assertEquals(new BigDecimal("20.00"), item.getProfitRate());

        // Summary should match
        assertEquals(new BigDecimal("10000.00"), response.getSummary().getTotalInvestedCost());
        assertEquals(new BigDecimal("12000.00"), response.getSummary().getTotalCurrentValue());
        assertEquals(new BigDecimal("2000.00"), response.getSummary().getTotalProfit());
        assertEquals(new BigDecimal("20.00"), response.getSummary().getTotalProfitRate());
    }

    /**
     * Test case 3: Multiple asset types aggregation
     */
    @Test
    public void testGetDashboard_MultipleAssets_CorrectAggregation() {
        List<Map<String, Object>> mockData = new ArrayList<>();

        // STOCK: 100 units @ 120 = 12,000 current (invested 10,000)
        Map<String, Object> stock = new HashMap<>();
        stock.put("assetType", "STOCK");
        stock.put("totalQuantity", new BigDecimal("100"));
        stock.put("totalInvested", new BigDecimal("10000.00"));
        stock.put("currentPrice", new BigDecimal("120.00"));
        mockData.add(stock);

        // GOLD: 50 units @ 2000 = 100,000 current (invested 90,000)
        Map<String, Object> gold = new HashMap<>();
        gold.put("assetType", "GOLD");
        gold.put("totalQuantity", new BigDecimal("50"));
        gold.put("totalInvested", new BigDecimal("90000.00"));
        gold.put("currentPrice", new BigDecimal("2000.00"));
        mockData.add(gold);

        when(portfolioRepository.findDashboardDataByUserId(TEST_USER_ID))
                .thenReturn(mockData);

        DashboardResponse response = dashboardService.execute();

        assertEquals(2, response.getPortfolios().size());

        // Total invested = 10,000 + 90,000 = 100,000
        assertEquals(new BigDecimal("100000.00"), response.getSummary().getTotalInvestedCost());

        // Total current = 12,000 + 100,000 = 112,000
        assertEquals(new BigDecimal("112000.00"), response.getSummary().getTotalCurrentValue());

        // Total profit = 112,000 - 100,000 = 12,000
        assertEquals(new BigDecimal("12000.00"), response.getSummary().getTotalProfit());

        // Total profit rate = (12,000 / 100,000) * 100 = 12.00%
        assertEquals(new BigDecimal("12.00"), response.getSummary().getTotalProfitRate());
    }

    /**
     * Test case 4: Null price handling
     */
    @Test
    public void testGetDashboard_NullPrice_HandledGracefully() {
        List<Map<String, Object>> mockData = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("assetType", "ETF");
        row.put("totalQuantity", new BigDecimal("50"));
        row.put("totalInvested", new BigDecimal("5000.00"));
        row.put("currentPrice", null);  // No price data
        mockData.add(row);

        when(portfolioRepository.findDashboardDataByUserId(TEST_USER_ID))
                .thenReturn(mockData);

        DashboardResponse response = dashboardService.execute();

        // Should still process, treating null price as 0
        assertNotNull(response);
        assertEquals(1, response.getPortfolios().size());
    }

    /**
     * Test case 5: Division by zero handling
     */
    @Test
    public void testGetDashboard_ZeroInvestedCost_SafelyHandled() {
        List<Map<String, Object>> mockData = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("assetType", "EMERGENCY");
        row.put("totalQuantity", BigDecimal.ZERO);
        row.put("totalInvested", BigDecimal.ZERO);  // Zero invested
        row.put("currentPrice", new BigDecimal("1.00"));
        mockData.add(row);

        when(portfolioRepository.findDashboardDataByUserId(TEST_USER_ID))
                .thenReturn(mockData);

        DashboardResponse response = dashboardService.execute();

        assertNotNull(response);
        // profitRate should be 0 when investedCost is 0
        assertEquals(BigDecimal.ZERO, response.getSummary().getTotalProfitRate());
    }

    /**
     * Test case 6: Negative profit (loss) calculation
     */
    @Test
    public void testGetDashboard_NegativeProfit_CorrectCalculation() {
        List<Map<String, Object>> mockData = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("assetType", "STOCK");
        row.put("totalQuantity", new BigDecimal("100"));
        row.put("totalInvested", new BigDecimal("12000.00"));  // invested 12,000
        row.put("currentPrice", new BigDecimal("100.00"));   // current price 100 (loss)
        mockData.add(row);

        when(portfolioRepository.findDashboardDataByUserId(TEST_USER_ID))
                .thenReturn(mockData);

        DashboardResponse response = dashboardService.execute();

        DashboardResponse.PortfolioItem item = response.getPortfolios().get(0);

        // currentValue = 100 * 100 = 10,000
        assertEquals(new BigDecimal("10000.00"), item.getCurrentValue());

        // profit = 10,000 - 12,000 = -2,000 (loss)
        assertEquals(new BigDecimal("-2000.00"), item.getProfit());

        // profitRate = (-2,000 / 12,000) * 100 = -16.67%
        assertTrue(item.getProfitRate().compareTo(BigDecimal.ZERO) < 0);
    }
}

