# 📈 Investment Workflow - Complete API Flow Guide

## 🎯 Overall User Journey

```
Login → Dashboard → Click "Đầu tư ngay" → Choose Strategy → Select Assets → Review → Finalize → Complete
```

---

## 📊 Step-by-Step Flow with API Calls

### **Phase 1: Dashboard & Strategy Selection**

#### Step 0️⃣: Get Allocation Master (Dashboard Load)
**When**: FE loads dashboard, user clicks "Đầu tư ngay" button
**API**: `GET /api/v1/allocation-master`
**Authentication**: Required (JWT Token)
**Response**:
```json
{
  "data": {
    "allocations": [
      {
        "strategyCode": "safe",
        "strategyName": "Chiến lược An toàn",
        "details": [
          {
            "assetType": "EMERGENCY",
            "percentage": 10.00,
            "minimumLotSizeAmount": 0
          },
          {
            "assetType": "GOLD",
            "percentage": 20.00,
            "minimumLotSizeAmount": 100
          },
          {
            "assetType": "ETF",
            "percentage": 50.00,
            "minimumLotSizeAmount": 100000
          },
          {
            "assetType": "STOCK",
            "percentage": 20.00,
            "minimumLotSizeAmount": 500000
          }
        ]
      }
    ]
  },
  "message": {
    "code": "investment.master.info",
    "text": "Lay danh sach cau hinh phan bo dau tu thanh cong",
    "service": "InvestmentService"
  }
}
```

---

### **Phase 2: Create Investment Session**

#### Step 1️⃣: User enters amount & clicks "Xem gợi ý đầu tư"
**API**: `POST /api/v1/investment/session`
**Authentication**: Required (JWT Token)
**Request Body**:
```json
{
  "strategyCode": "safe",
  "totalAmount": 10000000
}
```
**Response**:
```json
{
  "data": {
    "sessionId": 8,
    "userId": "user123",
    "strategyCode": "safe",
    "totalAmount": 10000000,
    "status": "DRAFT",
    "currentStep": "RECOMMEND",
    "recommendations": [
      {
        "type": "EMERGENCY",
        "amount": 1000000,
        "percent": 10.00,
        "status": "LOCKED"
      },
      {
        "type": "GOLD",
        "amount": 2000000,
        "percent": 20.00,
        "status": "NOT_STARTED"
      },
      {
        "type": "ETF",
        "amount": 5000000,
        "percent": 50.00,
        "status": "NOT_STARTED"
      },
      {
        "type": "STOCK",
        "amount": 2000000,
        "percent": 20.00,
        "status": "NOT_STARTED"
      }
    ]
  },
  "message": {
    "code": "investment.session.create.info.001",
    "text": "Tao phien dau tu thanh cong",
    "service": "InvestmentService"
  }
}
```

**⚠️ Important**: Save `sessionId: 8` for all subsequent API calls!

---

### **Phase 3: Get Investment Recommendations**

#### Step 2️⃣: View recommendations on screen
**API**: `GET /api/v1/investment/8/recommendations`
**Authentication**: Required
**Response**:
```json
{
  "data": {
    "sessionId": 8,
    "recommendations": [
      {
        "type": "EMERGENCY",
        "amount": 1000000,
        "percent": 10.00
      },
      {
        "type": "GOLD",
        "amount": 2000000,
        "percent": 20.00
      },
      {
        "type": "ETF",
        "amount": 5000000,
        "percent": 50.00
      },
      {
        "type": "STOCK",
        "amount": 2000000,
        "percent": 20.00
      }
    ]
  },
  "message": { ... }
}
```

---

### **Phase 4: Process Each Asset Type (Loop)**

#### Step 3️⃣: User clicks on "ETF" card → Get Available ETF Assets
**API**: `GET /api/v1/assets?type=ETF`
**Authentication**: Required
**Response**:
```json
{
  "data": [
    {
      "assetCode": "ETF001",
      "assetType": "ETF",
      "assetName": "Quỹ ETF Vàng",
      "buyPrice": 100.50,
      "sellPrice": 100.00,
      "isActive": true
    },
    {
      "assetCode": "ETF002",
      "assetType": "ETF",
      "assetName": "Quỹ ETF Chứng chỉ",
      "buyPrice": 250.75,
      "sellPrice": 250.00,
      "isActive": true
    }
  ],
  "message": { ... }
}
```

#### Step 4️⃣: User selects ETF & enters quantity → Select Asset
**API**: `POST /api/v1/investment/8/allocation/2/select`
**Authentication**: Required
**Note**: `allocationId=2` is the ID of ETF allocation (returned in session creation)

**Request Body**:
```json
{
  "assetCode": "ETF001",
  "quantity": 10
}
```

**Validation**:
- quantity > 0 ✅
- (quantity × buyPrice) ≤ recommended_amount ✅
  - Here: 10 × 100.50 = 1005 ≤ 5000000 ✅

**Response**:
```json
{
  "data": {
    "selectionId": 101,
    "allocationId": 2,
    "assetCode": "ETF001",
    "assetName": "Quỹ ETF Vàng",
    "quantity": 10,
    "buyPrice": 100.50,
    "investedAmount": 1005,
    "residualAmount": 4998995,
    "createdAt": "2026-06-08T13:25:00"
  },
  "message": {
    "code": "investment.allocation.selectAsset.info.001",
    "text": "Chon tai san cho phan bo thanh cong",
    "service": "InvestmentService"
  }
}
```

**Allocation Status Change**: `NOT_STARTED` → `IN_PROGRESS`

#### Step 5️⃣: User clicks "Hoàn thành" on ETF card → Complete Allocation
**API**: `POST /api/v1/investment/8/allocation/2/complete`
**Authentication**: Required
**Request Body**: Empty (just POST)

**Response**:
```json
{
  "data": {
    "allocationId": 2,
    "assetType": "ETF",
    "status": "COMPLETED",
    "investedAmount": 1005,
    "residualAmount": 4998995,
    "updatedAt": "2026-06-08T13:26:00"
  },
  "message": { ... }
}
```

**Allocation Status Change**: `IN_PROGRESS` → `COMPLETED`

---

#### Step 6️⃣: Repeat Steps 3-5 for GOLD & STOCK

**For GOLD**:
- `GET /api/v1/assets?type=GOLD`
- User selects Gold product (e.g., assetCode=GOLD001)
- `POST /api/v1/investment/8/allocation/3/select`
- `POST /api/v1/investment/8/allocation/3/complete`

**For STOCK**:
- `GET /api/v1/assets?type=STOCK`
- User selects Stock (e.g., assetCode=STOCK001)
- `POST /api/v1/investment/8/allocation/4/select`
- `POST /api/v1/investment/8/allocation/4/complete`

**Note**: Skip EMERGENCY (allocationId=1) - it's LOCKED!

---

### **Phase 5: Review Investment Session**

#### Step 7️⃣: User clicks "Tiếp tục" → Review Session
**API**: `POST /api/v1/investment/8/review`
**Authentication**: Required
**Request Body**: Empty

**What happens**:
- Session status: `DRAFT` → `REVIEW`
- Current step: `RECOMMEND` → `REVIEW`
- System calculates totals from all allocations

**Response**:
```json
{
  "data": {
    "sessionId": 8,
    "status": "REVIEW",
    "totalInvested": 1005 + 500000 + 1000000 = 1501005,
    "totalResidual": 4998995 + 1500000 + 1000000 = 6498995,
    "updatedAt": "2026-06-08T13:27:00"
  },
  "message": { ... }
}
```

#### Step 8️⃣: FE displays Review Screen
**Show to user**:
- Total Amount: 10,000,000 VND
- Total Invested: 1,501,005 VND
- Total Residual: 6,498,995 VND
- Breakdown by asset:
  - ETF: 1,005 invested + 4,998,995 residual
  - GOLD: 500,000 invested + 1,500,000 residual
  - STOCK: 1,000,000 invested + 1,000,000 residual
  - EMERGENCY: 0 invested + 1,000,000 residual (LOCKED)

---

### **Phase 6: Get Session Details (Optional)**

#### Step 9️⃣: FE wants full session details with allocations
**API**: `GET /api/v1/investment/8/detail`
**Authentication**: Required

**Response**:
```json
{
  "data": {
    "sessionId": 8,
    "userId": "user123",
    "strategyCode": "safe",
    "totalAmount": 10000000,
    "status": "REVIEW",
    "currentStep": "REVIEW",
    "allocations": [
      {
        "allocationId": 1,
        "assetType": "EMERGENCY",
        "recommendedAmount": 1000000,
        "percentage": 10.00,
        "status": "LOCKED",
        "investedAmount": 0,
        "residualAmount": 1000000,
        "selections": []
      },
      {
        "allocationId": 2,
        "assetType": "ETF",
        "recommendedAmount": 5000000,
        "percentage": 50.00,
        "status": "COMPLETED",
        "investedAmount": 1005,
        "residualAmount": 4998995,
        "selections": [
          {
            "selectionId": 101,
            "assetCode": "ETF001",
            "assetName": "Quỹ ETF Vàng",
            "quantity": 10,
            "buyPrice": 100.50,
            "investedAmount": 1005,
            "createdAt": "2026-06-08T13:25:00"
          }
        ]
      },
      {
        "allocationId": 3,
        "assetType": "GOLD",
        "recommendedAmount": 2000000,
        "percentage": 20.00,
        "status": "COMPLETED",
        "investedAmount": 500000,
        "residualAmount": 1500000,
        "selections": [...]
      },
      {
        "allocationId": 4,
        "assetType": "STOCK",
        "recommendedAmount": 2000000,
        "percentage": 20.00,
        "status": "COMPLETED",
        "investedAmount": 1000000,
        "residualAmount": 1000000,
        "selections": [...]
      }
    ]
  },
  "message": { ... }
}
```

---

### **Phase 7: Finalize Investment Session**

#### Step 1️0️⃣: User clicks "Hoàn thành" on Review screen → Finalize
**API**: `POST /api/v1/investment/8/finalize`
**Authentication**: Required
**Request Body**: Empty

**What happens**:
1. Session status: `REVIEW` → `COMPLETED`
2. Current step: `REVIEW` → `FINALIZE`
3. ALL residual amounts are summed: 1,000,000 + 4,998,995 + 1,500,000 + 1,000,000 = **7,998,995**
4. User's cash balance is updated: `user_cash_balance.cash_amount += 7,998,995`
5. Event `SESSION_FINALIZED` is created

**Response**:
```json
{
  "data": {
    "sessionId": 8,
    "status": "COMPLETED",
    "totalInvested": 1501005,
    "totalResidual": 6498995,
    "updatedCashBalance": 7998995,
    "completedAt": "2026-06-08T13:28:00"
  },
  "message": {
    "code": "investment.session.finalize.info.001",
    "text": "Hoan tat phien dau tu va cap nhat so du thanh cong",
    "service": "InvestmentService"
  }
}
```

#### Step 1️1️⃣: FE displays Success Screen
**Show**:
- ✅ Investment session completed successfully!
- Total Amount Invested: 1,501,005 VND
- Total Residual Added to Cash: 7,998,995 VND
- Current Cash Balance: 7,998,995 VND
- Session Status: COMPLETED

---

## 🗂️ API Endpoints Summary

| # | Method | Endpoint | Purpose | Status |
|---|--------|----------|---------|--------|
| 0 | GET | `/api/v1/allocation-master` | Get all strategies | ✅ NEW |
| 1 | POST | `/api/v1/investment/session` | Create investment session | ✅ |
| 2 | GET | `/api/v1/investment/{sessionId}/recommendations` | Get recommendations | ✅ |
| 3 | GET | `/api/v1/assets?type=ETF` | Get available assets | ✅ |
| 4 | POST | `/api/v1/investment/{sessionId}/allocation/{allocationId}/select` | Select asset | ✅ |
| 5 | POST | `/api/v1/investment/{sessionId}/allocation/{allocationId}/complete` | Complete allocation | ✅ |
| 6 | POST | `/api/v1/investment/{sessionId}/review` | Review session | ✅ |
| 7 | POST | `/api/v1/investment/{sessionId}/finalize` | Finalize & complete | ✅ |
| 8 | GET | `/api/v1/investment/{sessionId}/detail` | Get full details | ✅ |

---

## 🔐 Authentication

**All endpoints require JWT token**:
```
Authorization: Bearer <JWT_TOKEN>
```

The token will be parsed to extract **userId** for security isolation.

---

## ⚙️ Status Transitions

### Session Status Flow
```
DRAFT → PROCESSING → REVIEW → COMPLETED
```

### Allocation Status Flow
```
NOT_STARTED → IN_PROGRESS → COMPLETED
(LOCKED = special state for EMERGENCY asset type)
```

---

## 🚨 Common Error Scenarios

### Investment session not found
- ✅ **FIX**: Ensure sessionId is correct and user owns the session
- ✅ **FIX**: Make sure JWT token contains valid userId

### Allocation is locked and cannot be processed
- This is **EXPECTED** for EMERGENCY allocations
- ✅ **SKIP** the EMERGENCY card in the UI (no process button)

### Invested amount exceeds recommended amount
- ✅ **VALIDATE** before submitting: (quantity × buyPrice) ≤ recommended_amount
- ✅ **DISPLAY** error message if user tries to exceed

### User cash balance not found
- ✅ **FIX**: Ensure user account exists and user_cash_balance record is created
- ✅ **CREATE** record during user registration with initial 0 balance

---

## 📋 Test Data Setup

### Prerequisites
1. **User account must exist** in database:
   ```sql
   SELECT * FROM user WHERE user_id = 'test-user-id';
   ```

2. **User cash balance record must exist**:
   ```sql
   INSERT INTO user_cash_balance (user_id, cash_amount) 
   VALUES ('test-user-id', 0);
   ```

3. **Allocation strategies must exist**:
   ```sql
   SELECT * FROM mst_allocation_strategies WHERE code = 'safe';
   ```

4. **Asset prices must exist**:
   ```sql
   INSERT INTO asset_prices (asset_code, asset_type, asset_name, buy_price, sell_price, is_active)
   VALUES 
     ('ETF001', 'ETF', 'Quỹ ETF Vàng', 100.50, 100.00, true),
     ('GOLD001', 'GOLD', 'Vàng 999', 50000, 49000, true),
     ('STOCK001', 'STOCK', 'Cổ phiếu VNM', 150.50, 150.00, true);
   ```

---

## 🧪 Testing Checklist

- [ ] **Phase 1**: `GET /api/v1/allocation-master` → Returns strategies
- [ ] **Phase 2**: `POST /api/v1/investment/session` → Returns sessionId
- [ ] **Phase 3**: `GET /api/v1/investment/{id}/recommendations` → Returns correct totals
- [ ] **Phase 4a**: `GET /api/v1/assets?type=ETF` → Returns active assets
- [ ] **Phase 4b**: `POST /api/v1/investment/{id}/allocation/{id}/select` → Updates investment
- [ ] **Phase 4c**: `POST /api/v1/investment/{id}/allocation/{id}/complete` → Changes status
- [ ] **Phase 5**: `POST /api/v1/investment/{id}/review` → Calculates totals correctly
- [ ] **Phase 6**: `GET /api/v1/investment/{id}/detail` → Shows all allocations & selections
- [ ] **Phase 7**: `POST /api/v1/investment/{id}/finalize` → Updates cash balance
- [ ] **Validation**: User cannot process EMERGENCY allocation
- [ ] **Validation**: Cannot exceed recommended amount for allocation
- [ ] **Validation**: Only owner can access session

---

**Good luck! 🚀**

