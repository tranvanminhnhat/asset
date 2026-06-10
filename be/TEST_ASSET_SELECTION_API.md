# Test Script for Asset Selection APIs

## API 1: Get Available Assets (with type filter)

### Without Filter - Get All Assets
```bash
curl -X GET "http://localhost:8080/api/v1/assets" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json"
```

**Expected Response**:
```json
{
  "data": [
    {
      "id": 1,
      "productId": 1,
      "assetCode": "VNI",
      "assetType": "ETF",
      "assetName": "Quy VN-Index ETF",
      "buyPrice": 100.500000,
      "sellPrice": 100.000000,
      "priceDate": "2026-06-10T10:30:00",
      "isLatest": true,
      "isActive": true
    },
    {
      "id": 3,
      "productId": 3,
      "assetCode": "SJC_1D",
      "assetType": "GOLD",
      "assetName": "Vang SJC 1 chi",
      "buyPrice": 50000.000000,
      "sellPrice": 49800.000000,
      "priceDate": "2026-06-10T10:30:00",
      "isLatest": true,
      "isActive": true
    },
    {
      "id": 4,
      "productId": 4,
      "assetCode": "TCB",
      "assetType": "STOCK",
      "assetName": "Techcombank Stock",
      "buyPrice": 150.500000,
      "sellPrice": 150.000000,
      "priceDate": "2026-06-10T10:30:00",
      "isLatest": true,
      "isActive": true
    }
  ],
  "message": {
    "code": "GET_ASSETS_INFO",
    "message": "Danh sach tai san khop yeu cau",
    "service": "InvestmentWorkflow"
  }
}
```

### With Type Filter - Get Only GOLD Assets
```bash
curl -X GET "http://localhost:8080/api/v1/assets?type=GOLD" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json"
```

**Expected Response**:
```json
{
  "data": [
    {
      "id": 3,
      "productId": 3,
      "assetCode": "SJC_1D",
      "assetType": "GOLD",
      "assetName": "Vang SJC 1 chi",
      "buyPrice": 50000.000000,
      "sellPrice": 49800.000000,
      "priceDate": "2026-06-10T10:30:00",
      "isLatest": true,
      "isActive": true
    }
  ],
  "message": {
    "code": "GET_ASSETS_INFO",
    "message": "Danh sach tai san khop yeu cau",
    "service": "InvestmentWorkflow"
  }
}
```

---

## API 2: Select Asset for Allocation

### Request Body
```json
{
  "productId": 3,
  "assetType": "GOLD",
  "assetCode": "SJC_1D",
  "quantity": 10
}
```

### Successful Selection - GOLD Asset
```bash
curl -X POST "http://localhost:8080/api/v1/investment/1/allocation/5/select" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 3,
    "assetType": "GOLD",
    "quantity": 10
  }'
```

**Expected Response** (Status: 200 OK):
```json
{
  "data": {
    "selectionId": 100,
    "allocationId": 5,
    "assetCode": "SJC_1D",
    "assetType": "GOLD",
    "assetName": "Vang SJC 1 chi",
    "quantity": 10,
    "buyPrice": 50000.000000,
    "investedAmount": 500000.00,
    "residualAmount": 1500000.00,
    "createdAt": "2026-06-10T10:35:00"
  },
  "message": {
    "code": "SELECT_ASSET_INFO",
    "message": "Da chon tai san thanh cong",
    "service": "InvestmentWorkflow"
  }
}
```

---

## API 3: Error Scenarios

### Error Case 1: Asset Type Mismatch
```bash
# Allocation expects GOLD, but request sends STOCK
curl -X POST "http://localhost:8080/api/v1/investment/1/allocation/5/select" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 4,
    "assetType": "STOCK",
    "quantity": 10
  }'
```

**Expected Response** (Status: 400 Bad Request):
```json
{
  "data": null,
  "message": {
    "code": "",
    "message": "Asset type does not match allocation type",
    "service": "InvestmentWorkflow"
  }
}
```

### Error Case 2: Product Not Found
```bash
curl -X POST "http://localhost:8080/api/v1/investment/1/allocation/5/select" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 9999,
    "assetType": "GOLD",
    "quantity": 10
  }'
```

**Expected Response** (Status: 400 Bad Request):
```json
{
  "data": null,
  "message": {
    "code": "",
    "message": "Asset not found",
    "service": "InvestmentWorkflow"
  }
}
```

### Error Case 3: Invalid Quantity
```bash
curl -X POST "http://localhost:8080/api/v1/investment/1/allocation/5/select" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 3,
    "assetType": "GOLD",
    "quantity": 0
  }'
```

**Expected Response** (Status: 400 Bad Request):
```json
{
  "data": null,
  "message": {
    "code": "",
    "message": "Quantity must be greater than 0",
    "service": "InvestmentWorkflow"
  }
}
```

### Error Case 4: Investment Exceeds Recommended Amount
```bash
# Allocation has 500k recommended, already invested 100k, residual 400k
# Try to invest 500k more (5000 units @ 50000 = 250k) - should succeed
# But try to invest 600k more (30 units @ 20k = 600k) - should fail
curl -X POST "http://localhost:8080/api/v1/investment/1/allocation/5/select" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 3,
    "assetType": "GOLD",
    "quantity": 30,
    "assetCode": "SJC_1D"
  }'
```

**Expected Response** (Status: 400 Bad Request):
```json
{
  "data": null,
  "message": {
    "code": "",
    "message": "Invested amount cannot exceed recommended amount",
    "service": "InvestmentWorkflow"
  }
}
```

---

## Database Verification

After successful selection, verify data in database:

```sql
-- Check investment_asset_selections table
SELECT * FROM investment_asset_selections 
WHERE id = 100;

-- Expected columns populated:
-- id = 100
-- session_id = 1
-- allocation_id = 5
-- asset_code = "SJC_1D"
-- asset_name = "Vang SJC 1 chi"
-- category_code = "GOLD"
-- product_id = 3
-- quantity = 10.0000
-- buy_price = 50000.000000
-- invested_amount = 500000.00
-- created_at = 2026-06-10 10:35:00
```

---

## Key Points

✅ **assetType Validation**: Ensures user can only select asset type matching allocation
✅ **Proper Field Mapping**: assetCode, assetType, assetName properly populated from DB
✅ **Database Integrity**: All values stored correctly in investment_asset_selections
✅ **Response Consistency**: FE receives exactly what it sent in request + computed values
✅ **Backward Compatibility**: Old code sending only assetCode still works
✅ **Filter Support**: GET /api/v1/assets?type=GOLD returns only GOLD assets

---

## Integration Flow

```
1. User on Investment Processing Screen
   ↓
2. FE Calls GET /api/v1/assets?type=GOLD
   Returns list of GOLD assets with populated assetCode, assetName, buyPrice
   ↓
3. User selects "SJC_1D Vang SJC 1 chi" and enters quantity 10
   ↓
4. FE Calls POST /api/v1/investment/{sessionId}/allocation/{allocationId}/select
   Body: {productId: 3, assetType: "GOLD", quantity: 10}
   ↓
5. BE Validates:
   - Allocation exists and belongs to user
   - assetType "GOLD" matches allocation.assetType ✓
   - Product 3 exists and is active ✓
   - Price for product 3 exists ✓
   - Investment amount won't exceed recommendation ✓
   ↓
6. BE Creates InvestmentAssetSelectionEntity with:
   - assetCode: "SJC_1D"
   - assetType/categoryCode: "GOLD"
   - assetName: "Vang SJC 1 chi"
   - buyPrice: 50000.00
   - investedAmount: 500000.00
   ↓
7. FE Receives Response:
   {
     selectionId: 100,
     assetCode: "SJC_1D",
     assetType: "GOLD",
     assetName: "Vang SJC 1 chi",
     investedAmount: 500000.00,
     residualAmount: 1500000.00
   }
   ↓
8. FE Updates Screen showing selected asset and amounts
```

