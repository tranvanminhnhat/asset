# Refactoring: AssetPrice Entity - buyPrice/sellPrice → currentPrice

## 📝 Summary
Refactored `AssetPriceEntity` and related DTOs/Services to replace `buyPrice` and `sellPrice` with a single semantic `currentPrice` field. This improves code clarity and removes unused `sellPrice` field.

---

## ✅ Changes Made

### 1. **Entity Layer**
- **File**: `AssetPriceEntity.java`
- **Changes**:
  - ❌ Removed: `private BigDecimal buyPrice;`
  - ❌ Removed: `private BigDecimal sellPrice;`
  - ✅ Added: `private BigDecimal currentPrice;` (maps to `current_price` column)
- **Impact**: Database column renamed from `buy_price` to `current_price`

### 2. **DTO Request Layer**
- **Files**:
  - `CreateAssetPriceRequest.java`
  - `UpdateAssetPriceRequest.java`
- **Changes**:
  - ❌ Removed: `private BigDecimal buyPrice;`
  - ❌ Removed: `private BigDecimal sellPrice;`
  - ✅ Added: `private BigDecimal currentPrice;`
- **Validation Messages**:
  - "Buy price is required" → "Current price is required"

### 3. **DTO Response Layer**
- **File**: `AssetPriceResponse.java` (admin module)
- **Changes**:
  - ❌ Removed: `private BigDecimal buyPrice;`
  - ❌ Removed: `private BigDecimal sellPrice;`
  - ✅ Added: `private BigDecimal currentPrice;`

### 4. **Service Layer - Creation**
- **File**: `CreateAssetPriceService.java`
- **Changes**:
  - Line 38: `ctx.request.getBuyPrice()` → `ctx.request.getCurrentPrice()`
  - Line 58: `.buyPrice(...)` → `.currentPrice(...)`
  - Line 59: ❌ Removed `.sellPrice(...)` setter
  - Line 64: Log message updated with `getCurrentPrice()`
  - Line 71: `.buyPrice(...)` → `.currentPrice(...)`
  - Line 72: ❌ Removed `.sellPrice(...)` setter

### 5. **Service Layer - Update**
- **File**: `UpdateAssetPriceService.java`
- **Changes**:
  - Line 42: `ctx.request.getBuyPrice()` → `ctx.request.getCurrentPrice()`
  - Line 57: `.setBuyPrice(...)` → `.setCurrentPrice(...)`
  - Line 58: ❌ Removed `.setSellPrice(...)` setter
  - Line 63: Log message updated with `getCurrentPrice()`
  - Line 70: `.buyPrice(...)` → `.currentPrice(...)`
  - Line 71: ❌ Removed `.sellPrice(...)` setter

### 6. **Service Layer - Read**
- **File**: `AdminGetAssetPriceService.java`
- **Changes**:
  - Line 35: `.getBuyPrice()` → `.getCurrentPrice()`
  - Line 36: ❌ Removed `.getSellPrice()` mapping
  - Line 70: `.buyPrice(...)` → `.currentPrice(...)`
  - Line 71: ❌ Removed `.sellPrice(...)` setter

### 7. **Dashboard Service**
- **File**: `GetCurrentUserDashboardService.java`
- **Changes**:
  - Line 175: `price.getBuyPrice()` → `price.getCurrentPrice()`
  - Line 176: Same call returns price from `currentPrice` field
- **Impact**: Dashboard profit/loss calculation continues to work with no logic change

---

## 🔄 Database Migration Required

```sql
-- Rename column
ALTER TABLE asset_prices 
RENAME COLUMN buy_price TO current_price;

-- Drop unused column
ALTER TABLE asset_prices 
DROP COLUMN sell_price;

-- Verify
DESCRIBE asset_prices;
-- Should show: current_price, NOT buy_price or sell_price
```

---

## 📊 Changes Summary Table

| Item | Before | After |
|------|--------|-------|
| Entity Field | `buyPrice` + `sellPrice` | `currentPrice` |
| Column Name | `buy_price` + `sell_price` | `current_price` |
| Request DTO | `buyPrice` + `sellPrice` | `currentPrice` |
| Response DTO | `buyPrice` + `sellPrice` | `currentPrice` |
| Getter | `.getBuyPrice()` | `.getCurrentPrice()` |
| Setter | `.setBuyPrice()` | `.setCurrentPrice()` |
| Files Modified | 8 files | All updated consistently |
| Logic Change | No logic change | ✅ Preserved |
| Unused Field | `sellPrice` (never used) | Removed |

---

## ✨ Benefits

1. **Semantic Clarity**: `currentPrice` is unambiguous - it's the current market price
2. **No Confusion**: Previously `buyPrice` in AssetPrice was confusing (buy vs current?)
3. **Reduced Schema**: Eliminates unused `sellPrice` column
4. **Consistency**: All files updated consistently with no orphaned references
5. **Performance**: One less column to store/retrieve

---

## 🔍 Verification

### Files Modified (8 total):
1. ✅ `AssetPriceEntity.java` - Entity
2. ✅ `CreateAssetPriceRequest.java` - Request DTO
3. ✅ `UpdateAssetPriceRequest.java` - Request DTO
4. ✅ `AssetPriceResponse.java` - Response DTO (admin)
5. ✅ `CreateAssetPriceService.java` - Service
6. ✅ `UpdateAssetPriceService.java` - Service
7. ✅ `AdminGetAssetPriceService.java` - Service
8. ✅ `GetCurrentUserDashboardService.java` - Service (Dashboard)

### Grep Results:
- ✅ No references to `.getBuyPrice()` remain
- ✅ No references to `.setSellPrice()` remain
- ✅ No references to `.getSellPrice()` remain
- ✅ All migrated to `.getCurrentPrice()` / `.setCurrentPrice()`

### Build Status:
- Pending: `./gradlew clean build` (in progress at time of refactoring)

---

## 🎯 Next Steps

1. **Wait for Build**: Verify `./gradlew clean build` completes successfully
2. **Database Migration**: Run SQL migration to rename column + drop unused column
3. **Test Admin APIs**: 
   - POST `/api/v1/admin/asset-prices` (create)
   - GET `/api/v1/admin/asset-prices/{id}` (read)
   - PUT `/api/v1/admin/asset-prices` (update)
   - DELETE `/api/v1/admin/asset-prices/{id}` (delete)
4. **Test Dashboard**: GET `/api/v1/dashboard` (verify profit calculation works)
5. **Update Frontend** (if applicable): 
   - TypeScript types: `buyPrice` → `currentPrice`, remove `sellPrice`

---

## 📝 Notes

- **Logic Preservation**: All business logic remains unchanged
- **Backward Compatibility**: Database schema changed, but application logic intact
- **No Performance Impact**: Removing one column slightly improves performance
- **Clean Code**: More semantic naming improves maintainability

---

**Status**: ✅ REFACTORING COMPLETE (Pending Build Verification)

**Date**: 2026-07-01

**Changes**: All 8 files updated consistently following the `buyPrice → currentPrice` and `sellPrice` removal pattern

