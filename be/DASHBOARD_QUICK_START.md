## Dashboard API - Quick Start Guide

### 🚀 Quick Setup (5 minutes)

#### 1. **Verify Database Schema**
Ensure these tables exist in MySQL:

```sql
-- Check if tables exist
SHOW TABLES LIKE 'investment%';
SHOW TABLES LIKE 'asset_prices';
```

Expected output:
- ✅ `investment_plans`
- ✅ `investment_plan_allocations`
- ✅ `asset_prices`

#### 2. **Insert Sample Data** (Optional)

```sql
-- Sample asset prices
INSERT INTO asset_prices (asset_type, price, created_at, updated_at) VALUES
('STOCK', 120.00, NOW(), NOW()),
('GOLD', 2000.00, NOW(), NOW()),
('ETF', 32.50, NOW(), NOW()),
('EMERGENCY', 1.00, NOW(), NOW());

-- Sample investment plan
INSERT INTO investment_plans (user_id, total_amount, created_at, updated_at) VALUES
('user-123', 100000.00, NOW(), NOW());

-- Get the last inserted plan ID
SELECT @plan_id := id FROM investment_plans WHERE user_id = 'user-123' ORDER BY id DESC LIMIT 1;

-- Sample allocations
INSERT INTO investment_plan_allocations (plan_id, asset_type, amount, quantity, percentage, created_at, updated_at) VALUES
(@plan_id, 'STOCK', 10000.00, 100.00, 20.00, NOW(), NOW()),
(@plan_id, 'GOLD', 90000.00, 50.00, 80.00, NOW(), NOW());
```

#### 3. **Build & Run**

```bash
# Clean and build
./gradlew clean build

# Start application
./gradlew bootRun

# Expected output:
# Started AssetApplication in X seconds
```

#### 4. **Test the API**

**Option A: Using cURL**
```bash
curl -X GET http://localhost:8080/api/v1/dashboard \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" | jq
```

**Option B: Using Postman**
1. Create GET request to: `http://localhost:8080/api/v1/dashboard`
2. Add Header: `Authorization: Bearer YOUR_JWT_TOKEN`
3. Click Send
4. Check response in JSON tab

**Option C: Using HTTP Client (IDE)**
```
GET http://localhost:8080/api/v1/dashboard
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json
```

#### 5. **Expected Response**

```json
{
  "data": {
    "summary": {
      "totalInvestedCost": "100000.00",
      "totalCurrentValue": "112000.00",
      "totalProfit": "12000.00",
      "totalProfitRate": "12.00"
    },
    "portfolios": [
      {
        "assetType": "STOCK",
        "quantity": "100.00",
        "investedCost": "10000.00",
        "currentValue": "12000.00",
        "profit": "2000.00",
        "profitRate": "20.00"
      },
      {
        "assetType": "GOLD",
        "quantity": "50.00",
        "investedCost": "90000.00",
        "currentValue": "100000.00",
        "profit": "10000.00",
        "profitRate": "11.11"
      }
    ]
  },
  "statusCode": 200,
  "message": "Dashboard retrieved successfully"
}
```

---

### 🔍 Troubleshooting

#### Issue: 401 Unauthorized
**Cause**: Invalid JWT token
**Solution**: 
- Login first to get valid token
- Add token to Authorization header

#### Issue: Empty portfolio list
**Cause**: No investment plans for user
**Solution**:
- Create investment plan with allocations
- Use sample data above

#### Issue: "User not found" error
**Cause**: SecurityUtils.getCurrentUser() returns null
**Solution**:
- Ensure you're logged in
- Check JWT token validity
- Verify user exists in database

#### Issue: Calculation seems wrong
**Check**:
- currentValue = quantity × currentPrice ✓
- profit = currentValue - investedCost ✓
- profitRate = (profit ÷ investedCost) × 100 ✓

---

### 📊 Verification Checklist

Run this to verify everything is working:

```bash
# 1. Check compilation
./gradlew clean build -q
echo "✓ Build successful"

# 2. Check database connection
# (Look for "Connected to MySQL" in logs during startup)

# 3. Run unit tests
./gradlew test --tests DashboardServiceTest -q
echo "✓ Tests passed"

# 4. Start application
./gradlew bootRun > app.log &

# 5. Wait for startup
sleep 10

# 6. Test endpoint
curl -s http://localhost:8080/api/v1/dashboard \
  -H "Authorization: Bearer test-token" | jq . && echo "✓ API working"
```

---

### 💡 Common Use Cases

#### 1. **Get Your Portfolio Dashboard**
```bash
curl -X GET http://localhost:8080/api/v1/dashboard \
  -H "Authorization: Bearer $JWT_TOKEN"
```

#### 2. **Get User Dashboard (Admin)**
```bash
curl -X GET http://localhost:8080/api/v1/dashboard/user-id-12345 \
  -H "Authorization: Bearer $ADMIN_JWT_TOKEN"
```

#### 3. **Monitor Profit/Loss**
Check `summary.totalProfit` and `summary.totalProfitRate`:
- Positive = Gain
- Negative = Loss
- 0 = Break even

#### 4. **Per-Asset Analysis**
Look at `portfolios[].profitRate`:
- Which asset is performing best
- Which asset needs rebalancing
- Overall portfolio health

---

### 📚 Documentation Files

- **DASHBOARD_API_GUIDE.md** - Detailed technical documentation
- **DASHBOARD_API_EXAMPLES.md** - API examples and responses
- **DashboardService.java** - Service implementation with comments
- **DashboardServiceTest.java** - Unit tests with examples

---

### 🎯 Next Steps

1. ✅ Verify database schema
2. ✅ Insert sample data (optional)
3. ✅ Build and run application
4. ✅ Test API endpoints
5. ✅ Monitor logs and responses

**You're all set! 🚀**

