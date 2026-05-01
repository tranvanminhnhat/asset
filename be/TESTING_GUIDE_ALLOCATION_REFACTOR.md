# Testing Guide: Investment Recommendations Service Refactoring

## Pre-Refactoring Setup

### 1. Database Migration Script

Run this SQL to create the new tables and migrate existing data:

```sql
-- Drop old table if exists
-- DROP TABLE investment_allocation_master;

-- Create new strategy master table
CREATE TABLE IF NOT EXISTS `asset_db`.`mst_allocation_strategies` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(30) NOT NULL,
  `name` VARCHAR(100) NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `code` (`code` ASC)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Create new strategy details table
CREATE TABLE IF NOT EXISTS `asset_db`.`mst_allocation_strategy_details` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `strategy_id` BIGINT NOT NULL,
  `asset_type` VARCHAR(50) NOT NULL,
  `percentage` DECIMAL(5,2) NOT NULL,
  `strategy_code` VARCHAR(45) NOT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `unique_strategy_asset` (`strategy_id` ASC, `asset_type` ASC),
  INDEX `fk_strategy` (`strategy_id` ASC),
  CONSTRAINT `fk_strategy`
    FOREIGN KEY (`strategy_id`)
    REFERENCES `asset_db`.`mst_allocation_strategies` (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Insert strategies
INSERT INTO `asset_db`.`mst_allocation_strategies` (`code`, `name`)
VALUES 
  ('SAFE', 'Safe Investment Plan'),
  ('BALANCED', 'Balanced Growth Strategy'),
  ('GROWTH', 'Aggressive Growth Strategy');

-- Insert SAFE strategy allocations (40 + 30 + 10 + 20 = 100)
INSERT INTO `asset_db`.`mst_allocation_strategy_details` 
  (`strategy_id`, `asset_type`, `percentage`, `strategy_code`)
VALUES 
  (1, 'EMERGENCY', 40.00, 'SAFE'),
  (1, 'GOLD', 30.00, 'SAFE'),
  (1, 'ETF', 10.00, 'SAFE'),
  (1, 'STOCK', 20.00, 'SAFE');

-- Insert BALANCED strategy allocations (25 + 20 + 20 + 35 = 100)
INSERT INTO `asset_db`.`mst_allocation_strategy_details` 
  (`strategy_id`, `asset_type`, `percentage`, `strategy_code`)
VALUES 
  (2, 'EMERGENCY', 25.00, 'BALANCED'),
  (2, 'GOLD', 20.00, 'BALANCED'),
  (2, 'ETF', 20.00, 'BALANCED'),
  (2, 'STOCK', 35.00, 'BALANCED');

-- Insert GROWTH strategy allocations (10 + 10 + 30 + 50 = 100)
INSERT INTO `asset_db`.`mst_allocation_strategy_details` 
  (`strategy_id`, `asset_type`, `percentage`, `strategy_code`)
VALUES 
  (3, 'EMERGENCY', 10.00, 'GROWTH'),
  (3, 'GOLD', 10.00, 'GROWTH'),
  (3, 'ETF', 30.00, 'GROWTH'),
  (3, 'STOCK', 50.00, 'GROWTH');

COMMIT;
```

## Unit Test Cases

### Test 1: Load Strategy by Code

**Method**: `loadData(InvestmentRecommendationsRequest request)`

```java
@Test
public void testLoadDataWithValidCode() {
    // Arrange
    InvestmentRecommendationsRequest request = 
        InvestmentRecommendationsRequest.builder()
            .amount(new BigDecimal("10000"))
            .type("SAFE")
            .build();
    
    // Mock data
    AllocationStrategyEntity mockStrategy = AllocationStrategyEntity.builder()
        .id(1L)
        .code("SAFE")
        .name("Safe Investment Plan")
        .build();
    
    when(strategyRepository.findByCode("SAFE"))
        .thenReturn(Optional.of(mockStrategy));
    
    List<AllocationStrategyDetailEntity> mockDetails = Arrays.asList(
        AllocationStrategyDetailEntity.builder()
            .id(1L)
            .strategyId(1L)
            .assetType("EMERGENCY")
            .percentage(new BigDecimal("40.00"))
            .build(),
        AllocationStrategyDetailEntity.builder()
            .id(2L)
            .strategyId(1L)
            .assetType("GOLD")
            .percentage(new BigDecimal("30.00"))
            .build(),
        AllocationStrategyDetailEntity.builder()
            .id(3L)
            .strategyId(1L)
            .assetType("ETF")
            .percentage(new BigDecimal("10.00"))
            .build(),
        AllocationStrategyDetailEntity.builder()
            .id(4L)
            .strategyId(1L)
            .assetType("STOCK")
            .percentage(new BigDecimal("20.00"))
            .build()
    );
    
    when(detailRepository.findByStrategyId(1L))
        .thenReturn(mockDetails);
    
    // Act - Via reflection since it's private
    InvestmentRecommendationsResponse response = service.allocate(request);
    
    // Assert
    assertNotNull(response);
    assertEquals(4, response.getAllocations().size());
}

@Test
public void testLoadDataWithInvalidCode() {
    // Arrange
    InvestmentRecommendationsRequest request = 
        InvestmentRecommendationsRequest.builder()
            .amount(new BigDecimal("10000"))
            .type("INVALID")
            .build();
    
    when(strategyRepository.findByCode("INVALID"))
        .thenReturn(Optional.empty());
    
    // Act & Assert
    assertThrows(LogicException.class, () -> {
        service.allocate(request);
    });
}
```

### Test 2: Calculate Allocations Correctly

**Method**: `executeBusinessLogic(Context ctx)`

```java
@Test
public void testAllocationCalculation() {
    // Arrange
    InvestmentRecommendationsRequest request = 
        InvestmentRecommendationsRequest.builder()
            .amount(new BigDecimal("10000"))
            .type("SAFE")
            .build();
    
    AllocationStrategyEntity mockStrategy = AllocationStrategyEntity.builder()
        .id(1L)
        .code("SAFE")
        .build();
    
    List<AllocationStrategyDetailEntity> mockDetails = Arrays.asList(
        AllocationStrategyDetailEntity.builder()
            .assetType("EMERGENCY").percentage(new BigDecimal("40.00")).build(),
        AllocationStrategyDetailEntity.builder()
            .assetType("GOLD").percentage(new BigDecimal("30.00")).build(),
        AllocationStrategyDetailEntity.builder()
            .assetType("ETF").percentage(new BigDecimal("10.00")).build(),
        AllocationStrategyDetailEntity.builder()
            .assetType("STOCK").percentage(new BigDecimal("20.00")).build()
    );
    
    when(strategyRepository.findByCode("SAFE"))
        .thenReturn(Optional.of(mockStrategy));
    when(detailRepository.findByStrategyId(1L))
        .thenReturn(mockDetails);
    
    // Act
    InvestmentRecommendationsResponse response = service.allocate(request);
    
    // Assert
    assertEquals(4, response.getAllocations().size());
    
    // Find each allocation and verify
    Optional<InvestmentRecommendationsResponse.Allocation> emergency = 
        response.getAllocations().stream()
            .filter(a -> "EMERGENCY".equals(a.getType()))
            .findFirst();
    
    assertTrue(emergency.isPresent());
    assertEquals(new BigDecimal("4000"), emergency.get().getAmount());
    assertEquals(new BigDecimal("40"), emergency.get().getPercent());
    
    Optional<InvestmentRecommendationsResponse.Allocation> gold = 
        response.getAllocations().stream()
            .filter(a -> "GOLD".equals(a.getType()))
            .findFirst();
    
    assertTrue(gold.isPresent());
    assertEquals(new BigDecimal("3000"), gold.get().getAmount());
    assertEquals(new BigDecimal("30"), gold.get().getPercent());
    
    // Verify total = 10000
    BigDecimal total = response.getAllocations().stream()
        .map(InvestmentRecommendationsResponse.Allocation::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    assertEquals(new BigDecimal("10000"), total);
}
```

### Test 3: Percentage Normalization

```java
@Test
public void testPercentageNormalization() {
    // Test when percentages don't sum to 100 (or 1)
    // For example: SAFE with percentages that sum to 95
    // Expected: System should normalize them proportionally
    
    InvestmentRecommendationsRequest request = 
        InvestmentRecommendationsRequest.builder()
            .amount(new BigDecimal("10000"))
            .type("CUSTOM")
            .build();
    
    List<AllocationStrategyDetailEntity> customDetails = Arrays.asList(
        AllocationStrategyDetailEntity.builder()
            .assetType("EMERGENCY").percentage(new BigDecimal("38.00")).build(),
        AllocationStrategyDetailEntity.builder()
            .assetType("GOLD").percentage(new BigDecimal("28.50")).build(),
        AllocationStrategyDetailEntity.builder()
            .assetType("ETF").percentage(new BigDecimal("9.50")).build(),
        AllocationStrategyDetailEntity.builder()
            .assetType("STOCK").percentage(new BigDecimal("19.00")).build()
    );
    // Sum = 95.00, should normalize to 100.00
    
    AllocationStrategyEntity mockStrategy = AllocationStrategyEntity.builder()
        .id(99L)
        .code("CUSTOM")
        .build();
    
    when(strategyRepository.findByCode("CUSTOM"))
        .thenReturn(Optional.of(mockStrategy));
    when(detailRepository.findByStrategyId(99L))
        .thenReturn(customDetails);
    
    // Act
    InvestmentRecommendationsResponse response = service.allocate(request);
    
    // Assert: Total should still equal 10000
    BigDecimal total = response.getAllocations().stream()
        .map(InvestmentRecommendationsResponse.Allocation::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    
    assertEquals(0, new BigDecimal("10000").compareTo(total));
}
```

## Integration Tests

### Test 4: End-to-End API Call

```java
@Test
@SpringBootTest
public void testInvestmentRecommendationsAPI() {
    // Act
    mockMvc.perform(
        post("/api/investment/recommendations")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "amount": 50000,
                    "type": "BALANCED"
                }
                """)
    )
    // Assert
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.data.allocations", hasSize(4)))
    .andExpect(jsonPath("$.data.allocations[0].type", is("EMERGENCY")))
    .andExpect(jsonPath("$.data.allocations[1].type", is("GOLD")))
    .andExpect(jsonPath("$.data.allocations[2].type", is("ETF")))
    .andExpect(jsonPath("$.data.allocations[3].type", is("STOCK")))
    .andExpect(jsonPath("$.message.code", is("SUCCESS")));
}
```

## Manual Testing Checklist

### 1. Happy Path - SAFE Strategy

```bash
curl -X POST http://localhost:8080/api/investment/recommendations \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 10000,
    "type": "SAFE"
  }'
```

**Expected Response:**
```json
{
  "data": {
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
  },
  "message": {
    "code": "SUCCESS",
    "service": "InvestmentRecommendationsService",
    "message": "Allocation calculated successfully"
  }
}
```

### 2. Happy Path - BALANCED Strategy

```bash
curl -X POST http://localhost:8080/api/investment/recommendations \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50000,
    "type": "BALANCED"
  }'
```

**Verify:**
- EMERGENCY: 12500.00 (25%)
- GOLD: 10000.00 (20%)
- ETF: 10000.00 (20%)
- STOCK: 17500.00 (35%)
- Total: 50000.00

### 3. Happy Path - GROWTH Strategy

```bash
curl -X POST http://localhost:8080/api/investment/recommendations \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100000,
    "type": "GROWTH"
  }'
```

**Verify:**
- EMERGENCY: 10000.00 (10%)
- GOLD: 10000.00 (10%)
- ETF: 30000.00 (30%)
- STOCK: 50000.00 (50%)
- Total: 100000.00

### 4. Error Path - Invalid Strategy Code

```bash
curl -X POST http://localhost:8080/api/investment/recommendations \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 10000,
    "type": "INVALID"
  }'
```

**Expected:**
- HTTP Status: 400 or 500 (LogicException)
- Error message: "strategy config not found"

### 5. Error Path - Null Amount

```bash
curl -X POST http://localhost:8080/api/investment/recommendations \
  -H "Content-Type: application/json" \
  -d '{
    "amount": null,
    "type": "SAFE"
  }'
```

**Expected:**
- HTTP Status: 400
- Error message: "invalid amount"

### 6. Error Path - Empty Type

```bash
curl -X POST http://localhost:8080/api/investment/recommendations \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 10000,
    "type": ""
  }'
```

**Expected:**
- HTTP Status: 400
- Error message: "type is empty"

## Verification Checklist

- [ ] Database tables created successfully
- [ ] Sample data inserted correctly
- [ ] Service starts without errors
- [ ] SAFE strategy allocates correctly
- [ ] BALANCED strategy allocates correctly
- [ ] GROWTH strategy allocates correctly
- [ ] Invalid strategy returns error
- [ ] Null amount returns error
- [ ] Empty type returns error
- [ ] Amounts sum to request total
- [ ] Percentages sum to 100
- [ ] Logs show correct strategy/details loading
- [ ] Response DTOs match expected format
- [ ] No N+1 query issues (check logs)

## Rollback Procedure

If critical issues found:

1. Restore `InvestmentRecommendationsService.java` from git
2. Comment out new table creation in migrations
3. Revert to using `InvestmentAllocationMasterRepository`
4. Redeploy

```bash
git checkout HEAD -- src/main/java/com/minhnhat/demo/features/investmentRecommendations/service/InvestmentRecommendationsService.java
```

## Logs to Monitor

Enable DEBUG logging for diagnosis:

```properties
logging.level.com.minhnhat.demo.features.investmentRecommendations.service.InvestmentRecommendationsService=DEBUG
```

Expected log entries:
```
[DEBUG] Finding strategy by code: SAFE
[DEBUG] Found strategy with id: 1
[DEBUG] Loading strategy details for strategy_id: 1
[DEBUG] Allocation computed from master type=SAFE total=10000.00 => ...
[INFO] Normalized percents for type=SAFE from sum=100 to 1
```

---

**All Tests Complete**: When all test cases pass, the refactoring is production-ready.

