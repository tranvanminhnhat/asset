# ALLOCATION STRATEGY - DATA FLOW DIAGRAM

## 📊 Database Schema (NEW)

```
┌─────────────────────────────────────────┐
│   mst_allocation_strategies             │
├─────────────────────────────────────────┤
│ PK: id (BIGINT)                         │
│    code (VARCHAR 30) UNIQUE ← INDEXED   │
│    name (VARCHAR 100)                   │
│    created_at, updated_at               │
└────────────┬────────────────────────────┘
             │
             │ 1:N Relationship
             │
             ↓
┌─────────────────────────────────────────┐
│   mst_allocation_strategy_details       │
├─────────────────────────────────────────┤
│ PK: id (BIGINT)                         │
│ FK: strategy_id (BIGINT) ← Indexed      │
│    asset_type (VARCHAR 50)              │
│    percentage (DECIMAL 5,2)             │
│    strategy_code (VARCHAR 45)           │
│    created_at, updated_at               │
└─────────────────────────────────────────┘
```

---

## 🔄 Service Flow

```
User Request
    ↓
    │ POST /investment-amount
    │ {
    │   "amount": 50000,
    │   "type": "BALANCED"
    │ }
    ↓
InvestmentRecommendationsService.allocate()
    ├─→ validateRequest()
    │   ├─ Check amount > 0 ✓
    │   └─ Check type not empty ✓
    │
    ├─→ loadData()
    │   │
    │   └─ strategyRepository.findByCodeWithDetails("BALANCED")
    │       │
    │       └─ Query:
    │           SELECT s FROM AllocationStrategyEntity s
    │           LEFT JOIN FETCH s.details
    │           WHERE s.code = 'BALANCED'
    │       │
    │       ↓ Returns:
    │           AllocationStrategyEntity {
    │               id: 2,
    │               code: "BALANCED",
    │               name: "Chiến lược Cân Bằng",
    │               details: [
    │                   AllocationStrategyDetailEntity {
    │                       id: 5, strategyId: 2,
    │                       assetType: "EMERGENCY", percentage: 0.25
    │                   },
    │                   AllocationStrategyDetailEntity {
    │                       id: 6, strategyId: 2,
    │                       assetType: "GOLD", percentage: 0.20
    │                   },
    │                   AllocationStrategyDetailEntity {
    │                       id: 7, strategyId: 2,
    │                       assetType: "ETF", percentage: 0.20
    │                   },
    │                   AllocationStrategyDetailEntity {
    │                       id: 8, strategyId: 2,
    │                       assetType: "STOCK", percentage: 0.35
    │                   }
    │               ]
    │           }
    │
    ├─→ executeBusinessLogic()
    │   │
    │   ├─ Map<String, BigDecimal> assetPercentages
    │   │   = {
    │   │       "EMERGENCY": 0.25,
    │   │       "GOLD": 0.20,
    │   │       "ETF": 0.20,
    │   │       "STOCK": 0.35
    │   │     }
    │   │
    │   ├─ Normalize percentages (sum = 1.0) ✓
    │   │
    │   ├─ Calculate amounts:
    │   │   ├─ amountEmergency = 50000 × 0.25 = 12500
    │   │   ├─ amountGold = 50000 × 0.20 = 10000
    │   │   ├─ amountEtf = 50000 × 0.20 = 10000
    │   │   └─ amountStock = 50000 × 0.35 = 17500
    │   │       (adjusted to ensure sum = 50000)
    │   │
    │   └─ Store in Context object
    │
    ├─→ createResponse()
    │   │
    │   └─ Build list of Allocation objects
    │       InvestmentRecommendationsResponse {
    │           allocations: [
    │               Allocation {
    │                   type: "EMERGENCY",
    │                   amount: 12500,
    │                   percent: 0.25
    │               },
    │               Allocation {
    │                   type: "GOLD",
    │                   amount: 10000,
    │                   percent: 0.2
    │               },
    │               Allocation {
    │                   type: "ETF",
    │                   amount: 10000,
    │                   percent: 0.2
    │               },
    │               Allocation {
    │                   type: "STOCK",
    │                   amount: 17500,
    │                   percent: 0.35
    │               }
    │           ]
    │       }
    │
    ↓
Return JSON Response
{
  "success": true,
  "message": "Allocation calculated successfully",
  "data": {
    "allocations": [
      {"type": "EMERGENCY", "amount": 12500, "percent": 0.25},
      {"type": "GOLD", "amount": 10000, "percent": 0.2},
      {"type": "ETF", "amount": 10000, "percent": 0.2},
      {"type": "STOCK", "amount": 17500, "percent": 0.35}
    ]
  }
}
```

---

## 🎯 Strategy Configurations (Sample)

```
SAFE (Chi tiêu an toàn)
├─ EMERGENCY: 40%  → 高liquidity, low return (cash/savings)
├─ GOLD: 30%       → Hedge against inflation
├─ ETF: 10%        → Diversified index funds
└─ STOCK: 20%      → Growth potential

BALANCED (Cân bằng)
├─ EMERGENCY: 25%  → Moderate liquidity
├─ GOLD: 20%       → Some hedging
├─ ETF: 20%        → Core holdings
└─ STOCK: 35%      → Reasonable growth

GROWTH (Tăng trưởng)
├─ EMERGENCY: 10%  → Minimal liquidity
├─ GOLD: 10%       → Small hedge
├─ ETF: 30%        → Growth focus
└─ STOCK: 50%      → Maximum growth
```

---

## 📈 Advantages of New Design

```
OLD (Denormalized)
├─ One row per strategy
├─ Fixed columns: stock_%, emergency_%, gold_%, etf_%
├─ Hard to add new asset type (schema change)
└─ Not scalable

NEW (Normalized)
├─ Strategy table (master)
├─ Details table (child) - one row per asset
├─ Easy to add new asset type (just insert)
├─ Scalable, maintainable, follows 3NF
└─ Better performance with indexing
```

---

**Version:** 1.0 | **Last Updated:** 2024-04-29

