# Dashboard Fix - Visual Architecture Diagram

## 🏗️ Complete Data Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ CLIENT (Frontend)                                                           │
│ GET /api/v1/dashboard                                                      │
└─────────────────────┬───────────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────���───────────────────────────────────────────────┐
│ DashboardController.getDashboard()                                          │
│ ├─ Call: GetCurrentUserDashboardService.execute()                          │
│ └─ Return: ApiResponse<DashboardResponse>                                  │
└─────────────────────┬───────────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ GetCurrentUserDashboardService.execute()                                    │
│ ├─ getCurrentUser() → userId                                               │
│ └─ buildDashboard(userId)                                                  │
└─────────────────────┬───────────────────────────────────────────────────────┘
                      │
        ┌─────────────┴─────────────┐
        │                           │
        ▼                           ▼
    ┌─────────────────┐    ┌──────────────────────┐
    │📊 Portfolio DB  │    │🔄 Process Portfolio  │
    ├─────────────────┤    └──────────────────────┘
    │ user_id (PK)    │       For each portfolio:
    │ asset_type      │       │
    │ quantity        │       ├─ calculatePortfolioItem()
    │ total_invested  │       │  ├─ IF assetType == EMERGENCY
    │                 │       │  │  └─ currentValue = totalInvested
    │ Example:        │       │  │     profit = 0, rate = 0
    │ ─────────────── │       │  └─ ELSE
    │ GOLD    10.5    │       │     ├─ getCurrentPriceForAssetType()
    │ ETF    100.0    │       │     │
    │ STOCK   50.0    │       │     ├─ currentValue = qty × price
    │ EMERG 2250000   │       │     ├─ profit = currentValue - invested
    │                 │       │     └─ rate = (profit / invested) × 100
    └──────┬──────────┘       │
           │                  └─ Accumulate to totals
           │
           ├─────────────────────────────────────────────────────────┐
           │                                                         │
           ▼                                                         ▼
    ┌─────────────────────────────────────────────────────────────────┐
    │ Step 1: Get Price for Each Asset Type                          │
    │─────────────────────────────────────────────────────────────────│
    │                                                                 │
    │ getCurrentPriceForAssetType(assetType)                         │
    │ ┌──────────────────────────────────────────────────────────┐  │
    │ │ A. Get Actual Product Selections                        │  │
    │ │    selectionRepository.findByAssetType(assetType)      │  │
    │ │                                                          │  │
    │ │    Query: SELECT s FROM InvestmentAssetSelectionEntity │  │
    │ │            WHERE categoryCode = ?                       │  │
    │ │                                                          │  │
    │ │    Example for ETF:                                     │  │
    │ │    ┌────────────────────────────────────────────────┐   │  │
    │ │    │ productId │ quantity │ categoryCode │ buyPrice │   │  │
    │ │    ├────────────────────────────────────────────────┤   │  │
    │ │    │    21     │   50.00  │    ETF       │ 250000   │   │  │
    │ │    │    22     │   30.00  │    ETF       │ 270000   │   │  │
    │ │    │    23     │   20.00  │    ETF       │ 280000   │   │  │
    │ │    └─────────────────���──────────────────────────────┘   │  │
    │ └──────────────────────────────────────────────────────────┘  │
    │                          │                                     │
    │                          ▼                                     │
    │ ┌──────────────────────────────────────────────────────────┐  │
    │ │ B. Extract Product IDs                                  │  │
    │ │    productIds = {21, 22, 23}                            │  │
    │ └──────────────────────────────────────────────────────────┘  │
    │                          │                                     │
    │                          ▼                                     │
    │ ┌──────────────────────────────────────────────────────────┐  │
    │ │ C. Get Latest Prices for Those Products                 │  │
    │ │    assetPriceRepository.findAllLatestPrices()          │  │
    │ │    → Filter where productId IN {21, 22, 23}            │  │
    │ │                                                          │  │
    │ │    Current Market Prices:                              │  │
    │ │    ┌────────────────────────────────┐                  │  │
    │ │    │ productId │ is_latest │ price   │                  │  │
    │ │    ├────────────────────────────────┤                  │  │
    │ │    │    21     │    true   │ 265000  │                  │  │
    │ │    │    22     │    true   │ 285000  │                  │  │
    │ │    │    23     │    true   │ 295000  │                  │  │
    │ │    └────────────────────────────────┘                  │  │
    │ └──────────────────────────────────────────────────────────┘  │
    │                          │                                     │
    │                          ▼                                     │
    │ ┌──────────────────────────────────────────────────────────┐  │
    │ │ D. Calculate Weighted Average Price                     │  │
    │ │                                                          │  │
    │ │    Formula: weightedAvg = Σ(price × qty) / Σ(qty)     │  │
    │ │                                                          │  │
    │ │    Calculation:                                         │  │
    │ │    ─────────────────────────────────────────────────    │  │
    │ │    50 units @ 265,000 = 13,250,000                     │  │
    │ │    30 units @ 285,000 =  8,550,000                     │  │
    │ │    20 units @ 295,000 =  5,900,000                     │  │
    │ │                          ───────────                     │  │
    │ │    Total:               27,700,000                      │  │
    │ │                                                          │  │
    │ │    Weighted Average = 27,700,000 ÷ 100 = 277,000      │  │
    │ │                                                          │  │
    │ │    Return: 277,000 ✅                                  │  │
    │ └──────────────────────────────────────────────────────────┘  │
    │                                                                 │
    └─────────────────────────────────────────────────────────────────┘
           │
           ▼
    ┌─────────────────────┐
    │ Step 2: Calculate   │
    │ Current Value       │
    │                     │
    │ currentValue        │
    │   = quantity        │
    │   × weightedAvgPrice
    │   = 100 × 277,000   │
    │   = 27,700,000      │
    │                     │
    │ profit              │
    │   = 27,700,000      │
    │   - 26,200,000      │
    │   = 1,500,000       │
    │                     │
    │ profitRate          │
    │   = (1,500,000)     │
    │   / 26,200,000      │
    │   × 100             │
    │   = 5.73%           │
    └─────────────────────┘
           │
           ▼
    ┌─────────────────────────────────────────────┐
    │ Step 3: Build Response                      │
    │                                             │
    │ Return PortfolioItem {                      │
    │   assetType: "ETF",                         │
    │   quantity: 100.00,                         │
    │   investedCost: 26200000.00,               │
    │   currentValue: 27700000.00,               │
    │   profit: 1500000.00,                      │
    │   profitRate: 5.73                         │
    │ }                                           │
    └─────────────────────────────────────────────┘
```

---

## 🔄 Database Tables Involved

```
┌──────────────────────────────────────────────────────────────┐
│ DATABASE TABLES                                              │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│ 📊 portfolio (Aggregated Portfolio)                         │
│ ────────────────────────────────────────────────────────────│
│ │ user_id │ asset_type │ quantity │ total_invested         │
│ ├─────────┼────────────┼──────────┼───────────────────────│
│ │ user001 │ GOLD       │ 10.50    │ 5,250,000            │
│ │ user001 │ ETF        │ 100.00   │ 26,200,000           │
│ │ user001 │ STOCK      │ 50.00    │ 7,500,000            │
│ │ user001 │ EMERGENCY  │ 2250000  │ 2,250,000            │
│ └─────────┴────────────┴──────────┴───────────────────────│
│                    ▲                                        │
│                    │                                        │
│    Links to selections via assetType                       │
│                    │                                        │
│                    ▼                                        │
│ 📋 investment_asset_selections (Details)                   │
│ ────────────────────────────────────────────────────────────│
│ │ productId │ quantity │ categoryCode │ buyPrice           │
│ ├───────────┼──────────┼──────────────┼────────────────────│
│ │ 21        │ 50.00    │ ETF          │ 260,000           │
│ │ 22        │ 30.00    │ ETF          │ 280,000           │
│ │ 23        │ 20.00    │ ETF          │ 290,000           │
│ │ ... more selections ...                                  │
│ └───────────┴──────────┴──────────────┴────────────────────│
│                    ▲                                        │
│                    │                                        │
│    Links to prices via productId                           │
│                    │                                        │
│                    ▼                                        │
│ 💰 asset_prices (Market Data - Latest)                    │
│ ────────────────────────────────────────────────────────────│
│ │ productId │ buyPrice  │ sellPrice │ is_latest           │
│ ├───────────┼───────────┼───────────┼─────────────────────│
│ │ 21        │ 265,000   │ 264,500   │ true               │
│ │ 22        │ 285,000   │ 284,500   │ true               │
│ │ 23        │ 295,000   │ 294,500   │ true               │
│ │ ... more prices ...                                     │
│ └───────────┴───────────┴───────────┴─────────────────────│
│                    ▲                                        │
│                    │                                        │
│    Links to products via productId                         │
│                    │                                        │
│                    ▼                                        │
│ 📦 asset_products (Product Master)                         │
│ ────────────────────────────────────────────────────────────│
│ │ id │ code    │ name            │ categoryId             │
│ ├────┼─────────┼─────────────────┼──────────────────────│
│ │ 21 │ ETF_A   │ Index Fund A     │ 2 (ETF)             │
│ │ 22 │ ETF_B   │ Index Fund B     │ 2 (ETF)             │
│ │ 23 │ ETF_C   │ Index Fund C     │ 2 (ETF)             │
│ │ ... more products ...                                   │
│ └────┴─────────┴─────────────────┴──────────────────────│
│                                                              │
│ 🏷️  asset_categories (Category Master)                     │
│ ────────────────────────────────────────────────────────────│
│ │ id │ code    │ name                                      │
│ ├────┼─────────┼─────────────────────────────────────────│
│ │ 1  │ GOLD    │ Gold Assets                             │
│ │ 2  │ ETF     │ Exchange Traded Funds                   │
│ │ 3  │ STOCK   │ Stock Holdings                          │
│ │ 4  │ EMERGENCY│ Emergency Fund (Cash)                   │
│ └────┴─────────┴─────────────────────────────────────────│
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

---

## 🔗 Query Sequence

```
Step 1: User calls GET /api/v1/dashboard
        └─ Auth: Get userId from SecurityUtils

Step 2: Load Portfolio Data
        Query: SELECT * FROM portfolio WHERE user_id = 'user001'
        Result: 4 portfolio rows (GOLD, ETF, STOCK, EMERGENCY)

Step 3: For Each Portfolio Row (Example: ETF with 100 units)
        
        A. Find Product Selections
           Query: SELECT * FROM investment_asset_selections
                  WHERE categoryCode = 'ETF'
           Result: 3 rows with productIds 21, 22, 23

        B. Get Latest Prices
           Query: SELECT * FROM asset_prices
                  WHERE is_latest = true
           Filter: Keep only productId IN (21, 22, 23)
           Result: 3 price rows (265k, 285k, 295k)

        C. Calculate Weighted Average
           Math: (50×265k + 30×285k + 20×295k) / 100 = 277k

        D. Calculate Current Value
           Math: 100 units × 277k = 27.7M

        E. Calculate Profit
           Math: 27.7M - 26.2M = 1.5M
           Math: (1.5M / 26.2M) × 100 = 5.73%

Step 4: Build Response
        Combine all portfolio items with summary totals

Step 5: Return JSON Response
        └─ DashboardResponse with summary + portfolio list
```

---

## 📈 Comparison: OLD vs NEW

```
OLD DASHBOARD LOGIC (WRONG)
════════════════════════════════════════════════════════════════

For ETF (100 units, 26.2M invested):
   1. Get ALL latest prices globally
      → Returns 100+ prices from GOLD, ETF, STOCK, BONDS, etc.
   
   2. Find MAX price
      → 295,000 (happens to be from ETF_C)
   
   3. Calculate currentValue
      → 100 × 295,000 = 29.5M ❌ WRONG (overestimated)
   
   Result: Shows user made 12.4% profit instead of 5.73%
           Portfolio appears worth more than it actually is


NEW DASHBOARD LOGIC (CORRECT)
════════════════════════════════════════════════════════════════

For ETF (100 units, 26.2M invested):
   1. Get actual selections for ETF
      → 3 selections: productIds 21, 22, 23
   
   2. Get prices for ONLY those products
      → 265k, 285k, 295k (weighted by quantities)
   
   3. Calculate weighted average price
      → (50×265k + 30×285k + 20×295k) / 100 = 277k
   
   4. Calculate currentValue
      → 100 × 277,000 = 27.7M ✅ CORRECT
   
   Result: Shows user made 5.73% profit (accurate)
           Portfolio value correctly represents reality
```

---

## 🎯 Key Design Principles

```
┌──────────���──────────────────────────────────────────────────┐
│ Design Principles Applied                                   │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│ 1. Accuracy First                                           │
│    ├─ Use actual data (selections), not assumptions         │
│    └─ Weighted average reflects reality                     │
│                                                             │
│ 2. Single Responsibility                                    │
│    ├─ Portfolio = aggregate storage                         │
│    ├─ Selections = transaction details                      │
│    └─ Prices = market data                                  │
│                                                             │
│ 3. Query Optimization                                       │
│    ├─ Filter to relevant products early                     │
│    ├─ Avoid full table scans                                │
│    └─ Efficient JPQL queries                                │
│                                                             │
│ 4. Error Handling                                           │
│    ├─ Graceful fallbacks (price = 1.0)                      │
│    ├─ Comprehensive logging                                 │
│    └─ User-friendly empty states                            │
│                                                             │
│ 5. Special Cases                                            │
│    ├─ EMERGENCY requires no price lookup                    │
│    ├─ Cash doesn't appreciate                               │
│    └─ Always 0% profit for cash                             │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

**Visual Last Updated:** June 12, 2026  
**For Technical Details:** See DASHBOARD_FIX_DOCUMENTATION.md  
**For Quick Reference:** See DASHBOARD_FIX_QUICK_REFERENCE.md

