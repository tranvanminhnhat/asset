## Dashboard API - Example Requests & Responses

### 1. Get Current User's Dashboard

**Request:**
```http
GET /api/v1/dashboard
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Response (200 OK):**
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
      },
      {
        "assetType": "ETF",
        "quantity": "200.00",
        "investedCost": "5000.00",
        "currentValue": "6500.00",
        "profit": "1500.00",
        "profitRate": "30.00"
      }
    ]
  },
  "message": "Dashboard retrieved successfully",
  "statusCode": 200,
  "timestamp": "2026-04-27T10:30:45.123456"
}
```

---

### 2. Get Specific User's Dashboard (Admin)

**Request:**
```http
GET /api/v1/dashboard/user-id-12345
Authorization: Bearer {admin_jwt_token}
Content-Type: application/json
```

**Response (200 OK):**
```json
{
  "data": {
    "summary": {
      "totalInvestedCost": "50000.00",
      "totalCurrentValue": "55000.00",
      "totalProfit": "5000.00",
      "totalProfitRate": "10.00"
    },
    "portfolios": [
      {
        "assetType": "EMERGENCY",
        "quantity": "5000.00",
        "investedCost": "5000.00",
        "currentValue": "5000.00",
        "profit": "0.00",
        "profitRate": "0.00"
      },
      {
        "assetType": "STOCK",
        "quantity": "50.00",
        "investedCost": "35000.00",
        "currentValue": "40000.00",
        "profit": "5000.00",
        "profitRate": "14.29"
      },
      {
        "assetType": "GOLD",
        "quantity": "10.00",
        "investedCost": "10000.00",
        "currentValue": "10000.00",
        "profit": "0.00",
        "profitRate": "0.00"
      }
    ]
  },
  "message": "Dashboard retrieved successfully",
  "statusCode": 200,
  "timestamp": "2026-04-27T11:15:32.654321"
}
```

---

### 3. Empty Portfolio Response

**Request:**
```http
GET /api/v1/dashboard
Authorization: Bearer {jwt_token}
```

**Response (200 OK - No investments yet):**
```json
{
  "data": {
    "summary": {
      "totalInvestedCost": "0.00",
      "totalCurrentValue": "0.00",
      "totalProfit": "0.00",
      "totalProfitRate": "0.00"
    },
    "portfolios": []
  },
  "message": "Dashboard retrieved successfully",
  "statusCode": 200,
  "timestamp": "2026-04-27T12:00:00.000000"
}
```

---

### 4. Portfolio with Loss (Negative Profit)

**Request:**
```http
GET /api/v1/dashboard
Authorization: Bearer {jwt_token}
```

**Response (200 OK - Market downturn):**
```json
{
  "data": {
    "summary": {
      "totalInvestedCost": "20000.00",
      "totalCurrentValue": "18000.00",
      "totalProfit": "-2000.00",
      "totalProfitRate": "-10.00"
    },
    "portfolios": [
      {
        "assetType": "STOCK",
        "quantity": "100.00",
        "investedCost": "20000.00",
        "currentValue": "18000.00",
        "profit": "-2000.00",
        "profitRate": "-10.00"
      }
    ]
  },
  "message": "Dashboard retrieved successfully",
  "statusCode": 200,
  "timestamp": "2026-04-27T13:45:20.987654"
}
```

---

### 5. Error Response - Unauthorized

**Request:**
```http
GET /api/v1/dashboard
Authorization: Bearer {invalid_token}
```

**Response (401 Unauthorized):**
```json
{
  "statusCode": 401,
  "message": "Authentication required",
  "error": "Unauthorized access",
  "timestamp": "2026-04-27T14:20:00.123456"
}
```

---

### 6. Error Response - User Not Found

**Request:**
```http
GET /api/v1/dashboard/non-existent-user-id
Authorization: Bearer {admin_jwt_token}
```

**Response (200 OK - Empty data):**
```json
{
  "data": {
    "summary": {
      "totalInvestedCost": "0.00",
      "totalCurrentValue": "0.00",
      "totalProfit": "0.00",
      "totalProfitRate": "0.00"
    },
    "portfolios": []
  },
  "message": "Dashboard retrieved successfully",
  "statusCode": 200,
  "timestamp": "2026-04-27T14:25:30.456789"
}
```

---

## Response Field Descriptions

### Summary Object
| Field | Type | Description |
|-------|------|-------------|
| `totalInvestedCost` | BigDecimal | Sum of all amounts invested across all assets |
| `totalCurrentValue` | BigDecimal | Sum of all current values (quantity × current price) |
| `totalProfit` | BigDecimal | Total profit/loss = totalCurrentValue - totalInvestedCost |
| `totalProfitRate` | BigDecimal | Profit rate as percentage (2 decimal places) |

### PortfolioItem Object
| Field | Type | Description |
|-------|------|-------------|
| `assetType` | String | Asset type name (STOCK, GOLD, ETF, EMERGENCY, etc.) |
| `quantity` | BigDecimal | Total quantity held (aggregated across all plans) |
| `investedCost` | BigDecimal | Total amount invested at purchase price |
| `currentValue` | BigDecimal | quantity × current market price |
| `profit` | BigDecimal | currentValue - investedCost |
| `profitRate` | BigDecimal | (profit / investedCost) × 100 (%) |

---

## Calculation Examples

### Example 1: Profit Scenario
```
Asset: STOCK
quantity = 100 units
investedCost = 10,000 USD (bought at 100 USD/unit)
currentPrice = 120 USD/unit (market price)

Calculations:
currentValue = 100 × 120 = 12,000 USD
profit = 12,000 - 10,000 = 2,000 USD
profitRate = (2,000 ÷ 10,000) × 100 = 20.00%
```

### Example 2: Loss Scenario
```
Asset: STOCK
quantity = 100 units
investedCost = 12,000 USD (bought at 120 USD/unit)
currentPrice = 100 USD/unit (market price)

Calculations:
currentValue = 100 × 100 = 10,000 USD
profit = 10,000 - 12,000 = -2,000 USD (LOSS)
profitRate = (-2,000 ÷ 12,000) × 100 = -16.67%
```

### Example 3: Multiple Assets Aggregation
```
Asset 1: STOCK
investedCost = 10,000
currentValue = 12,000
profit = 2,000

Asset 2: GOLD
investedCost = 90,000
currentValue = 100,000
profit = 10,000

Summary:
totalInvestedCost = 10,000 + 90,000 = 100,000
totalCurrentValue = 12,000 + 100,000 = 112,000
totalProfit = 112,000 - 100,000 = 12,000
totalProfitRate = (12,000 ÷ 100,000) × 100 = 12.00%
```

---

## Using cURL

### Get current user's dashboard:
```bash
curl -X GET http://localhost:8080/api/v1/dashboard \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Get specific user's dashboard (admin):
```bash
curl -X GET http://localhost:8080/api/v1/dashboard/user-id-12345 \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

---

## Using Postman

1. **Create new request**: GET request
2. **URL**: `http://localhost:8080/api/v1/dashboard`
3. **Headers**:
   - Authorization: `Bearer {jwt_token}`
   - Content-Type: `application/json`
4. **Send**
5. **Check Response**: Look for data with summary and portfolios arrays

---

## Notes

- All financial values use `BigDecimal` for precision
- Percentages are shown with 2 decimal places (e.g., 20.00%)
- Timestamp format: ISO 8601 (2026-04-27T10:30:45.123456)
- Empty portfolios return summary with all zeros
- Division by zero is handled safely (profitRate = 0 if investedCost is 0)

