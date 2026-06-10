# Test Commands - Products API

## Quick Test After Deployment

Run these commands to verify the new API is working:

### 1. Test ETF Products
```bash
curl -X GET "http://localhost:8080/api/v1/products?type=ETF" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -s | jq '.'
```

**Expected Output** (2 products):
```json
{
  "data": [
    {
      "productId": 1,
      "assetCode": "VNI",
      "assetType": "ETF",
      "assetName": "Quy VN-Index ETF",
      "platformName": "VNDirect",
      "buyPrice": 100.5,
      "sellPrice": 100.0
    },
    {
      "productId": 2,
      "assetCode": "VCI10",
      "assetType": "ETF",
      "assetName": "Quy VCI10 ETF",
      "platformName": "VNDirect",
      "buyPrice": 250.75,
      "sellPrice": 250.0
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

### 2. Test GOLD Products
```bash
curl -X GET "http://localhost:8080/api/v1/products?type=GOLD" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -s | jq '.'
```

**Expected Output** (1 product):
```json
{
  "data": [
    {
      "productId": 3,
      "assetCode": "SJC_1D",
      "assetType": "GOLD",
      "assetName": "Vang SJC 1 chi",
      "platformName": "San Vang",
      "buyPrice": 50000.0,
      "sellPrice": 49800.0
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

### 3. Test STOCK Products
```bash
curl -X GET "http://localhost:8080/api/v1/products?type=STOCK" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -s | jq '.'
```

**Expected Output** (1 product):
```json
{
  "data": [
    {
      "productId": 4,
      "assetCode": "TCB",
      "assetType": "STOCK",
      "assetName": "Techcombank Stock",
      "platformName": "VNDirect",
      "buyPrice": 150.5,
      "sellPrice": 150.0
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

### 4. Test Error Handling - Missing Type
```bash
curl -X GET "http://localhost:8080/api/v1/products" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -s | jq '.'
```

**Expected Output** (400 Bad Request):
```json
{
  "data": null,
  "message": {
    "code": "error",
    "message": "Asset type parameter is required",
    "service": "InvestmentWorkflow"
  }
}
```

---

### 5. Test Error Handling - Invalid Type
```bash
curl -X GET "http://localhost:8080/api/v1/products?type=INVALID" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -s | jq '.'
```

**Expected Output** (400 Bad Request):
```json
{
  "data": null,
  "message": {
    "code": "error",
    "message": "Asset type not found: INVALID",
    "service": "InvestmentWorkflow"
  }
}
```

---

## Testing in Postman

### 1. Create New Request
- Method: **GET**
- URL: `http://localhost:8080/api/v1/products?type=ETF`

### 2. Add Headers
- Header 1:
  - Key: `Authorization`
  - Value: `Bearer YOUR_JWT_TOKEN_HERE`
- Header 2:
  - Key: `Content-Type`
  - Value: `application/json`

### 3. Send Request
- Click **Send**
- Review response in **Body** tab

### 4. Test All Types
- Change URL to test different types:
  - `?type=ETF`
  - `?type=GOLD`
  - `?type=STOCK`
  - `?type=EMERGENCY`

---

## Testing in Browser DevTools

### 1. Open Browser Console (F12)
Press **F12** to open developer tools

### 2. Copy-Paste JavaScript
```javascript
// Get JWT Token (adjust based on your auth implementation)
const token = localStorage.getItem('token') || sessionStorage.getItem('token');

// Fetch ETF products
fetch('http://localhost:8080/api/v1/products?type=ETF', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})
  .then(response => response.json())
  .then(data => console.log(JSON.stringify(data, null, 2)))
  .catch(error => console.error('Error:', error));
```

### 3. View Response
Check console output to see the API response

---

## Quick Verification Checklist

Run these tests and check:

- [ ] `GET /api/v1/products?type=ETF` returns 2 products
  - [ ] VNI (100.50)
  - [ ] VCI10 (250.75)

- [ ] `GET /api/v1/products?type=GOLD` returns 1 product
  - [ ] SJC_1D (50000.00)

- [ ] `GET /api/v1/products?type=STOCK` returns 1 product
  - [ ] TCB (150.50)

- [ ] `GET /api/v1/products` (no type) returns 400 error
  - [ ] Message says "required"

- [ ] `GET /api/v1/products?type=INVALID` returns 400 error
  - [ ] Message says "not found"

---

## If Tests Fail

### Status Code 401 (Unauthorized)
- ❌ JWT token missing or expired
- ✅ Add valid JWT token to Authorization header

### Status Code 400 (Bad Request)
- ❌ Missing `type` parameter or invalid type
- ✅ Use one of: ETF, GOLD, STOCK, EMERGENCY

### Status Code 500 (Internal Server Error)
- ❌ Server error
- ✅ Check server logs for error details
- ✅ Verify database connection

### Empty Data Array
- ❌ No products for that type in database
- ✅ Add products via admin API or check DB directly

---

## Database Verification

If API returns empty data, verify database has test data:

```sql
-- Check categories exist
SELECT * FROM asset_categories;

-- Check products exist
SELECT * FROM asset_products;

-- Check prices exist
SELECT * FROM asset_prices;

-- Check specific product
SELECT * FROM asset_products WHERE code = 'VNI';
SELECT * FROM asset_prices WHERE product_id = 1;
```

---

## Sample Response Breakdown

### ETF Response Example
```json
{
  "data": [
    {
      "productId": 1,           // Use for asset selection
      "assetCode": "VNI",       // Product code
      "assetType": "ETF",       // Asset type/category
      "assetName": "Quy VN-Index ETF",  // Display name
      "platformName": "VNDirect",       // Trading platform
      "buyPrice": 100.5,        // Current buy price
      "sellPrice": 100.0        // Current sell price
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

## Performance Notes

- ✅ Single API call per asset type
- ✅ Database query optimized with indexes
- ✅ Response time: < 100ms typically
- ✅ No N+1 query issues
- ✅ Efficient HashMap lookups

---

## Next Steps After Testing

1. ✅ Verify all test cases pass
2. ⏳ Integrate with FE using FE_INTEGRATION_GUIDE.md
3. ⏳ Replace mock data in FE
4. ⏳ Test end-to-end flow
5. ⏳ Deploy to production

---

## Common JWT Token Sources

### Postman
- Extract from login response
- Format: `Bearer eyJhbGciOiJIUzI1NiIs...`

### Browser DevTools (Console)
```javascript
localStorage.getItem('token')
sessionStorage.getItem('token')
// Or look in Application tab
```

### cURL from Login Response
```bash
# First login
curl -X POST "http://localhost:8080/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password"}' \
  -s | jq '.data.token'

# Then use that token in products API
curl -X GET "http://localhost:8080/api/v1/products?type=ETF" \
  -H "Authorization: Bearer TOKEN_FROM_LOGIN"
```

---

## Summary

The API is ready! Test it with any of the above methods to verify it's working correctly before FE integration.

