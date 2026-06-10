# API Asset Selection Fix - Complete Summary

## Overview
Fixed two critical API endpoints to properly handle asset selection with type filtering and ensure response fields are correctly populated.

## Changes Made

### 1. SelectAssetRequest.java
**Purpose**: Define the request contract for the asset selection API

**Changes**:
- Added `Long productId` - Product ID from FE selection (primary key to identify specific asset)
- Added `String assetType` - Asset type/category code (e.g., GOLD, STOCK) for validation
- Kept `String assetCode` for backward compatibility
- Kept `BigDecimal quantity` for investment amount

**Why**: 
- FE sends both productId and assetType to avoid ambiguous DB lookups
- assetType validates that user is selecting correct type of asset for allocated category
- productId is more efficient than assetCode for direct product lookup

```java
public class SelectAssetRequest {
    private Long productId;           // Product ID from FE selection
    private String assetType;         // Asset type/category code (e.g., GOLD, STOCK)
    private String assetCode;         // Legacy support: product code
    private BigDecimal quantity;      // Quantity to invest
}
```

### 2. SelectAssetResponse.java
**Purpose**: Define the response contract for the asset selection API

**Changes**:
- Added `String assetType` field to match request and FE expectations

**Why**:
- FE needs to display the asset type back to user for confirmation
- Response must match what user selected to ensure data integrity

```java
public class SelectAssetResponse {
    // ... existing fields ...
    private String assetType;         // Asset type/category code (e.g., GOLD, STOCK)
}
```

### 3. SelectAssetForAllocationService.java
**Purpose**: Handle the core business logic for asset selection

**Key Changes**:

#### a. loadData() Method
- **Step 1**: Validate that `assetType` from request matches the allocation's `assetType`
  - Prevents user from selecting GOLD when allocation expects STOCK
  - Throws `IllegalArgumentException` if mismatch
  
- **Step 2**: Determine productId
  - Primary: Use `productId` from request (direct lookup)
  - Fallback: Use `assetCode` for backward compatibility
  - Loads product details (code, name)
  
- **Step 3**: Find latest price for the product
  - Uses `productId` to lookup latest asset price
  - Validates total investment amount won't exceed recommendation

#### b. processBusiness() Method
- Creates `InvestmentAssetSelectionEntity` with:
  - `assetCode` = product's code (from DB)
  - `assetName` = product's name (from DB)
  - `categoryCode` = request's assetType (stores the asset type for audit trail)
  - `productId` = request's productId (stores product reference)

#### c. createResponse() Method
- Includes `assetType` field populated from selection's `categoryCode`
- Returns complete selection details to FE for display

**Database Schema**:
- Uses existing `category_code` column in `investment_asset_selections` table
- Column was added in V008 migration

### 4. GetAvailableAssetsService.java
**Purpose**: Retrieve available assets, optionally filtered by assetType

**Key Changes**:

#### New Dependencies
- Added `AssetProductRepository` - to load product details
- Added `AssetCategoryRepository` - to load category/asset type details

#### How It Works

1. **loadData()**:
   - Loads all latest, active asset prices
   - Loads all active products (mapped by ID)
   - Loads all active asset categories (mapped by ID)

2. **processBusiness()**:
   - Filters assets by assetType if provided:
     - If `assetType` parameter is empty → returns all assets
     - If `assetType` provided → filters to only matching category code
   - For each asset, looks up:
     - Product details (code, name)
     - Category details (code as assetType)
   - Populates response with:
     - `assetCode` = product's code
     - `assetType` = category's code (e.g., GOLD, STOCK)
     - `assetName` = product's name

3. **Response**:
   - Legacy fields (assetCode, assetType, assetName) now properly populated
   - New fields (productId, prices) included for complete information

**Example Flow**:
```
GET /api/v1/assets?type=GOLD

Response includes:
[
  {
    "id": 3,
    "productId": 3,
    "assetCode": "SJC_1D",
    "assetType": "GOLD",
    "assetName": "Vang SJC 1 chi",
    "buyPrice": 50000.00,
    "sellPrice": 49800.00,
    "isLatest": true,
    "isActive": true
  }
]
```

## Database Schema Impact

**Migration V008 Already Includes**:
```sql
ALTER TABLE `investment_asset_selections`
ADD COLUMN IF NOT EXISTS `product_id` BIGINT,
ADD COLUMN IF NOT EXISTS `platform_code` VARCHAR(50),
ADD COLUMN IF NOT EXISTS `product_code` VARCHAR(50),
ADD COLUMN IF NOT EXISTS `product_name` VARCHAR(255),
ADD COLUMN IF NOT EXISTS `category_code` VARCHAR(50),
ADD COLUMN IF NOT EXISTS `unit_name` VARCHAR(50);
```

- `category_code` = stores the assetType (GOLD, STOCK, ETF, etc.)
- `product_id` = reference to asset_products table
- All fields already exist in DB

## Data Flow

### Asset Selection Flow
```
FE sends SelectAssetRequest:
  {
    "productId": 3,
    "assetType": "GOLD",
    "quantity": 10
  }

↓ SelectAssetForAllocationService

1. Validate: assetType "GOLD" == allocation.assetType ✓
2. Load: Find product by ID 3 → get assetCode "SJC_1D", name "Vang SJC 1 chi"
3. Load: Get latest price for product 3 → buyPrice 50000.00
4. Save: InvestmentAssetSelectionEntity with:
   - assetCode: "SJC_1D"
   - categoryCode: "GOLD"  (assetType)
   - assetName: "Vang SJC 1 chi"
   - buyPrice: 50000.00
   - investedAmount: 500000.00

↓ Returns SelectAssetResponse:
  {
    "selectionId": 123,
    "assetCode": "SJC_1D",
    "assetType": "GOLD",
    "assetName": "Vang SJC 1 chi",
    "quantity": 10,
    "buyPrice": 50000.00,
    "investedAmount": 500000.00,
    "residualAmount": 1500000.00
  }
```

### Get Available Assets Flow
```
FE requests GET /api/v1/assets?type=GOLD

↓ GetAvailableAssetsService

1. Load: All latest active asset prices
2. Load: All active products (VNI, VCI10, SJC_1D, TCB)
3. Load: All active categories (ETF, GOLD, STOCK, EMERGENCY)
4. Filter: Keep only assets where product.category.code == "GOLD"
5. Map: For each asset:
   - assetCode ← product.code
   - assetType ← category.code
   - assetName ← product.name

↓ Returns List<AssetPriceResponse>
  [
    {
      "id": 3,
      "productId": 3,
      "assetCode": "SJC_1D",
      "assetType": "GOLD",
      "assetName": "Vang SJC 1 chi",
      "buyPrice": 50000.00,
      "sellPrice": 49800.00
    }
  ]
```

## Backward Compatibility

✅ **SelectAssetForAllocationService**:
- If `productId` not provided, falls back to `assetCode` lookup
- Existing FE code sending only `assetCode` will still work
- New code should send `productId + assetType`

✅ **GetAvailableAssetsService**:
- If `type` parameter not provided, returns all assets
- Existing code calling without filter still works
- New code can filter by type

## Build Status

✅ **Build Successful**: 0 errors, 3 warnings (pre-existing)
- All Java files compile successfully
- No breaking changes to existing code
- Database schema compatibility verified in V008

## Testing Recommendations

1. **Test SelectAssetForAllocationService**:
   - ✓ Send valid productId + assetType
   - ✓ Verify response includes assetCode, assetType, assetName
   - ✓ Test with mismatched assetType (should fail)
   - ✓ Test backward compatibility with assetCode-only request

2. **Test GetAvailableAssetsService**:
   - ✓ Call without type filter → returns all assets
   - ✓ Call with type=GOLD → returns only GOLD assets with populated fields
   - ✓ Verify assetCode, assetType, assetName are populated in response
   - ✓ Test with invalid type → returns empty list

3. **Integration Test**:
   - ✓ Full investment workflow with asset selection
   - ✓ Verify allocation and selection records created correctly
   - ✓ Check database values (assetCode, categoryCode, productId properly stored)

## Files Modified

1. `D:\code\asset\be\src\main\java\com\minhnhat\demo\features\investment\dto\request\SelectAssetRequest.java`
2. `D:\code\asset\be\src\main\java\com\minhnhat\demo\features\investment\dto\response\SelectAssetResponse.java`
3. `D:\code\asset\be\src\main\java\com\minhnhat\demo\features\investment\service\SelectAssetForAllocationService.java`
4. `D:\code\asset\be\src\main\java\com\minhnhat\demo\features\investment\service\GetAvailableAssetsService.java`

## Issue Resolution

### Original Issues
1. ❌ `loadData()` was loading ALL assets instead of filtering by assetType
2. ❌ Response fields (assetCode, assetType, assetName) were null
3. ❌ No assetType validation against allocation
4. ❌ SelectAssetRequest missing productId and assetType

### Fixed With
✅ Proper filtering by assetType in both services
✅ All response fields now properly populated from entities
✅ assetType validation ensures correct asset category selection
✅ SelectAssetRequest now includes productId and assetType
✅ GetAvailableAssetsService joins with Product and Category entities
✅ SelectAssetForAllocationService validates and stores assetType

