# Asset Selection API - Architecture & Data Flow Diagram

## System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         Frontend (React/Angular)                        │
│                                                                         │
│  ┌─────────────────────────────┐      ┌──────────────────────────────┐ │
│  │ Investment Processing Screen│      │   Asset Selection Component  │ │
│  │                             │      │                              │ │
│  │ • Select Allocation (GOLD)  ├─────►│ • Load Assets by Type        │ │
│  │ • View Recommended Amount   │      │ • Display Available Assets   │ │
│  │ • See Residual Amount       │      │ • Select Asset to Invest     │ │
│  └─────────────────────────────┘      └──────────────────────────────┘ │
└────────────────┬───────────────────────────────────────────────────┬─────┘
                 │                                                   │
                 │ API Request 1: Get Assets                         │
                 │ GET /api/v1/assets?type=GOLD                      │
                 │                                                   │
                 ▼                                                   ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                          Backend Services                               │
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │         GetAvailableAssetsService                               │  │
│  │                                                                  │  │
│  │ 1. loadData()                                                    │  │
│  │    ├─ Load all latest active asset prices                      │  │
│  │    ├─ Load all active products (map by ID)                    │  │
│  │    └─ Load all active asset categories (map by ID)            │  │
│  │                                                                  │  │
│  │ 2. processBusiness()                                            │  │
│  │    ├─ Filter: assetType == "GOLD" ?                           │  │
│  │    │         (AssetCategoryEntity.code == "GOLD")             │  │
│  │    ├─ For each matching asset:                                │  │
│  │    │  ├─ assetCode ← product.code                            │  │
│  │    │  ├─ assetType ← category.code                           │  │
│  │    │  └─ assetName ← product.name                            │  │
│  │    └─ Return populated AssetPriceResponse list                │  │
│  │                                                                  │  │
│  │ 3. createResponse()                                             │  │
│  │    └─ Wrap in ApiResponse with message                        │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                              ▲                                          │
│                              │ Query                                    │
│                              │                                          │
└──────────────────────────────┼──────────────────────────────────────────┘
                               │
                               │ API Response:
                               │ {
                               │   data: [
                               │     {
                               │       productId: 3,
                               │       assetCode: "SJC_1D",
                               │       assetType: "GOLD",
                               │       assetName: "Vang SJC 1 chi",
                               │       buyPrice: 50000.00,
                               │       ...
                               │     }
                               │   ]
                               │ }
                               │
                               ◄─────────────────────────────────────────
                                      FE displays list to user
                                      User selects: SJC_1D (productId: 3)
                                      User enters: quantity 10
                                      ▼
                               API Request 2: Select Asset
                               POST /api/v1/investment/1/allocation/5/select
                               {
                                 productId: 3,
                                 assetType: "GOLD",
                                 quantity: 10
                               }
                               │
                               ▼

┌─────────────────────────────────────────────────────────────────────────┐
│                          Backend Services (cont'd)                      │
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │       SelectAssetForAllocationService                            │  │
│  │                                                                  │  │
│  │ 1. validateRequest()                                             │  │
│  │    ├─ Session exists & belongs to user? ✓                      │  │
│  │    ├─ Allocation exists & unlocked? ✓                          │  │
│  │    └─ Quantity > 0? ✓                                           │  │
│  │                                                                  │  │
│  │ 2. loadData()                                                    │  │
│  │    ├─ Validate: allocation.assetType == request.assetType      │  │
│  │    │            ("GOLD" == "GOLD" ✓)                           │  │
│  │    ├─ Load product by ID 3:                                    │  │
│  │    │  ├─ assetCode: "SJC_1D"                                  │  │
│  │    │  └─ assetName: "Vang SJC 1 chi"                          │  │
│  │    ├─ Load price for product 3:                                │  │
│  │    │  └─ buyPrice: 50000.00                                   │  │
│  │    └─ Validate: 10 * 50000 + existing ≤ recommended ✓         │  │
│  │                                                                  │  │
│  │ 3. processBusiness()                                            │  │
│  │    ├─ Create InvestmentAssetSelectionEntity:                   │  │
│  │    │  ├─ sessionId: 1                                          │  │
│  │    │  ├─ allocationId: 5                                       │  │
│  │    │  ├─ assetCode: "SJC_1D"                                  │  │
│  │    │  ├─ assetName: "Vang SJC 1 chi"                          │  │
│  │    │  ├─ categoryCode: "GOLD" ← assetType from request         │  │
│  │    │  ├─ productId: 3                                          │  │
│  │    │  ├─ quantity: 10                                          │  │
│  │    │  ├─ buyPrice: 50000.00                                   │  │
│  │    │  └─ investedAmount: 500000.00                             │  │
│  │    ├─ Update allocation:                                       │  │
│  │    │  ├─ status: ALLOCATION_IN_PROGRESS                        │  │
│  │    │  ├─ investedAmount: 500000.00                             │  │
│  │    │  └─ residualAmount: 1500000.00                            │  │
│  │    └─ Create event log: EVENT_ASSET_SELECTED                   │  │
│  │                                                                  │  │
│  │ 4. createResponse()                                             │  │
│  │    └─ Return SelectAssetResponse:                              │  │
│  │       ├─ selectionId: 100                                      │  │
│  │       ├─ allocationId: 5                                       │  │
│  │       ├─ assetCode: "SJC_1D"                                  │  │
│  │       ├─ assetType: "GOLD"                                     │  │
│  │       ├─ assetName: "Vang SJC 1 chi"                           │  │
│  │       ├─ quantity: 10                                          │  │
│  │       ├─ buyPrice: 50000.00                                   │  │
│  │       ├─ investedAmount: 500000.00                             │  │
│  │       ├─ residualAmount: 1500000.00                            │  │
│  │       └─ createdAt: 2026-06-10T10:35:00                        │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                              ▼                                          │
└──────────────────────────────┼──────────────────────────────────────────┘
                               │
                               │ Save to Database
                               │ (via Repository pattern)
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         PostgreSQL/MySQL Database                       │
│                                                                         │
│  investment_sessions (1 record)                                        │
│  ├─ id: 1                                                              │
│  ├─ user_id: "user-uuid-123"                                          │
│  └─ status: "PROCESSING"                                              │
│                                                                         │
│  investment_session_allocations (n records)                            │
│  ├─ id: 5                                                              │
│  ├─ session_id: 1                                                      │
│  ├─ asset_type: "GOLD"                                                │
│  ├─ recommended_amount: 2000000.00                                     │
│  ├─ invested_amount: 500000.00    ← updated                           │
│  ├─ residual_amount: 1500000.00   ← updated                           │
│  └─ status: "ALLOCATION_IN_PROGRESS"  ← updated                       │
│                                                                         │
│  investment_asset_selections (new record)                              │
│  ├─ id: 100                                                            │
│  ├─ session_id: 1                                                      │
│  ├─ allocation_id: 5                                                   │
│  ├─ asset_code: "SJC_1D"                                              │
│  ├─ asset_name: "Vang SJC 1 chi"                                      │
│  ├─ category_code: "GOLD"                                             │
│  ├─ product_id: 3                                                      │
│  ├─ quantity: 10.0000                                                 │
│  ├─ buy_price: 50000.000000                                           │
│  ├─ invested_amount: 500000.00                                        │
│  └─ created_at: 2026-06-10T10:35:00                                   │
│                                                                         │
│  asset_products (reference)                                            │
│  ├─ id: 3                                                              │
│  ├─ code: "SJC_1D"                                                    │
│  ├─ name: "Vang SJC 1 chi"                                            │
│  ├─ category_id: 2                                                     │
│  └─ platform_id: 4                                                     │
│                                                                         │
│  asset_categories (reference)                                          │
│  ├─ id: 2                                                              │
│  ├─ code: "GOLD"                                                       │
│  └─ name: "Vang"                                                       │
│                                                                         │
│  asset_prices (reference)                                              │
│  ├─ id: 3                                                              │
│  ├─ product_id: 3                                                      │
│  ├─ buy_price: 50000.000000                                           │
│  ├─ sell_price: 49800.000000                                          │
│  └─ is_latest: true                                                   │
└─────────────────────────────────────────────────────────────────────────┘
                               ▲
                               │
                               │ Response sent back:
                               │ {
                               │   data: {
                               │     selectionId: 100,
                               │     assetCode: "SJC_1D",
                               │     assetType: "GOLD",
                               │     assetName: "Vang SJC 1 chi",
                               │     investedAmount: 500000.00,
                               │     residualAmount: 1500000.00,
                               │     ...
                               │   }
                               │ }
                               │
┌──────────────────────────────┴──────────────────────────────────────────┐
│                         Frontend (cont'd)                               │
│                                                                         │
│  Investment Processing Screen (Updated)                                │
│  ├─ Allocation: Gold                                                   │
│  ├─ Recommended: 2,000,000 VND                                        │
│  ├─ Invested: 500,000 VND (Updated)                                  │
│  ├─ Residual: 1,500,000 VND (Updated)                                │
│  │                                                                    │
│  └─ Selected Assets:                                                  │
│     └─ [SJC_1D] Vang SJC 1 chi                                        │
│        Quantity: 10                                                    │
│        Unit Price: 50,000                                             │
│        Total: 500,000                                                 │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Entity Relationship Diagram

```
investment_sessions (1)
    │
    ├──┬─ investment_session_allocations (n)
    │  │      │
    │  │      ├─── asset_type: "GOLD" | "STOCK" | "ETF"
    │  │      ├─── recommended_amount: 2000000.00
    │  │      ├─── status: "NOT_STARTED" | "IN_PROGRESS" | "COMPLETED" | "LOCKED"
    │  │      │
    │  │      └──┬─ investment_asset_selections (n)
    │  │         │
    │  │         ├─── assetCode: "SJC_1D"
    │  │         ├─── assetType/categoryCode: "GOLD"
    │  │         ├─── assetName: "Vang SJC 1 chi"
    │  │         ├─── productId: 3  ◄─────┐
    │  │         ├─── quantity: 10         │
    │  │         ├─── buyPrice: 50000.00   │
    │  │         └─── investedAmount: 500k │
    │  │                                    │
    │  └────────────────────────────────────┼─── asset_products (n)
    │                                       │    ├─ id: 3
    │                                       │    ├─ code: "SJC_1D"
    │                                       │    ├─ name: "Vang SJC 1 chi"
    │                                       │    ├─ category_id: 2  ◄──┐
    │                                       │    └─ platform_id: 4    │
    │                                       │                         │
    │                                       └─────── asset_categories (1)
    │                                              ├─ id: 2
    │                                              ├─ code: "GOLD"
    │                                              └─ name: "Vang"
    │
    └─── user (Foreign Key: user_id)
         ├─ user_id: "uuid-123"
         ├─ email: "user@example.com"
         └─ status: "ACTIVE"
```

---

## Request-Response Flow Details

### Flow 1: Get Available Assets

```
GET /api/v1/assets?type=GOLD

┌─────────────────────────────────────────────────────────┐
│ Request Validation                                      │
├─────────────────────────────────────────────────────────┤
│ □ JWT Token Valid?                                      │
│ □ User Authenticated?                                   │
└─────────────────────────────────────────────────────────┘
                        ▼
┌─────────────────────────────────────────────────────────┐
│ GetAvailableAssetsService.execute("GOLD")               │
├─────────────────────────────────────────────────────────┤
│ 1. loadData(ctx)                                        │
│    ├─ Query: SELECT * FROM asset_prices                │
│    │         WHERE is_latest=true AND is_active=true    │
│    │         (4 records)                                │
│    │                                                    │
│    ├─ Query: SELECT * FROM asset_products              │
│    │         WHERE is_active=true                       │
│    │         (4 records → Map by ID)                   │
│    │                                                    │
│    └─ Query: SELECT * FROM asset_categories            │
│             WHERE is_active=true                        │
│             (4 records → Map by ID)                    │
│                                                        │
│ 2. processBusiness(ctx)                                │
│    Stream 4 assets:                                     │
│    ├─ Asset 1: product_id=1 (/1/)                      │
│    │  ├─ product=VNI, category=ETF                    │
│    │  └─ assetType="ETF" ≠ "GOLD" → FILTER OUT        │
│    │                                                   │
│    ├─ Asset 2: product_id=2 (/2/)                      │
│    │  ├─ product=VCI10, category=ETF                  │
│    │  └─ assetType="ETF" ≠ "GOLD" → FILTER OUT        │
│    │                                                   │
│    ├─ Asset 3: product_id=3 (/3/)                      │
│    │  ├─ product=SJC_1D, category=GOLD                │
│    │  └─ assetType="GOLD" == "GOLD" → KEEP ✓          │
│    │     Map to response: {                            │
│    │       productId: 3,                               │
│    │       assetCode: "SJC_1D",                        │
│    │       assetType: "GOLD",                          │
│    │       assetName: "Vang SJC 1 chi",                │
│    │       buyPrice: 50000.00,                         │
│    │       ...                                         │
│    │     }                                              │
│    │                                                   │
│    └─ Asset 4: product_id=4 (/4/)                      │
│       ├─ product=TCB, category=STOCK                   │
│       └─ assetType="STOCK" ≠ "GOLD" → FILTER OUT      │
│                                                        │
│ 3. createResponse(ctx)                                 │
│    ├─ responses.size() = 1                             │
│    └─ Return ApiResponse<List<AssetPriceResponse>>     │
└─────────────────────────────────────────────────────────┘
                        ▼
┌─────────────────────────────────────────────────────────┐
│ HTTP 200 OK                                             │
├─────────────────────────────────────────────────────────┤
│ {                                                       │
│   "data": [                                             │
│     {                                                   │
│       "id": 3,                                          │
│       "productId": 3,                                   │
│       "assetCode": "SJC_1D",                            │
│       "assetType": "GOLD",                              │
│       "assetName": "Vang SJC 1 chi",                    │
│       "buyPrice": 50000.00,                             │
│       "sellPrice": 49800.00,                            │
│       "isLatest": true,                                 │
│       "isActive": true                                  │
│     }                                                   │
│   ],                                                    │
│   "message": {                                          │
│     "code": "GET_ASSETS_INFO",                          │
│     "message": "Danh sach tai san khop yeu cau",        │
│     "service": "InvestmentWorkflow"                     │
│   }                                                     │
│ }                                                       │
└─────────────────────────────────────────────────────────┘
```

### Flow 2: Select Asset for Allocation

```
POST /api/v1/investment/1/allocation/5/select
{
  "productId": 3,
  "assetType": "GOLD",
  "quantity": 10
}

┌─────────────────────────────────────────────────────────┐
│ Request Validation                                      │
├─────────────────────────────────────────────────────────┤
│ □ JWT Token Valid? Yes                                  │
│ □ User Authenticated? Yes (user-uuid-123)               │
│ □ Body valid JSON? Yes                                  │
│ □ Required fields present? Yes                          │
└─────────────────────────────────────────────────────────┘
                        ▼
┌─────────────────────────────────────────────────────────┐
│ SelectAssetForAllocationService.execute(1, 5, request)  │
├─────────────────────────────────────────────────────────┤
│ 1. validateRequest(ctx)                                 │
│    □ Get current user: user-uuid-123                    │
│    □ Session 1 exists & user owns it?                  │
│      Query: SELECT * FROM investment_sessions           │
│              WHERE id=1 AND user_id='user-uuid-123'    │
│      Result: ✓ Found                                    │
│    □ Allocation 5 exists & belongs to session 1?       │
│      Query: SELECT * FROM investment_session_allocations│
│              WHERE id=5 AND session_id=1               │
│      Result: ✓ Found (status=NOT_STARTED, amount=2M)   │
│    □ Allocation not locked?                             │
│      allocationStatus ≠ LOCKED? ✓ TRUE                 │
│    □ Quantity > 0?                                      │
│      10 > 0? ✓ TRUE                                     │
│                                                        │
│ 2. loadData(ctx)                                        │
│    □ assetType validation:                              │
│      request.assetType="GOLD" ==                        │
│      allocation.assetType="GOLD"? ✓ TRUE               │
│                                                        │
│    □ Find product by productId=3:                       │
│      Query: SELECT * FROM asset_products               │
│              WHERE id=3 AND is_active=true             │
│      Result: product{code="SJC_1D", name="..."} ✓      │
│                                                        │
│    □ Get product's asset code & name:                  │
│      assetCode="SJC_1D"                                │
│      assetName="Vang SJC 1 chi"                        │
│                                                        │
│    □ Find latest price for product 3:                  │
│      Query: SELECT * FROM asset_prices                 │
│              WHERE product_id=3 AND is_latest=true    │
│      Result: assetPrice{buyPrice=50000.00} ✓           │
│                                                        │
│    □ Investment amount validation:                      │
│      invested = 10 * 50000.00 = 500,000.00            │
│      total = 0 + 500,000.00 = 500,000.00              │
│      recommended = 2,000,000.00                         │
│      500,000 ≤ 2,000,000? ✓ TRUE                      │
│                                                        │
│ 3. processBusiness(ctx)                                │
│    □ Create InvestmentAssetSelectionEntity:            │
│      {                                                  │
│        sessionId: 1,                                    │
│        allocationId: 5,                                 │
│        assetCode: "SJC_1D",                            │
│        assetName: "Vang SJC 1 chi",                    │
│        categoryCode: "GOLD",                            │
│        productId: 3,                                    │
│        quantity: 10,                                    │
│        buyPrice: 50000.00,                              │
│        investedAmount: 500000.00                        │
│      }                                                  │
│      → INSERT INTO investment_asset_selections...      │
│      → ID: 100 (auto-generated)                        │
│                                                        │
│    □ Update allocation:                                │
│      UPDATE investment_session_allocations              │
│      SET status="ALLOCATION_IN_PROGRESS",              │
│          invested_amount=500000.00,                     │
│          residual_amount=1500000.00                     │
│      WHERE id=5                                        │
│                                                        │
│    □ Create event:                                      │
│      INSERT INTO investment_session_events              │
│      {sessionId: 1, eventType: "ASSET_SELECTED", ...}  │
│                                                        │
│ 4. createResponse(ctx)                                 │
│    Return SelectAssetResponse {                         │
│      selectionId: 100,                                 │
│      allocationId: 5,                                  │
│      assetCode: "SJC_1D",                              │
│      assetType: "GOLD",                                │
│      assetName: "Vang SJC 1 chi",                      │
│      quantity: 10,                                      │
│      buyPrice: 50000.00,                               │
│      investedAmount: 500000.00,                        │
│      residualAmount: 1500000.00,                       │
│      createdAt: 2026-06-10T10:35:00                    │
│    }                                                    │
└─────────────────────────────────────────────────────────┘
                        ▼
┌─────────────────────────────────────────────────────────┐
│ HTTP 200 OK                                             │
├─────────────────────────────────────────────────────────┤
│ {                                                       │
│   "data": {                                             │
│     "selectionId": 100,                                 │
│     "allocationId": 5,                                  │
│     "assetCode": "SJC_1D",                              │
│     "assetType": "GOLD",                                │
│     "assetName": "Vang SJC 1 chi",                      │
│     "quantity": 10,                                     │
│     "buyPrice": 50000.00,                               │
│     "investedAmount": 500000.00,                        │
│     "residualAmount": 1500000.00,                       │
│     "createdAt": "2026-06-10T10:35:00"                  │
│   }                                                     │
│ }                                                       │
└─────────────────────────────────────────────────────────┘
```


