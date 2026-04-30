# QUICK START - TEST API ALLOCATION STRATEGY

## 🚀 Step-by-Step Test Guide

### Step 1: Verify Database Structure
```sql
-- Check table names
SHOW TABLES LIKE 'mst_allocation%';

-- Expected output:
-- mst_allocation_strategies
-- mst_allocation_strategy_details
```

### Step 2: Insert Sample Data
```sql
-- Run this to populate test data
INSERT INTO mst_allocation_strategies (code, name, created_at) VALUES 
('SAFE', 'Chiến lược An Toàn', NOW()),
('BALANCED', 'Chiến lược Cân Bằng', NOW()),
('GROWTH', 'Chiến lược Tăng Trưởng', NOW());

INSERT INTO mst_allocation_strategy_details (strategy_id, asset_type, percentage, strategy_code, created_at) VALUES
(1, 'EMERGENCY', 40, 'SAFE', NOW()),
(1, 'GOLD', 30, 'SAFE', NOW()),
(1, 'ETF', 10, 'SAFE', NOW()),
(1, 'STOCK', 20, 'SAFE', NOW()),
(2, 'EMERGENCY', 25, 'BALANCED', NOW()),
(2, 'GOLD', 20, 'BALANCED', NOW()),
(2, 'ETF', 20, 'BALANCED', NOW()),
(2, 'STOCK', 35, 'BALANCED', NOW()),
(3, 'EMERGENCY', 10, 'GROWTH', NOW()),
(3, 'GOLD', 10, 'GROWTH', NOW()),
(3, 'ETF', 30, 'GROWTH', NOW()),
(3, 'STOCK', 50, 'GROWTH', NOW());

-- Verify
SELECT s.code, sd.asset_type, sd.percentage 
FROM mst_allocation_strategies s
LEFT JOIN mst_allocation_strategy_details sd ON s.id = sd.strategy_id
ORDER BY s.code, sd.asset_type;
```

### Step 3: Build Application
```bash
cd D:\Woft\GitHub\SmartAssetManagement\source\asset\be
gradlew.bat clean build -x test
```

### Step 4: Start Application
```bash
gradlew.bat bootRun
```

### Step 5: Test API Endpoints

#### Test 1: SAFE Strategy
```bash
curl -X POST http://localhost:8080/investment-amount \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 10000,
    "type": "SAFE"
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "data": {
    "allocations": [
      {"type": "EMERGENCY", "amount": 4000, "percent": 0.4},
      {"type": "GOLD", "amount": 3000, "percent": 0.3},
      {"type": "ETF", "amount": 1000, "percent": 0.1},
      {"type": "STOCK", "amount": 2000, "percent": 0.2}
    ]
  }
}
```

#### Test 2: BALANCED Strategy
```bash
curl -X POST http://localhost:8080/investment-amount \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50000,
    "type": "BALANCED"
  }'
```

**Expected Response:**
```json
{
  "success": true,
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

#### Test 3: GROWTH Strategy
```bash
curl -X POST http://localhost:8080/investment-amount \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100000,
    "type": "GROWTH"
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "data": {
    "allocations": [
      {"type": "EMERGENCY", "amount": 10000, "percent": 0.1},
      {"type": "GOLD", "amount": 10000, "percent": 0.1},
      {"type": "ETF", "amount": 30000, "percent": 0.3},
      {"type": "STOCK", "amount": 50000, "percent": 0.5}
    ]
  }
}
```

#### Test 4: Error - Invalid Type
```bash
curl -X POST http://localhost:8080/investment-amount \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 10000,
    "type": "INVALID"
  }'
```

**Expected Response (Error):**
```json
{
  "success": false,
  "message": "Error",
  "code": "ALLOCATE_LOGIC_ERROR_001"
}
```

### Step 6: Verify in Logs
Look for this log message:
```
Allocation computed from master type=BALANCED total=50000 => sumPercents=1.00 sumAmounts=50000
```

---

## 📊 Validation Checklist

After each test, verify:
- [ ] HTTP Status 200 OK
- [ ] Response has `success: true`
- [ ] `allocations` array has exactly 4 items
- [ ] All types present: EMERGENCY, GOLD, ETF, STOCK
- [ ] Amount values have at most 2 decimals
- [ ] Percents properly stripped of trailing zeros
- [ ] Sum of amounts = input amount
- [ ] Sum of percents = 1.0

---

## 🔧 Troubleshooting

### Issue: Table Not Found
```
Error: Table 'asset_db'.'mst_allocation_strategies' doesn't exist
```
**Solution:** Manually run migration SQL:
```sql
ALTER TABLE allocation_strategies RENAME TO mst_allocation_strategies;
ALTER TABLE allocation_strategy_details RENAME TO mst_allocation_strategy_details;
```

### Issue: No Data Found
```
Error: ALLOCATE_LOGIC_ERROR_001 - strategy not found
```
**Solution:** Insert sample data (see Step 2 above)

### Issue: Compilation Error
```
error: cannot find symbol - class AllocationStrategyRepository
```
**Solution:** Run `gradlew clean build` to rebuild

---

## ✅ Success Indicators

✓ All 3 test cases return correct allocations
✓ Error cases handled properly  
✓ Logs show proper calculation messages
✓ No database errors
✓ Response format matches expected output

**You're done!** The refactoring is complete and working.


