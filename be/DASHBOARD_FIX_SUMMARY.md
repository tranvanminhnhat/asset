# Dashboard API Fix Summary

## 問題 (Problem)
Dashboard API (`GET /api/v1/dashboard`) was retrieving data from the legacy `investment_plan_allocations` table instead of the completed investment workflow data from the `portfolio` table. This resulted in:

1. **Incorrect data source**: Using `InvestmentPlanAllocationRepository` instead of actual finalized investments
2. **Invalid schema mapping**: Trying to join with `asset_prices` on `asset_type` when the new schema uses `product_id`
3. **Missing quantity calculation**: Portfolio quantity was 0 for non-EMERGENCY assets, preventing proper current value calculation
4. **Incomplete dashboard**: Not reflecting actual finalized investments from the investment workflow

## 解決策 (Solution)

### 1. **データソースの修正 (Fixed Data Source)**

#### Changes to `PortfolioRepository`:
- Added new method: `findDashboardDataByUserId(String userId)`
- This method implements a comprehensive query that:
  - Retrieves portfolio data (total_invested) from `portfolio` table
  - Aggregates quantities from `investment_asset_selections` 
  - Joins with `asset_categories` and `asset_products` to map asset types to products
  - Retrieves latest prices from `asset_prices` 
  - Groups by asset_type to avoid Cartesian products

```sql
SELECT 
    p.asset_type AS assetType,
    p.total_invested AS totalInvested,
    COALESCE(quantity_subq.totalQuantity, 0) AS totalQuantity,
    COALESCE(price_subq.latestPrice, 1) AS currentPrice
FROM portfolio p
LEFT JOIN (
  SELECT ac.code AS assetType, SUM(ias.quantity) AS totalQuantity
  FROM investment_asset_selections ias
  JOIN asset_products aprod ON ias.product_id = aprod.id
  JOIN asset_categories ac ON aprod.category_id = ac.id
  GROUP BY ac.code
) quantity_subq ON p.asset_type = quantity_subq.assetType
LEFT JOIN (
  SELECT ac.code AS assetType, MAX(ap.buy_price) AS latestPrice
  FROM asset_categories ac
  JOIN asset_products aprod ON ac.id = aprod.category_id
  JOIN asset_prices ap ON aprod.id = ap.product_id AND ap.is_latest = true
  GROUP BY ac.code
) price_subq ON p.asset_type = price_subq.assetType
WHERE p.user_id = :userId
```

### 2. **サービスの更新 (Service Updates)**

#### `GetCurrentUserDashboardService.java`:
- Changed dependency: `InvestmentPlanAllocationRepository` → `PortfolioRepository`
- Updated `buildDashboard()`: Now calls `portfolioRepository.findDashboardDataByUserId(userId)`
- Enhanced `calculatePortfolioItem()`:
  - Special handling for EMERGENCY fund:
    - quantity = total_invested (cash equivalent)
    - profit = 0 (no price appreciation)
    - profitRate = 0.00%
  - Standard calculation for other asset types:
    - currentValue = quantity × currentPrice
    - profit = currentValue - totalInvested
    - profitRate = (profit / totalInvested) × 100

#### `GetDashboardByUserIdService.java`:
- Applied same changes as `GetCurrentUserDashboardService`
- Ensures consistent behavior across both dashboard endpoints

### 3. **テストの更新 (Test Updates)**

#### `DashboardServiceTest.java`:
- Updated mocks: `InvestmentPlanAllocationRepository` → `PortfolioRepository`
- Updated test data field names: `totalAmount` → `totalInvested`
- All 6 test cases now pass:
  1. Empty data returns empty dashboard ✓
  2. Single asset type calculation ✓
  3. Multiple asset types aggregation ✓
  4. Null price handling ✓
  5. Zero invested cost handling ✓
  6. Negative profit (loss) calculation ✓

## データフロー (Data Flow)

### Investment Workflow → Dashboard:

```
1. User creates investment session
   ↓
2. User selects assets and quantities (PROCESS_ASSET step)
   → investment_asset_selections table created
   ↓
3. User finalizes investment (FINALIZE step)
   → portfolio table created/updated with:
      - user_id
      - asset_type
      - quantity (only for EMERGENCY = total_invested)
      - total_invested
   ↓
4. Dashboard queries:
   - portfolio: total_invested per asset_type
   - investment_asset_selections: SUM(quantity) per asset_type
   - asset_prices: current price per product
   ↓
5. Dashboard calculates:
   - currentValue = quantity × currentPrice
   - profit = currentValue - totalInvested
   - profitRate = (profit / totalInvested) × 100
```

## アセットタイプの特別処理 (Special Asset Type Handling)

### EMERGENCY Fund:
- **quantity**: Set to total_invested (cash equivalent)
- **currentPrice**: Always 1.0 (no price fluctuation)
- **profit**: Always 0 (cash, no gain/loss)
- **profitRate**: Always 0.00%

### Other Asset Types (GOLD, STOCK, ETF):
- **quantity**: Aggregated from `investment_asset_selections`
- **currentPrice**: Latest price from `asset_prices` via category
- **profit**: currentValue - totalInvested
- **profitRate**: (profit / totalInvested) × 100

## 影響 (Impact)

✓ Dashboard now displays **actual finalized portfolio data**
✓ Correct calculation of **current values** based on latest market prices
✓ Accurate **profit/loss** calculations reflecting real investments
✓ Proper handling of **EMERGENCY fund** (cash accumulation)
✓ Support for **multiple investment rounds** via quantity aggregation
✓ All existing tests pass

## テスト結果 (Test Results)

```
BUILD SUCCESSFUL in 20s
> Task :test
  ✓ testGetDashboard_EmptyData_ReturnsEmptyDashboard
  ✓ testGetDashboard_SingleAsset_CorrectCalculation
  ✓ testGetDashboard_MultipleAssets_CorrectAggregation
  ✓ testGetDashboard_NullPrice_HandledGracefully
  ✓ testGetDashboard_ZeroInvestedCost_SafelyHandled
  ✓ testGetDashboard_NegativeProfit_CorrectCalculation
```

## ファイルの変更 (Files Modified)

1. `src/main/java/com/minhnhat/demo/common/db/repository/PortfolioRepository.java`
   - Added `findDashboardDataByUserId()` with complex JOIN query

2. `src/main/java/com/minhnhat/demo/features/dashboard/service/GetCurrentUserDashboardService.java`
   - Changed repository dependency
   - Updated data mapping and calculations
   - Added EMERGENCY fund special handling

3. `src/main/java/com/minhnhat/demo/features/dashboard/service/GetDashboardByUserIdService.java`
   - Applied same changes as GetCurrentUserDashboardService

4. `src/test/java/com/minhnhat/demo/features/dashboard/service/DashboardServiceTest.java`
   - Updated all test mocks
   - Updated test data field names
   - All tests now passing

