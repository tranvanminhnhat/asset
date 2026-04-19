# Quick Start Guide - Fintech Investment System

## For Developers

### 1. Build and Run

```bash
# Clean build
./gradlew clean build

# Run application
./gradlew bootRun

# Tests
./gradlew test
```

### 2. Database Migration

The application uses Flyway for automatic schema migration.

**What happens on startup:**
1. Flyway checks `src/main/resources/db/migration/` for SQL scripts
2. Finds `V001__Create_Transaction_Based_Portfolio_Tables.sql`
3. Automatically executes against MySQL database
4. Creates new tables: PORTFOLIO_TRANSACTION, ASSET_PRICE, etc.

**Manual migration (if needed):**
```bash
# Using MySQL CLI
mysql -u root -p asset_db < src/main/resources/db/migration/V001__Create_Transaction_Based_Portfolio_Tables.sql
```

### 3. Testing New APIs

#### Get User Portfolios
```bash
curl -H "Authorization: Bearer <JWT_TOKEN>" \
  http://localhost:8080/api/v1/portfolios
```

#### Record Transaction
```bash
curl -X POST \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "portfolioId": 1,
    "transactionType": "BUY",
    "assetType": "AAPL",
    "quantity": 10.00,
    "unitPrice": 150.25,
    "transactionDate": "2025-04-19T10:30:00"
  }' \
  http://localhost:8080/api/v1/portfolios/1/transactions
```

#### Get Transaction History
```bash
curl -H "Authorization: Bearer <JWT_TOKEN>" \
  http://localhost:8080/api/v1/portfolios/1/transactions
```

### 4. Key Files to Review

#### Architecture
- `REFACTORING_GUIDE.md` - Complete refactoring explanation
- `IMPLEMENTATION_SUMMARY.md` - What was built and where

#### Entities
- `features/portfolio/entity/PortfolioEntity.java` - Portfolio with relationships
- `features/portfolio/entity/PortfolioTransactionEntity.java` - Transaction log
- `features/portfolio/entity/AssetPriceEntity.java` - Price history
- `features/investmentComplete/entity/InvestmentPlanEntity.java` - Investment plan

#### Services
- `features/portfolio/service/PortfolioService.java` - Portfolio logic
- `features/portfolio/service/PnLService.java` - P&L calculations
- `features/investmentComplete/service/InvestmentPlanService.java` - Plan management

#### Repositories
- `features/portfolio/repository/PortfolioRepository.java` - Portfolio queries
- `features/portfolio/repository/PortfolioTransactionRepository.java` - Transaction queries
- `features/portfolio/repository/AssetPriceRepository.java` - Price queries

### 5. Service Patterns

All services follow this pattern:

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class ExampleService {

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity doSomething(RequestDTO request) {
        // 1. Validate inputs
        validateInputs(request);
        
        // 2. Load data (with JOIN FETCH to prevent N+1)
        List<Entity> entities = repository.findWithDetails(id);
        
        // 3. Process business logic
        processLogic(entities);
        
        // 4. Persist changes
        Entity saved = repository.save(entity);
        
        // 5. Build response (usually in controller)
        return mapToResponse(saved);
    }
}
```

### 6. Understanding N+1 Prevention

#### ❌ WRONG - Causes N+1 queries
```java
List<Portfolio> portfolios = portfolioRepository.findByUserId(userId);
for (Portfolio p : portfolios) {
    User user = p.getUser(); // Triggers separate query for each portfolio!
}
```

#### ✅ CORRECT - Single query with JOIN FETCH
```java
@Query("SELECT DISTINCT p FROM PortfolioEntity p " +
       "LEFT JOIN FETCH p.user " +
       "WHERE p.userId = :userId")
List<PortfolioEntity> findByUserIdWithDetails(@Param("userId") String userId);

// Now this triggers only 1 query
List<Portfolio> portfolios = portfolioRepository.findByUserIdWithDetails(userId);
for (Portfolio p : portfolios) {
    User user = p.getUser(); // No additional query!
}
```

### 7. Understanding PnL Calculations

**Key Rule:** PnL is CALCULATED, not stored.

```java
// ✅ CORRECT - Always accurate
BigDecimal pnl = currentValue.subtract(purchaseValue);

// ❌ WRONG - Stored value becomes stale
@Column(name = "pnl")
private BigDecimal pnl; // Stale data risk
```

**Calculate PnL:**
```java
BigDecimal pnl = pnlService.calculateSimplePnL(portfolioId);
BigDecimal pnlPercent = pnlService.calculatePnLPercentage(portfolioId);
boolean inProfit = pnlService.isInProfit(portfolioId);
```

### 8. Adding New Features

#### Example: Add new transaction type

```java
// 1. Update enum/constant
public class TransactionType {
    public static final String BUY = "BUY";
    public static final String SELL = "SELL";
    public static final String DIVIDEND = "DIVIDEND";
    public static final String NEW_TYPE = "NEW_TYPE"; // Add here
}

// 2. Update service validation
private void validateTransactionType(String type) {
    if (!Arrays.asList(BUY, SELL, DIVIDEND, NEW_TYPE).contains(type)) {
        throw new IllegalArgumentException("Invalid transaction type");
    }
}

// 3. Add handler in service
if (type.equals(NEW_TYPE)) {
    handleNewType(transaction);
}

// 4. Update controller endpoint
// Already accepts any transaction type
```

#### Example: Add new asset type

```java
// 1. Insert into ASSET_PRICE table
INSERT INTO ASSET_PRICE (asset_type, price, price_date, currency)
VALUES ('BTC', 45000.00, NOW(), 'USD');

// 2. Use in transaction
portfolioService.addTransaction(
    portfolioId,
    userId,
    "BUY",
    "BTC",
    0.5,
    new BigDecimal("45000.00"),
    LocalDateTime.now()
);

// 3. Query price when needed
Optional<BigDecimal> price = pnlService.getLatestAssetPrice("BTC");
```

### 9. Debugging Tips

#### Check if N+1 problem fixed
Enable SQL logging in `application.yaml`:
```yaml
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
```

Then watch the logs. Should see ONE query with multiple LEFT JOINs instead of multiple queries.

#### Check transaction consistency
Add logs in service:
```java
@Transactional(rollbackFor = Exception.class)
public void doSomething() {
    try {
        validateInputs();
        loadData();
        processLogic();
        persist(); // All of above atomic
        log.info("Operation completed successfully");
    } catch (Exception e) {
        log.error("Operation rolled back due to: {}", e.getMessage());
        throw e;
    }
}
```

#### Verify foreign keys
```sql
-- Check if constraints exist
SELECT CONSTRAINT_NAME 
FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS 
WHERE TABLE_NAME = 'PORTFOLIO';

-- Verify data integrity
SELECT * FROM PORTFOLIO p 
LEFT JOIN USER u ON p.USER_ID = u.USER_ID
WHERE u.USER_ID IS NULL; -- Should be empty
```

### 10. Performance Tuning

#### Query optimization
```java
// Bad: Loads unnecessary data
@Query("SELECT p FROM PortfolioEntity p")
List<PortfolioEntity> getAll();

// Good: Fetch only needed data
@Query("SELECT p FROM PortfolioEntity p WHERE p.userId = :userId")
List<PortfolioEntity> getByUserId(@Param("userId") String userId);

// Better: Use projections for summary data
@Query("SELECT new PortfolioSummaryDTO(p.id, p.name, p.currentValue) " +
       "FROM PortfolioEntity p WHERE p.userId = :userId")
List<PortfolioSummaryDTO> getPortfolioSummaries(@Param("userId") String userId);
```

#### Index strategy
```sql
-- Already created indexes
CREATE INDEX idx_portfolio_id ON PORTFOLIO_TRANSACTION(PORTFOLIO_ID);
CREATE INDEX idx_user_id ON PORTFOLIO_TRANSACTION(USER_ID);
CREATE INDEX idx_transaction_date ON PORTFOLIO_TRANSACTION(TRANSACTION_DATE);
UNIQUE INDEX idx_asset_type_date ON ASSET_PRICE(ASSET_TYPE, PRICE_DATE);
```

### 11. Common Tasks

#### Create new portfolio for user
```java
// In controller
PortfolioEntity portfolio = PortfolioEntity.builder()
    .userId(currentUser.getUserId())
    .name("My First Portfolio")
    .type("EQUITY")
    .purchaseValue(BigDecimal.ZERO)
    .currentValue(BigDecimal.ZERO)
    .build();

PortfolioEntity saved = portfolioRepository.save(portfolio);
```

#### Record multiple transactions in batch
```java
// Service method
@Transactional(rollbackFor = Exception.class)
public List<PortfolioTransactionEntity> addBatchTransactions(
        Long portfolioId,
        List<AddTransactionRequest> requests) {
    
    return requests.stream()
        .map(req -> portfolioService.addTransaction(
            portfolioId,
            userId,
            req.getTransactionType(),
            req.getAssetType(),
            req.getQuantity(),
            req.getUnitPrice(),
            req.getTransactionDate()
        ))
        .toList();
}
```

#### Calculate portfolio performance
```java
// In controller or service
List<PortfolioEntity> portfolios = portfolioService.getUserPortfolios(userId);
List<PortfolioSummaryResponse> summaries = portfolios.stream()
    .map(p -> PortfolioSummaryResponse.builder()
        .portfolioId(p.getId())
        .name(p.getName())
        .currentValue(p.getCurrentValue())
        .pnl(pnlService.calculateSimplePnL(p.getId()))
        .pnlPercentage(pnlService.calculatePnLPercentage(p.getId()))
        .build())
    .toList();
```

### 12. Troubleshooting

| Problem | Cause | Solution |
|---------|-------|----------|
| N+1 queries still happening | Method not using JOIN FETCH | Use repository methods ending in `WithDetails` |
| PnL calculations wrong | Using old data | PnL is dynamic - ensure currentValue is updated |
| Foreign key constraint error | Missing user | Verify user exists before creating portfolio |
| Transaction not recorded | No @Transactional annotation | Add @Transactional to service method |
| Portfolio not found | Wrong ID or no JOIN FETCH | Use `findByIdWithTransactions()` instead of `findById()` |

### 13. Environment Variables

Ensure `application.yaml` is configured:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/asset_db
    username: root
    password: 123456
  jpa:
    hibernate:
      ddl-auto: none  # Migration handled by Flyway
    show-sql: true
```

### 14. Next Steps

1. **Build Project:** `./gradlew clean build`
2. **Review Documentation:** Read REFACTORING_GUIDE.md
3. **Test Endpoints:** Use provided curl examples
4. **Check Logs:** Look for N+1 query elimination
5. **Load Test:** Test with 10,000+ transactions
6. **Integrate with Frontend:** Wire up portfolio and plan endpoints
7. **Performance Monitoring:** Track query times and DB connections

---

**Questions?** Review REFACTORING_GUIDE.md for detailed explanations.
**Need Help?** Check IMPLEMENTATION_SUMMARY.md for file locations.

