# Get Products by Asset Type API - Implementation Guide

## ✅ API Endpoint Created

**Endpoint**: `GET /api/v1/products?type=ETF`

## Overview

This API returns a list of products (assets) available for a specific asset type, including their latest prices. It replaces the need for mock data in the FE.

## Endpoint Details

### Request

```
GET /api/v1/products?type={assetType}
Authorization: Bearer {JWT_TOKEN}
```

**Parameters**:
- `type` (query): Required. Asset type code (ETF, GOLD, STOCK, EMERGENCY)

### Response

```json
{
  "data": [
    {
      "productId": 1,
      "assetCode": "VNI",
      "assetType": "ETF",
      "assetName": "Quy VN-Index ETF",
      "platformName": "VNDirect",
      "buyPrice": 100.50,
      "sellPrice": 100.00
    },
    {
      "productId": 2,
      "assetCode": "VCI10",
      "assetType": "ETF",
      "assetName": "Quy VCI10 ETF",
      "platformName": "VNDirect",
      "buyPrice": 250.75,
      "sellPrice": 250.00
    }
  ],
  "message": {
    "code": "GET_ASSETS_INFO",
    "message": "Danh sach tai san khop yeu cau",
    "service": "InvestmentWorkflow"
  }
}
```

## Response Fields

| Field | Type | Description |
|-------|------|-------------|
| `productId` | Long | Unique product ID (use this for asset selection) |
| `assetCode` | String | Product code (e.g., VNI, SJC_1D, TCB) |
| `assetType` | String | Asset type/category code (ETF, GOLD, STOCK, EMERGENCY) |
| `assetName` | String | Product name in Vietnamese |
| `platformName` | String | Trading platform name |
| `buyPrice` | BigDecimal | Current buy/bid price |
| `sellPrice` | BigDecimal | Current sell/ask price |

## Example Requests

### Get ETF Products
```bash
curl -X GET "http://localhost:8080/api/v1/products?type=ETF" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json"
```

**Response** (2 ETF products):
```json
{
  "data": [
    {
      "productId": 1,
      "assetCode": "VNI",
      "assetType": "ETF",
      "assetName": "Quy VN-Index ETF",
      "platformName": "VNDirect",
      "buyPrice": 100.50,
      "sellPrice": 100.00
    },
    {
      "productId": 2,
      "assetCode": "VCI10",
      "assetType": "ETF",
      "assetName": "Quy VCI10 ETF",
      "platformName": "VNDirect",
      "buyPrice": 250.75,
      "sellPrice": 250.00
    }
  ]
}
```

### Get GOLD Products
```bash
curl -X GET "http://localhost:8080/api/v1/products?type=GOLD" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json"
```

**Response** (1 GOLD product):
```json
{
  "data": [
    {
      "productId": 3,
      "assetCode": "SJC_1D",
      "assetType": "GOLD",
      "assetName": "Vang SJC 1 chi",
      "platformName": "San Vang",
      "buyPrice": 50000.00,
      "sellPrice": 49800.00
    }
  ]
}
```

### Get STOCK Products
```bash
curl -X GET "http://localhost:8080/api/v1/products?type=STOCK" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json"
```

**Response** (1 STOCK product):
```json
{
  "data": [
    {
      "productId": 4,
      "assetCode": "TCB",
      "assetType": "STOCK",
      "assetName": "Techcombank Stock",
      "platformName": "VNDirect",
      "buyPrice": 150.50,
      "sellPrice": 150.00
    }
  ]
}
```

## Error Responses

### Missing Asset Type Parameter
```
GET /api/v1/products
```

**Response** (Status: 400 Bad Request):
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

### Invalid Asset Type
```
GET /api/v1/products?type=INVALID
```

**Response** (Status: 400 Bad Request):
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

## Frontend Integration

### Initial Implementation (Replace Mock Data)

**Before** (using MOCK_ASSETS):
```typescript
const assets: AssetPrice[] = apiAssets.length > 0
  ? apiAssets
  : (MOCK_ASSETS[assetType] ?? []).map(...);
```

**After** (using real API):
```typescript
// Add to investment-processing.api.ts
export const investmentProcessingApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getProductsByType: builder.query<ProductListApiResponse, string>({
      query: (type) => ({
        url: `/api/v1/products?type=${type}`,
        method: 'GET',
      }),
    }),
    // ... other endpoints
  }),
});
```

### Usage in Component

```typescript
// In InvestmentProcessingPage.tsx
const {
  data: productsResponse,
  isLoading,
  isError,
} = useGetProductsByTypeQuery(assetType);

const assets: AssetPrice[] = productsResponse?.data ?? [];

// If no products in DB for this type, fall back to mock
const finalAssets = assets.length > 0 
  ? assets 
  : (MOCK_ASSETS[assetType] ?? []);
```

## Data Flow

```
Frontend
  ↓
GET /api/v1/products?type=ETF
  ↓
Backend GetProductsByAssetTypeController
  ↓
GetProductsByAssetTypeService
  1. Validate asset type exists (category)
  2. Load all active products
  3. Load all active platforms
  4. Load all active prices
  5. Filter products by category
  6. Map to response with platform names
  ↓
Database Queries:
  - asset_categories (find by code: "ETF")
  - asset_products (all active)
  - asset_platforms (all active)
  - asset_prices (all latest active)
  ↓
Response with populated ProductWithPriceResponse[]
  ↓
Frontend receives list of products with prices
```

## Database Integration

The API uses the following database tables:

1. **asset_categories**: ETF, GOLD, STOCK, EMERGENCY
2. **asset_products**: product codes and names (joined with category)
3. **asset_platforms**: platform names
4. **asset_prices**: latest prices for products

## Test Data Available

The database contains sample data (from V008 migration):

```sql
-- ETF Products
- VNI (Quy VN-Index ETF) - buyPrice: 100.50
- VCI10 (Quy VCI10 ETF) - buyPrice: 250.75

-- GOLD Products
- SJC_1D (Vang SJC 1 chi) - buyPrice: 50000.00

-- STOCK Products
- TCB (Techcombank Stock) - buyPrice: 150.50
```

## Migration Steps for FE

1. **Create API type** in `investment-processing.type.ts`:
```typescript
export interface ProductWithPrice {
  productId: number;
  assetCode: string;
  assetType: string;
  assetName: string;
  platformName: string;
  buyPrice: number;
  sellPrice: number;
}

export interface ProductListApiResponse {
  data: ProductWithPrice[];
  message?: {
    code: string;
    message: string;
    service: string;
  };
}
```

2. **Update API hook** in `investment-processing.api.ts`:
```typescript
getProductsByType: builder.query<ProductListApiResponse, string>({
  query: (type) => ({
    url: `/api/v1/products?type=${type}`,
    method: 'GET',
  }),
}),
```

3. **Update hook** in `useInvestmentProcessing.ts`:
```typescript
const {
  data: productsResponse,
  isLoading: isLoadingProducts,
  isError: isProductsError,
} = useGetProductsByTypeQuery(assetType);

const assets = productsResponse?.data?.filter((a) => a.isActive) ?? [];
```

4. **Update component** in `InvestmentProcessingPage.tsx`:
```typescript
const {
  assets: apiAssets,
  // ...
} = useInvestmentProcessing({ sessionId, allocationId, assetType });

// Use real data or fall back to mock if empty
const assets: AssetPrice[] = apiAssets.length > 0
  ? apiAssets
  : (MOCK_ASSETS[assetType] ?? []);
```

## Comparison with `/api/v1/assets`

| Feature | `/api/v1/products` | `/api/v1/assets` |
|---------|-------------------|------------------|
| Purpose | Product listing | Price listing |
| Response | ProductWithPriceResponse[] | AssetPriceResponse[] |
| Fields | productId, assetCode, assetType, assetName, platformName, prices | id, productId, assetCode, assetType, assetName, prices, priceDate, isLatest, isActive |
| Use Case | Display available products | Display asset prices with metadata |

Both endpoints return the same underlying data, but `/api/v1/products` is optimized for FE listing while `/api/v1/assets` includes more metadata (price date, latest flag, active flag).

## Verification

✅ **API Created**: `/api/v1/products` endpoint
✅ **Service**: `GetProductsByAssetTypeService` 
✅ **Controller**: `GetProductsByAssetTypeController`
✅ **Response Model**: `ProductWithPriceResponse`
✅ **Build Status**: SUCCESS - 0 errors
✅ **Database**: Test data available in V008 migration

## Ready to Use

The API is now deployed and ready for FE integration. FE can start calling:

```
GET /api/v1/products?type=ETF
GET /api/v1/products?type=GOLD
GET /api/v1/products?type=STOCK
GET /api/v1/products?type=EMERGENCY
```

Instead of using mock data in `MOCK_ASSETS`.

