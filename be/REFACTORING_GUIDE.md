# Spring Boot Fintech Investment System - Refactoring Guide

## Overview

This document describes the refactoring of the investment system from a simple portfolio model to a **transaction-based, event-sourced architecture** following fintech best practices.

## Key Changes Summary

### 1. Data Type Consistency (CRITICAL FIX)
- **Before**: `PortfolioEntity.userId` was `Long` while `UserEntity.userId` was `String`
- **After**: Standardized all userId fields to `String` for consistency
- **Impact**: Enables proper foreign key relationships and JPA mappings

### 2. New Database Tables (Transaction-Based)

#### PORTFOLIO_TRANSACTION (Append-Only)
```
- Immutable audit trail of all portfolio transactions
- Fields: id, portfolio_id, user_id, transaction_type, asset_type, quantity, unit_price, total_amount, transaction_date
- Indexes: portfolio_id, user_id, transaction_date
- Purpose: Source of truth for portfolio history, enables PnL recalculation
```

#### ASSET_PRICE (Historical Prices)
```
- Snapshots of asset prices over time
- Fields: id, asset_type, price, price_date, currency, source
- Unique Index: (asset_type, price_date)
- Purpose: Calculate PnL as-of any date, track price history
```

#### ALLOCATION_STRATEGY (Future Enhancement)
```
- Master data for allocation strategies
- Fields: id, code, name, description, status
- Purpose: Define investment strategy rules
```

#### ALLOCATION_STRATEGY_DETAIL (Future Enhancement)
```
- Allocation percentages per asset type
- Fields: id, strategy_id, asset_type, allocation_percent
- Purpose: Define asset allocation breakdowns
```

### 3. JPA Entity Relationships

#### Before (No Relationships)
```java
// Disconnected entities - N+1 query problems
List<Portfolio> portfolios = portfolioRepository.findByUserId(userId); // N queries
for (Portfolio p : portfolios) {
    User u = p.getUser(); // N additional queries!
}
```

#### After (With Proper Relationships)
```java
// Entities properly mapped
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "USER_ID", updatable = false, insertable = false)
private UserEntity user;

@OneToMany(mappedBy = "portfolio", fetch = FetchType.LAZY)
private List<PortfolioTransactionEntity> transactions;

// JOIN FETCH queries - Single DB call
List<Portfolio> portfolios = 
    portfolioRepository.findByUserIdWithDetails(userId); // 1 query with joins
```

### 4. Service Architecture Pattern

**Consistent Pattern Applied Across All Services:**

```
Service Method:
  1. Validate (Input validation)
  2. LoadData (Fetch with JOIN FETCH)
  3. Process (Business logic)
  4. Persist (Save changes)
  5. BuildResponse (DTO conversion)
```

**Example:**
```java
@Transactional(rollbackFor = Exception.class)
public PortfolioTransactionEntity addTransaction(...) {
    // 1. Validate
    validateInputs(portfolioId, quantity, unitPrice);
    
    // 2. LoadData
    PortfolioEntity portfolio = portfolioRepository
        .findByIdWithTransactions(portfolioId)
        .orElseThrow(...);
    
    // 3. Process
    BigDecimal totalAmount = quantity.multiply(unitPrice);
    
    // 4. Persist
    PortfolioTransactionEntity transaction = 
        transactionRepository.save(newTransaction);
    
    // 5. BuildResponse (in Controller)
    return mapToResponse(transaction);
}
```

### 5. N+1 Query Prevention

**Implementation:**
- All multi-entity queries use `@Query` with `JOIN FETCH`
- Named methods: `findBy*WithDetails()` for eager loading
- Read-only queries use `@Transactional(readOnly = true)`

**Example Query:**
```java
@Query("SELECT DISTINCT p FROM PortfolioEntity p " +
       "LEFT JOIN FETCH p.user " +
       "LEFT JOIN FETCH p.transactions " +
       "WHERE p.userId = :userId " +
       "ORDER BY p.id ASC")
List<PortfolioEntity> findByUserIdWithDetails(@Param("userId") String userId);
```

### 6. PnL Calculation (Dynamic, Not Stored)

**Key Principle:** PnL is CALCULATED, not stored in DB

```java
// ✅ CORRECT - Dynamic calculation
BigDecimal pnl = currentValue.subtract(purchaseValue);

// ❌ WRONG - Stored in DB (stale data risk)
@Column(name = "pnl")
private BigDecimal pnl;
```

**Benefits:**
- Always accurate
- No data consistency issues
- Historical PnL via asset prices
- Supports "as-of" calculations

### 7. Transactional Consistency

**Implementation:**
```java
@Transactional(rollbackFor = Exception.class)
public InvestmentPlanEntity createPlan(...) {
    // Validation first
    validateInputs(...);
    
    // Business logic
    InvestmentPlanEntity plan = buildPlan(...);
    
    // Persistence LAST
    return investmentPlanRepository.save(plan);
}
```

**Rules:**
- Transactions are atomic (all-or-nothing)
- Write operations are marked `@Transactional(rollbackFor = Exception.class)`
- Read-only operations use `@Transactional(readOnly = true)` for optimization
- Append-only tables (transactions) written LAST to ensure consistency

### 8. Repository Layer Enhancements

**Before:**
```java
public interface PortfolioRepository extends JpaRepository<Portfolio, String> {
    List<PortfolioEntity> findByUserId(String nnn);  // Generic name, N+1 problem
}
```

**After:**
```java
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    // Multiple named methods for different use cases
    
    @Query("SELECT DISTINCT p FROM PortfolioEntity p " +
           "LEFT JOIN FETCH p.user WHERE p.userId = :userId")
    List<PortfolioEntity> findByUserIdWithDetails(@Param("userId") String userId);
    
    @Query("SELECT p FROM PortfolioEntity p " +
           "LEFT JOIN FETCH p.transactions WHERE p.id = :id")
    Optional<PortfolioEntity> findByIdWithTransactions(@Param("id") Long id);
    
    // Keep legacy method for backward compatibility
    List<PortfolioEntity> findByUserId(String userId);
}
```

## New Services

### PortfolioService
- `getPortfolioWithDetails()` - Get portfolio with relationships
- `getUserPortfolios()` - Get all user portfolios (with JOIN FETCH)
- `addTransaction()` - Record immutable transaction
- `calculatePortfolioPnL()` - Dynamic PnL calculation
- `updatePortfolioCurrentValue()` - Update after price changes

### PnLService
- `calculateSimplePnL()` - Current value minus purchase value
- `calculatePnLPercentage()` - PnL as percentage
- `isInProfit()` - Boolean profit indicator
- `getLatestAssetPrice()` - Fetch current asset price
- `getAssetPriceAsOf()` - Historical price lookup

### InvestmentPlanService
- `createPlan()` - Create investment plan with snapshot
- `getPlanWithAllocations()` - Get plan with all details
- `getUserPlans()` - Get user's plans
- `updatePlanStatus()` - Update plan status
- `addAllocation()` - Add allocation to plan
- `getPlanAllocations()` - Get allocations

### RecommendationService
- `calculateRecommendation()` - Read-only recommendations
- `getRecommendationProfile()` - Get user profile (no DB writes)

## API Backward Compatibility

### Maintained Endpoints
- `GET /api/v1/dashboard` - Still returns same format
- `POST /auth/login` - Authentication unchanged
- `POST /auth/register` - Registration unchanged

### New Endpoints
- `GET /api/v1/portfolios` - Get user portfolios with PnL
- `POST /api/v1/portfolios/{id}/transactions` - Record transaction
- `GET /api/v1/portfolios/{id}/transactions` - Get transaction history
- `GET /api/v1/investment-plans` - List investment plans
- `POST /api/v1/investment-plans` - Create investment plan

## Database Migration

### Migration Files
Located in: `src/main/resources/db/migration/`

**V001__Create_Transaction_Based_Portfolio_Tables.sql:**
- Creates PORTFOLIO_TRANSACTION table (append-only)
- Creates ASSET_PRICE table (historical prices)
- Creates ALLOCATION_STRATEGY tables (future)
- Adds foreign key constraints

### How to Run
1. Ensure Flyway is configured in build.gradle
2. Place SQL files in `src/main/resources/db/migration/`
3. Schema automatically migrates on application startup

## Best Practices Implemented

### ✅ 1. Type Consistency
- All userId fields are `String` (matches authentication)
- All monetary values are `BigDecimal` with scale 2
- All timestamps are `LocalDateTime`

### ✅ 2. Relationship Mapping
- Foreign keys properly mapped with `@ManyToOne` / `@OneToMany`
- `fetch = FetchType.LAZY` by default (prevent loading unnecessary data)
- `insertable = false, updatable = false` for foreign key columns in entities

### ✅ 3. Query Optimization
- All multi-entity queries use `JOIN FETCH`
- Indexes on frequently queried columns
- Read-only queries marked as such for DB optimization

### ✅ 4. Immutability Where Needed
- Transaction table is append-only (no updates/deletes)
- `updatable = false` on transaction columns
- Audit trail preserved forever

### ✅ 5. Transactional Integrity
- All writes are atomic
- Rollback on exception
- Related entities saved in correct order

### ✅ 6. Error Handling
- Consistent exception throwing
- Logged at appropriate levels (debug/info/error)
- Business rules validated before persistence

### ✅ 7. DTO Contract
- Request/Response DTOs separate from entities
- `@JsonInclude(NON_NULL)` to avoid null clutter
- Consistent naming with database fields

## Migration Checklist

- [ ] Backup existing database
- [ ] Review migration SQL before executing
- [ ] Update `application.yaml` if needed (ddl-auto remains "none")
- [ ] Restart application (Flyway auto-migration)
- [ ] Test all portfolio endpoints
- [ ] Verify transaction history is populated
- [ ] Test PnL calculations
- [ ] Load test with transaction data
- [ ] Monitor query performance (check transaction counts)

## Testing Recommendations

### Unit Tests
```java
@Test
public void testAddTransaction_ValidData_Success() {
    // Setup
    Long portfolioId = 1L;
    
    // Execute
    PortfolioTransactionEntity result = 
        portfolioService.addTransaction(...);
    
    // Assert
    assertNotNull(result.getId());
    assertEquals("BUY", result.getTransactionType());
}
```

### Integration Tests
```java
@Test
@Transactional
public void testPortfolioWithTransactions_SingleQuery() {
    // Should execute exactly 1 query via JOIN FETCH
    PortfolioEntity portfolio = 
        portfolioRepository.findByIdWithTransactions(1L).get();
    
    // This should NOT trigger additional queries
    int txnCount = portfolio.getTransactions().size();
}
```

### Performance Tests
- Verify N+1 queries eliminated
- Benchmark PnL calculations
- Load test with 10k+ transactions per portfolio

## Rollback Plan

If issues occur:
1. Keep backup of original database
2. SQL files versioned (V001, V002, etc.)
3. Rollback: Drop new tables, restore from backup
4. Revert entity classes to previous version

## Future Enhancements

1. **Allocation Strategy Engine** - Implement AllocationStrategy tables
2. **Automated Recommendations** - Build ML-based recommendation algorithm
3. **Event Sourcing** - Store all changes as immutable events
4. **Real-time Price Updates** - Integrate price feeds into AssetPrice table
5. **Advanced Reporting** - Historical PnL, tax impact, allocation drift
6. **Risk Analysis** - VaR, Sharpe ratio, portfolio correlation analysis

## Code Examples

### Create Investment Plan
```java
InvestmentPlanEntity plan = investmentPlanService.createPlan(
    userId = "user-123",
    totalAmount = new BigDecimal("10000.00"),
    status = "DRAFT",
    recommendationSnapshot = jsonString,
    note = "Quarterly rebalancing"
);
```

### Record Transaction
```java
PortfolioTransactionEntity txn = portfolioService.addTransaction(
    portfolioId = 1L,
    userId = "user-123",
    transactionType = "BUY",
    assetType = "AAPL",
    quantity = new BigDecimal("10.00"),
    unitPrice = new BigDecimal("150.25"),
    transactionDate = LocalDateTime.now()
);
```

### Get Portfolio Summary
```java
List<PortfolioEntity> portfolios = 
    portfolioService.getUserPortfolios("user-123");

for (PortfolioEntity p : portfolios) {
    BigDecimal pnl = pnlService.calculateSimplePnL(p.getId());
    BigDecimal pnlPercent = pnlService.calculatePnLPercentage(p.getId());
    System.out.println("PnL: " + pnl + ", %" + pnlPercent);
}
```

---

**Last Updated:** 2025-04-19
**Author:** Senior Java Backend Engineer
**Status:** Ready for Integration

