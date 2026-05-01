# Refactoring Summary: Investment Allocation Master to Allocation Strategies

## Overview
Successfully refactored the `InvestmentRecommendationsService` to load allocation data from two new master tables (`mst_allocation_strategies` and `mst_allocation_strategy_details`) instead of the single `investment_allocation_master` table.

## Changes Made

### 1. **Database Schema Updates**

#### Old Schema:
- Single table: `investment_allocation_master`
  - Columns: code (PK), stock_percentage, emergency_percentage, gold_percentage, etf_percentage

#### New Schema:
- `mst_allocation_strategies`: Master table with code and strategy metadata
  - id (PK, BIGINT AUTO_INCREMENT)
  - code (VARCHAR(30), UNIQUE) - SAFE, BALANCED, GROWTH
  - name (VARCHAR(100))
  - created_at (DATETIME)
  - updated_at (DATETIME)

- `mst_allocation_strategy_details`: Detail table with percentage allocations
  - id (PK, BIGINT AUTO_INCREMENT)
  - strategy_id (FK to mst_allocation_strategies.id)
  - asset_type (VARCHAR(50)) - EMERGENCY, GOLD, ETF, STOCK
  - percentage (DECIMAL(5,2)) - Stored as 0-100
  - strategy_code (VARCHAR(45)) - Denormalized code reference
  - created_at (TIMESTAMP)
  - updated_at (TIMESTAMP)

### 2. **JPA Entity Changes**

#### Existing Entities (Already Created):
- `AllocationStrategyEntity` - Maps to `mst_allocation_strategies`
  - Contains `@OneToMany` relationship to details
  - Fetch strategy with `findByCode(String code)` method
  
- `AllocationStrategyDetailEntity` - Maps to `mst_allocation_strategy_details`
  - Contains `@ManyToOne` relationship to strategy
  - Fetch details by strategy_id

#### Old Entity (Now Deprecated):
- `InvestmentAllocationMasterEntity` - Original single-table entity

### 3. **Service Layer Refactoring**

#### File: `InvestmentRecommendationsService.java`

**Changes:**
```
OLD:
- private final InvestmentAllocationMasterRepository masterRepository;
- Context.master: InvestmentAllocationMasterEntity

NEW:
- private final AllocationStrategyRepository strategyRepository;
- private final AllocationStrategyDetailRepository detailRepository;
- Context.strategy: AllocationStrategyEntity
- Context.details: List<AllocationStrategyDetailEntity>
```

**Key Method Updates:**

1. **loadData() Method:**
   - OLD: Query single table by code, fallback to findAll() + case-insensitive loop
   - NEW: 
     - Query `mst_allocation_strategies` by code using `findByCode(type)`
     - If found, load related details using `findByStrategyId(strategy.getId())`
     - Proper error handling for both missing strategy and missing details

2. **executeBusinessLogic() Method:**
   - OLD: Direct field access on master entity (getEmergencyPercent(), etc.)
   - NEW:
     - Iterate through details list
     - Match asset_type (EMERGENCY, GOLD, ETF, STOCK) with percentage
     - Support both percentage formats:
       - 0-100 (automatic conversion to 0-1)
       - 0-1 (direct use)
     - All remaining logic stays the same (normalization, amount calculation, rounding adjustments)

3. **Context Class:**
   - OLD: Stored single `InvestmentAllocationMasterEntity master`
   - NEW: Stores `AllocationStrategyEntity strategy` + `List<AllocationStrategyDetailEntity> details`

### 4. **Repositories (Already Implemented)**

```java
AllocationStrategyRepository:
- findByCode(String code): Optional<AllocationStrategyEntity>
- findByCodeWithDetails(String code): Optional<AllocationStrategyEntity> (with lazy-loaded details)
- findByIdWithDetails(Long id): Optional<AllocationStrategyEntity>

AllocationStrategyDetailRepository:
- findByStrategyId(Long strategyId): List<AllocationStrategyDetailEntity>
```

## Benefits of New Structure

1. **Flexibility**: Support multiple strategies with different allocations
2. **Normalization**: Master data properly normalized (1NF compliant)
3. **Scalability**: Easy to add new asset types without modifying master table
4. **Maintainability**: Clear separation of concerns (strategy definition vs. allocation details)
5. **Auditability**: Each detail can track creation/update timestamps independently

## API Contract (Unchanged)

Request remains identical:
```json
{
  "amount": 10000,
  "type": "SAFE"  // or "BALANCED", "GROWTH"
}
```

Response remains identical:
```json
{
  "allocations": [
    {
      "type": "EMERGENCY",
      "amount": 4000.00,
      "percent": 40
    },
    {
      "type": "GOLD",
      "amount": 3000.00,
      "percent": 30
    },
    {
      "type": "ETF",
      "amount": 1000.00,
      "percent": 10
    },
    {
      "type": "STOCK",
      "amount": 2000.00,
      "percent": 20
    }
  ]
}
```

## Data Migration

To migrate existing data from `investment_allocation_master` to new tables:

```sql
-- Create strategies
INSERT INTO mst_allocation_strategies (code, name, created_at)
SELECT code, code AS name, CURRENT_TIMESTAMP FROM investment_allocation_master
GROUP BY code;

-- Migrate EMERGENCY allocations
INSERT INTO mst_allocation_strategy_details (strategy_id, asset_type, percentage, strategy_code)
SELECT s.id, 'EMERGENCY', m.emergency_percentage, m.code
FROM mst_allocation_strategies s
JOIN investment_allocation_master m ON s.code = m.code;

-- Migrate GOLD allocations
INSERT INTO mst_allocation_strategy_details (strategy_id, asset_type, percentage, strategy_code)
SELECT s.id, 'GOLD', m.gold_percentage, m.code
FROM mst_allocation_strategies s
JOIN investment_allocation_master m ON s.code = m.code;

-- Migrate ETF allocations
INSERT INTO mst_allocation_strategy_details (strategy_id, asset_type, percentage, strategy_code)
SELECT s.id, 'ETF', m.etf_percentage, m.code
FROM mst_allocation_strategies s
JOIN investment_allocation_master m ON s.code = m.code;

-- Migrate STOCK allocations
INSERT INTO mst_allocation_strategy_details (strategy_id, asset_type, percentage, strategy_code)
SELECT s.id, 'STOCK', m.stock_percentage, m.code
FROM mst_allocation_strategies s
JOIN investment_allocation_master m ON s.code = m.code;
```

## Deprecation Notes

The following are now deprecated but can be retained for backward compatibility:
- `InvestmentAllocationMasterEntity`
- `InvestmentAllocationMasterRepository`

## Testing Recommendations

1. **Unit Tests**: Verify allocation calculation with new strategy loading
2. **Integration Tests**: Test with both 0-100 and 0-1 percentage formats
3. **End-to-End Tests**: Verify API response matches expected allocation breakdown
4. **Edge Cases**:
   - Missing strategy code
   - Missing allocation details
   - Incomplete asset type allocations
   - Percentage sum != 100

## Rollback Plan

If issues arise:
1. Revert changes to `InvestmentRecommendationsService.java`
2. Revert to using `InvestmentAllocationMasterRepository`
3. Keep new tables for reference/audit trail
4. New strategy feature remains available if only service layer is reverted

## Performance Notes

- **Single Query Path**: New design still fetches strategy and details, minimal N+1 issues
- **Recommendation**: Use `findByCodeWithDetails()` if details are always needed (better than separate queries)
- **Caching**: Consider caching strategy details as they change infrequently

## Files Modified

1. `/features/investmentRecommendations/service/InvestmentRecommendationsService.java` - Main refactoring
2. `/features/master/entity/AllocationStrategyEntity.java` - Pre-existing, no changes needed
3. `/features/master/entity/AllocationStrategyDetailEntity.java` - Pre-existing, no changes needed
4. `/features/master/repository/AllocationStrategyRepository.java` - Pre-existing, no changes needed
5. `/features/master/repository/AllocationStrategyDetailRepository.java` - Pre-existing, no changes needed

## Files NOT Modified (For Backward Compatibility)

- `/features/master/entity/InvestmentAllocationMasterEntity.java` - Kept for reference
- `/features/master/repository/InvestmentAllocationMasterRepository.java` - Kept for reference

---

**Refactoring Complete**: The service now uses normalized master data tables while maintaining identical API contracts.

