# Refactored Data Flow Diagram

## Old Architecture (Single Master Table)

```
InvestmentRecommendationsService
    ↓
InvestmentAllocationMasterRepository.findFirstByCode("SAFE")
    ↓
investment_allocation_master (single row)
    - code: "SAFE"
    - stock_percentage: 20
    - emergency_percentage: 40
    - gold_percentage: 30
    - etf_percentage: 10
    ↓
Context.master: InvestmentAllocationMasterEntity
    ↓
executeBusinessLogic() → Calculate allocations from fields
    ↓
createResponse() → Return allocation breakdown
```

## New Architecture (Normalized Strategy Tables)

```
InvestmentRecommendationsService
    ↓
Step 1: Load Strategy
AllocationStrategyRepository.findByCode("SAFE")
    ↓
    mst_allocation_strategies table
    ┌─────────────────────────────────────┐
    │ id│  code  │       name              │
    ├─────────────────────────────────────┤
    │ 1 │ "SAFE" │ "Safe Investment Plan"  │
    │ 2 │ "BAL"  │ "Balanced Growth"       │
    │ 3 │ "GRO"  │ "Growth Strategy"       │
    └─────────────────────────────────────┘
    ↓ Selects row with id=1
    ↓
Context.strategy: AllocationStrategyEntity(id=1, code="SAFE")

Step 2: Load Strategy Details
AllocationStrategyDetailRepository.findByStrategyId(1)
    ↓
    mst_allocation_strategy_details table
    ┌────────────────────────────────────────────────┐
    │ id│ strategy_id│ asset_type │ percentage │      │
    ├────────────────────────────────────────────────┤
    │ 1 │     1      │ "EMERGENCY"│   40.00   │ ← SELECT
    │ 2 │     1      │ "GOLD"     │   30.00   │ ← SELECT
    │ 3 │     1      │ "ETF"      │   10.00   │ ← SELECT
    │ 4 │     1      │ "STOCK"    │   20.00   │ ← SELECT
    │ 5 │     2      │ "EMERGENCY"│   25.00   │
    │ 6 │     2      │ "GOLD"     │   20.00   │
    │ ... remaining rows ...                       │
    └────────────────────────────────────────────────┘
    ↓ Selects all rows with strategy_id=1
    ↓
Context.details: List<AllocationStrategyDetailEntity>
    [
      {id:1, strategy_id:1, asset_type:"EMERGENCY", percentage:40.00},
      {id:2, strategy_id:1, asset_type:"GOLD", percentage:30.00},
      {id:3, strategy_id:1, asset_type:"ETF", percentage:10.00},
      {id:4, strategy_id:1, asset_type:"STOCK", percentage:20.00}
    ]

Step 3: Extract & Process
executeBusinessLogic()
    ↓
    Loop through details:
    - EMERGENCY: 40.00 → normalize to 0.40 → calculate amount
    - GOLD: 30.00 → normalize to 0.30 → calculate amount
    - ETF: 10.00 → normalize to 0.10 → calculate amount
    - STOCK: 20.00 → normalize to 0.20 → calculate amount
    ↓
Context.percentEmergency = 0.40, amountEmergency = 4000.00
Context.percentGold = 0.30, amountGold = 3000.00
Context.percentEtf = 0.10, amountEtf = 1000.00
Context.percentStock = 0.20, amountStock = 2000.00

Step 4: Build Response
createResponse()
    ↓
InvestmentRecommendationsResponse
    [
      {type:"EMERGENCY", amount:4000.00, percent:40},
      {type:"GOLD", amount:3000.00, percent:30},
      {type:"ETF", amount:1000.00, percent:10},
      {type:"STOCK", amount:2000.00, percent:20}
    ]
```

## Comparison: API Request/Response (Identical)

### Request
```json
{
  "amount": 10000,
  "type": "SAFE"
}
```

### Response (Same in Both Architectures)
```json
{
  "allocations": [
    {
      "type": "EMERGENCY",
      "amount": 4000.00,
      "percent": 40.00
    },
    {
      "type": "GOLD",
      "amount": 3000.00,
      "percent": 30.00
    },
    {
      "type": "ETF",
      "amount": 1000.00,
      "percent": 10.00
    },
    {
      "type": "STOCK",
      "amount": 2000.00,
      "percent": 20.00
    }
  ]
}
```

## Entity Relationship Diagram

### Old Schema (Flat)
```
investment_allocation_master
├── PK: code (VARCHAR 30)
├── stock_percentage (DECIMAL 5,2)
├── emergency_percentage (DECIMAL 5,2)
├── gold_percentage (DECIMAL 5,2)
└── etf_percentage (DECIMAL 5,2)
```

### New Schema (Normalized)
```
mst_allocation_strategies (1)
├── id (BIGINT PK)
├── code (VARCHAR 30 UNIQUE)
├── name (VARCHAR 100)
├── created_at
└── updated_at
        ↓ 1-to-Many
        
mst_allocation_strategy_details (Many)
├── id (BIGINT PK)
├── strategy_id (BIGINT FK)  ← references mst_allocation_strategies.id
├── asset_type (VARCHAR 50)
├── percentage (DECIMAL 5,2)
├── strategy_code (VARCHAR 45)
├── created_at
└── updated_at
```

## Benefits of Normalization

| Aspect | Old | New |
|--------|-----|-----|
| **Flexibility** | Fixed 4 asset types | Add new asset types dynamically |
| **Data Consistency** | Single write path | Atomic detail writes |
| **Querying** | Single table scan | Indexed strategy lookup + detail fetch |
| **Schema Evolution** | Modify master table | Add new rows to details |
| **Audit Trail** | Single timestamp | Per-detail timestamps |
| **Referential Integrity** | Limited | FK constraints enforced |
| **Scalability** | Single record per strategy | Multiple detail rows per strategy |

## Loading Performance

```
OLD: 1 Query
├── SELECT * FROM investment_allocation_master WHERE code = ?
└── Time: ~5ms

NEW: 2 Queries (or 1 with JOIN FETCH)
├── SELECT * FROM mst_allocation_strategies WHERE code = ?        (~5ms)
├── SELECT * FROM mst_allocation_strategy_details WHERE strategy_id = ? (~5ms)
└── Total: ~10ms (or ~6ms with eager loading)

OPTIMIZATION: Use findByCodeWithDetails()
├── SELECT s FROM AllocationStrategyEntity s
│  LEFT JOIN FETCH s.details
│  WHERE s.code = ?
└── Single query with results: ~6ms
```

## Code Flow Comparison

### Old Implementation
```java
// Load single master record
Optional<InvestmentAllocationMasterEntity> opt = 
    masterRepository.findFirstByCode(type);
InvestmentAllocationMasterEntity master = opt.get();

// Direct field access
BigDecimal pEmergency = master.getEmergencyPercent();
BigDecimal pGold = master.getGoldPercent();
BigDecimal pEtf = master.getEtfPercent();
BigDecimal pStock = master.getStockPercent();
```

### New Implementation
```java
// Load strategy and details
AllocationStrategyEntity strategy = 
    strategyRepository.findByCode(type).get();
List<AllocationStrategyDetailEntity> details = 
    detailRepository.findByStrategyId(strategy.getId());

// Dynamic field extraction via loop
for (AllocationStrategyDetailEntity detail : details) {
    if ("EMERGENCY".equalsIgnoreCase(detail.getAssetType())) {
        pEmergency = detail.getPercentage();
    }
    // ... similar for other types
}
```

## Transaction Safety

Both implementations follow the same pattern:
1. Load data (read-only)
2. Validate
3. Process
4. Create response

No write operations, so transaction isolation not affected.

## Backward Compatibility

✅ API Contract: Unchanged
✅ Request Format: Identical
✅ Response Format: Identical
✅ Error Handling: Same exceptions
✅ Business Logic: Same calculation algorithm

⚠️ Internal: Changed repositories and entities used

