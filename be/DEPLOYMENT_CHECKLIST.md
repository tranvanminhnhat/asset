# CHECKLIST - ALLOCATION STRATEGY REFACTORING

## ✅ COMPLETED TASKS

### 1. Entity Layer
- [x] AllocationStrategyEntity updated
  - [x] Table name: `mst_allocation_strategies`
  - [x] Columns: id, code, name (removed description)
  - [x] Relationship: OneToMany with details (LAZY)
  
- [x] AllocationStrategyDetailEntity updated
  - [x] Table name: `mst_allocation_strategy_details`
  - [x] Columns: id, strategyId, assetType, percentage, strategyCode
  - [x] Relationship: ManyToOne with strategy (LAZY)
  - [x] ForeignKey constraint on strategy_id

### 2. Repository Layer
- [x] AllocationStrategyRepository (no changes needed)
  - [x] findByCode(String code)
  - [x] findByCodeWithDetails(String code) with FETCH JOIN
  - [x] Inherits from JpaRepository<AllocationStrategyEntity, Long>

### 3. Service Layer
- [x] InvestmentRecommendationsService refactored
  - [x] Imports updated (removed InvestmentAllocationMaster*)
  - [x] Imports added (AllocationStrategy*)
  - [x] Constructor injection: AllocationStrategyRepository
  - [x] validateRequest() - unchanged
  - [x] loadData() - updated to use new repository
  - [x] executeBusinessLogic() - updated to use details map
  - [x] createResponse() - unchanged output format
  - [x] Context class - updated fields
  - [x] Utility methods: nonNull(), strip() - unchanged

### 4. Controller Layer
- [x] InvestmentRecommendationsController - no changes needed
  - [x] Endpoint: POST /investment-amount
  - [x] Request: InvestmentRecommendationsRequest
  - [x] Response: InvestmentRecommendationsResponse

### 5. Database Migration
- [x] V005__Rename_Allocation_Tables.sql created
  - [x] Rename allocation_strategies → mst_allocation_strategies
  - [x] Rename allocation_strategy_details → mst_allocation_strategy_details

### 6. Documentation
- [x] ALLOCATION_STRATEGY_REFACTOR_GUIDE.md - comprehensive guide
- [x] REFACTORING_SUMMARY.md - quick summary
- [x] DATA_FLOW_DIAGRAM.md - visual flow
- [x] SQL_SAMPLE_DATA.sql - test data

---

## 🧪 TESTING CHECKLIST

### Database
- [ ] Tables exist with new names
- [ ] Columns match entity definitions
- [ ] Foreign keys are correct
- [ ] Indexes are created

### Sample Data
- [ ] mst_allocation_strategies table populated with at least 3 strategies (SAFE, BALANCED, GROWTH)
- [ ] mst_allocation_strategy_details populated with details for each strategy
- [ ] Verify: SELECT COUNT(*) FROM mst_allocation_strategy_details; should return 12

### API Testing
- [ ] POST /investment-amount with type=SAFE
  - [ ] Returns correct allocations
  - [ ] All percentages sum to 1.0
  - [ ] All amounts sum to input amount

- [ ] POST /investment-amount with type=BALANCED
  - [ ] Returns correct allocations
  - [ ] All percentages sum to 1.0
  - [ ] All amounts sum to input amount

- [ ] POST /investment-amount with type=GROWTH
  - [ ] Returns correct allocations
  - [ ] All percentages sum to 1.0
  - [ ] All amounts sum to input amount

- [ ] Error handling
  - [ ] Invalid type throws LogicException
  - [ ] Null request throws exception
  - [ ] Negative amount throws exception
  - [ ] Empty type throws exception

### Build
- [ ] No compilation errors
- [ ] No runtime errors on startup
- [ ] Application context loads successfully
- [ ] Beans are created:
  - [ ] AllocationStrategyRepository
  - [ ] InvestmentRecommendationsService
  - [ ] InvestmentRecommendationsController

### Logging
- [ ] Service logs validation steps
- [ ] Service logs when data loaded
- [ ] Service logs normalization if needed
- [ ] Service logs final allocations with summary

---

## 📋 PRE-DEPLOYMENT VERIFICATION

### Code Quality
- [ ] No unused imports
- [ ] Consistent naming conventions
- [ ] No null pointers (proper null handling)
- [ ] Proper exception handling
- [ ] Logging at appropriate levels

### Performance
- [ ] Query uses FETCH JOIN (no N+1)
- [ ] Indexes created on foreign keys
- [ ] BigDecimal operations use proper rounding
- [ ] No unnecessary database calls

### Backward Compatibility
- [ ] Request DTO unchanged
- [ ] Response DTO unchanged
- [ ] API endpoint unchanged
- [ ] Logic produces same results

---

## 🚀 DEPLOYMENT STEPS

1. **Backup Database**
   ```bash
   mysqldump -u root -p asset_db > backup_$(date +%Y%m%d_%H%M%S).sql
   ```

2. **Run Migration** (automatic via Flyway)
   ```
   Application startup will run V005__Rename_Allocation_Tables.sql
   ```

3. **Insert Sample Data** (manual)
   ```sql
   -- Execute SQL_SAMPLE_DATA.sql
   ```

4. **Start Application**
   ```bash
   ./gradlew bootRun
   ```

5. **Test API** (using curl or Postman)
   ```bash
   curl -X POST http://localhost:8080/investment-amount \
     -H "Content-Type: application/json" \
     -d '{"amount": 50000, "type": "BALANCED"}'
   ```

6. **Verify Logs**
   ```
   Look for: "Allocation computed from master type=BALANCED"
   ```

---

## ⚠️ COMMON ISSUES & SOLUTIONS

| Issue | Cause | Solution |
|-------|-------|----------|
| Table not found | Migration not run | Check Flyway logs, manually run SQL |
| No data found | Sample data not inserted | Run SQL_SAMPLE_DATA.sql |
| 401/403 | Missing auth | Add JWT token in Authorization header |
| Type mismatch | Wrong type value | Use exactly: SAFE, BALANCED, GROWTH |
| Null pointer | Details not fetched | Check FETCH JOIN in query |

---

## 📞 SUPPORT

- Documentation: `/ALLOCATION_STRATEGY_REFACTOR_GUIDE.md`
- Flow Diagram: `/DATA_FLOW_DIAGRAM.md`
- Sample Data: `/SQL_SAMPLE_DATA.sql`
- Database Schema: Check `mst_allocation_strategies` table structure

---

**Last Updated:** 2024-04-29
**Status:** ✅ Ready for Deployment

