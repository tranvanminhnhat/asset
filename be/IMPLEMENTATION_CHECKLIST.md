# Implementation Checklist: Allocation Strategy Refactoring

## Pre-Implementation

- [x] Understand old schema (investment_allocation_master)
- [x] Understand new schema (mst_allocation_strategies + mst_allocation_strategy_details)
- [x] Review existing entity implementations
- [x] Review existing repository implementations
- [x] Plan refactoring approach

## Database Setup

- [ ] Backup current production data
- [ ] Run table creation SQL
- [ ] Verify tables created in database
- [ ] Insert seed data (SAFE, BALANCED, GROWTH strategies)
- [ ] Verify data inserted correctly
- [ ] Check indexes are created
- [ ] Verify foreign key relationships

```bash
# Verification queries
SELECT * FROM mst_allocation_strategies;
SELECT * FROM mst_allocation_strategy_details;
```

## Code Changes

- [x] Update `InvestmentRecommendationsService.java`:
  - [x] Change imports (remove old, add new repositories/entities)
  - [x] Update constructor injection (remove masterRepository, add strategyRepository & detailRepository)
  - [x] Refactor loadData() method
  - [x] Refactor executeBusinessLogic() method
  - [x] Update Context class
  - [x] Keep validateRequest() unchanged
  - [x] Keep createResponse() unchanged

- [ ] Verify no compilation errors
- [ ] Verify no import issues

## Testing

### Unit Tests
- [ ] Test loadData() with valid strategy code
- [ ] Test loadData() with invalid strategy code
- [ ] Test loadData() with valid strategy but no details
- [ ] Test executeBusinessLogic() with normalized percentages
- [ ] Test executeBusinessLogic() with non-normalized percentages
- [ ] Test createResponse() generates correct allocations
- [ ] Test rounding adjustments work correctly
- [ ] Test edge cases (very small amounts, very large amounts)

### Integration Tests
- [ ] Test full allocate() flow with SAFE strategy
- [ ] Test full allocate() flow with BALANCED strategy
- [ ] Test full allocate() flow with GROWTH strategy
- [ ] Test API endpoint returns correct response
- [ ] Test error responses for invalid data

### Manual Tests
- [ ] Start application
- [ ] Test SAFE strategy (10000)
  - [ ] EMERGENCY: 4000.00 (40%)
  - [ ] GOLD: 3000.00 (30%)
  - [ ] ETF: 1000.00 (10%)
  - [ ] STOCK: 2000.00 (20%)
- [ ] Test BALANCED strategy (50000)
  - [ ] EMERGENCY: 12500.00 (25%)
  - [ ] GOLD: 10000.00 (20%)
  - [ ] ETF: 10000.00 (20%)
  - [ ] STOCK: 17500.00 (35%)
- [ ] Test GROWTH strategy (100000)
  - [ ] EMERGENCY: 10000.00 (10%)
  - [ ] GOLD: 10000.00 (10%)
  - [ ] ETF: 30000.00 (30%)
  - [ ] STOCK: 50000.00 (50%)
- [ ] Test error cases
  - [ ] Invalid strategy code
  - [ ] Null/negative amount
  - [ ] Empty type

## Performance Testing

- [ ] Query logging enabled
- [ ] Verify no N+1 queries
- [ ] Check response time (< 100ms)
- [ ] Load test with 100 concurrent requests
- [ ] Monitor database connection pool
- [ ] Check memory usage

## Documentation

- [x] Create REFACTOR_ALLOCATION_STRATEGY_SUMMARY.md
- [x] Create REFACTOR_DATA_FLOW_DIAGRAM.md
- [x] Create CODE_COMPARISON_BEFORE_AFTER.md
- [x] Create TESTING_GUIDE_ALLOCATION_REFACTOR.md
- [x] Create QUICK_REFERENCE_ALLOCATION.md
- [ ] Update project README if needed
- [ ] Add inline code comments

## Deployment

- [ ] Code review completed
- [ ] All tests passing
- [ ] No warnings in build
- [ ] Documentation review completed
- [ ] Rollback plan documented
- [ ] Stakeholders notified
- [ ] Deploy to staging
- [ ] Smoke tests on staging
- [ ] Deploy to production
- [ ] Monitor production logs
- [ ] Verify no errors in logs
- [ ] Performance metrics normal

## Post-Deployment

- [ ] Monitor application for 24 hours
- [ ] Check database query logs
- [ ] Verify no spike in errors
- [ ] Gather feedback from users
- [ ] Monitor performance metrics
- [ ] Archive old table (optional)
- [ ] Update any dependent documentation

## Rollback Plan (If Needed)

- [ ] Backup current state
- [ ] Revert `InvestmentRecommendationsService.java`
- [ ] Recompile and restart
- [ ] Verify API working
- [ ] Restore from backup if needed

```bash
git checkout HEAD -- \
  src/main/java/com/minhnhat/demo/features/investmentRecommendations/service/InvestmentRecommendationsService.java
./gradlew clean build -x test
```

## Documentation Files Created

1. **REFACTOR_ALLOCATION_STRATEGY_SUMMARY.md**
   - Overview of changes
   - Database schema updates
   - JPA entity changes
   - Service layer refactoring
   - Benefits and migration guide

2. **REFACTOR_DATA_FLOW_DIAGRAM.md**
   - Visual data flow comparison
   - Entity relationship diagrams
   - Performance comparison
   - Code flow comparison
   - Transaction safety notes

3. **CODE_COMPARISON_BEFORE_AFTER.md**
   - Side-by-side code comparison
   - Key differences summary
   - Entity class definitions
   - Query patterns
   - Method-by-method comparison
   - Testing impact

4. **TESTING_GUIDE_ALLOCATION_REFACTOR.md**
   - Database migration SQL
   - Unit test cases
   - Integration tests
   - Manual testing checklist
   - Verification checklist
   - Logs to monitor

5. **QUICK_REFERENCE_ALLOCATION.md**
   - Quick start guide
   - Files modified
   - Quick curl tests
   - Common issues
   - Performance tips
   - Compatibility matrix

## Sign-Off

| Role | Name | Date | Notes |
|------|------|------|-------|
| Developer | - | - | Implemented refactoring |
| Code Reviewer | - | - | Reviewed changes |
| QA Tester | - | - | Verified all tests pass |
| DevOps/DBA | - | - | Database setup verified |
| Project Manager | - | - | Approved for deployment |

## Key Points to Remember

✅ **API Contract Unchanged**
- Same request format
- Same response format
- Same error codes

✅ **Business Logic Preserved**
- Same allocation calculation
- Same rounding behavior
- Same normalization logic

❌ **Internal Changes**
- New repository pattern
- New entity structure
- New data loading method

⚠️ **Critical Dependencies**
- Must create new database tables
- Must insert seed data
- Must compile without errors
- Must pass all tests before deployment

🔄 **Easy Rollback**
- Single file revert if needed
- Old entities still in codebase
- No data loss (both schemas can coexist)

---

## Pre-Deployment Checklist

Before pushing to production:

```bash
# 1. Build and test
cd source/asset/be
./gradlew clean build

# 2. Run unit tests
./gradlew test

# 3. Check for warnings
./gradlew check

# 4. Create tables
# Run SQL migration scripts

# 5. Start app locally
./gradlew bootRun

# 6. Test API
curl -X POST http://localhost:8080/api/investment/recommendations \
  -H "Content-Type: application/json" \
  -d '{"amount": 10000, "type": "SAFE"}'

# 7. Monitor logs
# Should see: "Allocation computed from master type=SAFE..."

# 8. Git commit
git add -A
git commit -m "refactor: update allocation strategy to use normalized master tables"

# 9. Push and deploy
git push
# (CI/CD pipeline handles deployment)
```

---

**Status**: Ready for Implementation ✅  
**Complexity**: Medium  
**Risk Level**: Low (API contract unchanged, internal refactor)  
**Estimated Time**: 2-4 hours (including testing)

