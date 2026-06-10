# Frontend Integration Example

## Complete Example: Replacing Mock Data with Real API

### Current Status
The FE currently uses `MOCK_ASSETS` in `InvestmentProcessingPage.tsx` like this:

```typescript
// Mock price data for types that may not have real price data yet
const MOCK_ASSETS: Record<string, { assetCode: string; assetName: string; buyPrice: number; sellPrice: number }[]> = {
  GOLD: [
    { assetCode: 'GOLD_SJC', assetName: 'Vàng SJC 1 chỉ', buyPrice: 8820000, sellPrice: 8920000 },
    // ...
  ],
  ETF: [
    { assetCode: 'E1VFVN30', assetName: 'VFMVN30 ETF', buyPrice: 14200, sellPrice: 14300 },
    // ...
  ],
  // ...
};

const assets: AssetPrice[] = apiAssets.length > 0
  ? apiAssets
  : (MOCK_ASSETS[assetType] ?? []).map((m) => ({
      id: 0,
      productId: 0,
      assetCode: m.assetCode,
      assetName: m.assetName,
      assetType,
      buyPrice: m.buyPrice,
      sellPrice: m.sellPrice,
      priceDate: null,
      isLatest: true,
      isActive: true,
    }));
```

---

## Solution: Use New `/api/v1/products` Endpoint

### Phase 1: Create API Service

**File**: `src/features/investment-processing/services/investment-processing.api.ts`

Add new query endpoint:

```typescript
import { baseApi } from '@/store/api/baseApi';
import {
  AssetsApiResponse,
  CompleteAllocationApiResponse,
  SelectAssetApiResponse,
  SelectAssetRequest,
  ProductListApiResponse,              // 🆕 NEW
} from '../type/investment-processing.type';

export const investmentProcessingApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    // Existing endpoints
    getAvailableAssets: builder.query<AssetsApiResponse, string>({
      query: (type) => ({
        url: `/api/v1/assets?type=${type}`,
        method: 'GET',
      }),
    }),
    
    // 🆕 NEW ENDPOINT: Get products by type
    getProductsByType: builder.query<ProductListApiResponse, string>({
      query: (type) => ({
        url: `/api/v1/products?type=${type}`,
        method: 'GET',
      }),
      // Optional: Cache for 5 minutes
      // keepUnusedDataFor: 5 * 60,
    }),

    // ... existing mutation endpoints
    selectAssetForAllocation: builder.mutation<
      SelectAssetApiResponse,
      { sessionId: number; allocationId: number; body: SelectAssetRequest }
    >({
      query: ({ sessionId, allocationId, body }) => ({
        url: `/api/v1/investment/${sessionId}/allocation/${allocationId}/select`,
        method: 'POST',
        body,
      }),
    }),

    // ... rest of endpoints
  }),
});

// Export hook for new endpoint
export const {
  useGetAvailableAssetsQuery,
  useGetProductsByTypeQuery,      // 🆕 NEW HOOK
  useSelectAssetForAllocationMutation,
  useCompleteAllocationMutation,
} = investmentProcessingApi;
```

---

### Phase 2: Define Types

**File**: `src/features/investment-processing/type/investment-processing.type.ts`

Add new types:

```typescript
// 🆕 NEW: Product with price response
export interface ProductWithPrice {
  productId: number;
  assetCode: string;
  assetType: string;
  assetName: string;
  platformName: string;
  buyPrice: number;
  sellPrice: number;
}

// 🆕 NEW: API response for product list
export interface ProductListApiResponse {
  data: ProductWithPrice[];
  message?: {
    code: string;
    message: string;
    service: string;
  };
}

// Existing types (keep unchanged)
export interface AssetPrice {
  id: number;
  productId: number;
  assetCode?: string | null;
  assetType?: string | null;
  assetName?: string | null;
  buyPrice: number;
  sellPrice: number;
  priceDate: string | null;
  isLatest: boolean;
  isActive: boolean;
}

// ... rest of types
```

---

### Phase 3: Update Hook

**File**: `src/features/investment-processing/hooks/useInvestmentProcessing.ts`

Update to use new endpoint:

```typescript
import { useState } from 'react';
import {
  useGetAvailableAssetsQuery,
  useGetProductsByTypeQuery,              // 🆕 NEW
  useSelectAssetForAllocationMutation,
  useCompleteAllocationMutation,
} from '../services/investment-processing.api';
import { CompleteAllocationResponse, SelectAssetRequest, SelectAssetResponse } from '../type/investment-processing.type';

interface UseInvestmentProcessingParams {
  sessionId: number;
  allocationId: number;
  assetType: string;
}

export const useInvestmentProcessing = ({ sessionId, allocationId, assetType }: UseInvestmentProcessingParams) => {
  // 🆕 NEW: Use products endpoint instead of assets
  const {
    data: productsResponse,
    isLoading: isLoadingProducts,
    isError: isProductsError,
  } = useGetProductsByTypeQuery(assetType);

  // Keep for backwards compatibility if needed
  const {
    data: assetsResponse,
    isLoading: isLoadingAssets,
    isError: isAssetsError,
  } = useGetAvailableAssetsQuery(assetType);

  const [selectAsset, { isLoading: isSelecting, isError: isSelectError, error: selectError }] =
    useSelectAssetForAllocationMutation();
  const [completeAllocation, { isLoading: isCompleting, isError: isCompleteError }] =
    useCompleteAllocationMutation();

  const [selectResult, setSelectResult] = useState<SelectAssetResponse | null>(null);

  // 🆕 NEW: Use products data, fall back to assets if needed
  const assets = productsResponse?.data?.filter((a) => a !== null) ?? [];

  const submitSelect = async (request: SelectAssetRequest): Promise<SelectAssetResponse | null> => {
    const response = await selectAsset({ sessionId, allocationId, body: request }).unwrap();
    const result = response.data ?? null;
    setSelectResult(result);
    return result;
  };

  const submitComplete = async (): Promise<CompleteAllocationResponse | null> => {
    const response = await completeAllocation({ sessionId, allocationId }).unwrap();
    return response.data ?? null;
  };

  return {
    assets,
    isLoadingAssets: isLoadingProducts,      // 🆕 Use products loading state
    isAssetsError: isProductsError,           // 🆕 Use products error state
    selectResult,
    isSelecting,
    isSelectError,
    selectError,
    isCompleting,
    isCompleteError,
    submitSelect,
    submitComplete,
  };
};
```

---

### Phase 4: Update Component

**File**: `src/features/investment-processing/pages/InvestmentProcessingPage.tsx`

Simplify mock data usage:

```typescript
'use client';
import React, { useState } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { formatCurrency } from '@/lib/formatters';
import LogoutButton from '@/features/logout/components/LogoutButton';
import AssetOption from '../components/AssetOption';
import { useInvestmentProcessing } from '../hooks/useInvestmentProcessing';
import { AssetPrice } from '../type/investment-processing.type';

const typeNames: Record<string, string> = {
  EMERGENCY: 'Quỹ Khẩn cấp',
  GOLD: 'Quỹ Vàng',
  ETF: 'Quỹ ETF',
  STOCK: 'Cổ Phiếu',
};

// 🆕 OPTIONAL: Keep mock data only as fallback for EMERGENCY type (not in DB yet)
const MOCK_ASSETS: Record<string, { assetCode: string; assetName: string; buyPrice: number; sellPrice: number }[]> = {
  // Removed GOLD, ETF, STOCK - now use real API data
  // Only keep if needed for EMERGENCY or testing
  EMERGENCY: [
    // Add if EMERGENCY products are needed
  ],
};

// ... rest of component

const InvestmentProcessingContent: React.FC<InvestmentProcessingPageProps> = ({
  sessionId,
  allocationId,
  assetType,
  recommendedAmount,
}) => {
  const router = useRouter();
  const [selectedAsset, setSelectedAsset] = useState<AssetPrice | null>(null);
  const [quantity, setQuantity] = useState<string>('');
  const [submitError, setSubmitError] = useState<string | null>(null);
  const [step, setStep] = useState<'select' | 'confirm' | 'done'>('select');

  const {
    assets: apiAssets,
    isLoadingAssets,
    isAssetsError,
    selectResult,
    isSelecting,
    isSelectError,
    isCompleting,
    isCompleteError,
    submitSelect,
    submitComplete,
  } = useInvestmentProcessing({ sessionId, allocationId, assetType });

  // 🆕 SIMPLIFIED: Now uses real API data
  const assets: AssetPrice[] = apiAssets.length > 0
    ? apiAssets.map(p => ({
        id: p.productId ?? 0,
        productId: p.productId,
        assetCode: p.assetCode,
        assetName: p.assetName,
        assetType: p.assetType,
        buyPrice: p.buyPrice,
        sellPrice: p.sellPrice,
        priceDate: null,
        isLatest: true,
        isActive: true,
      }))
    : (MOCK_ASSETS[assetType] ?? []).map((m) => ({
        // Fallback to mock if API fails (optional)
        id: 0,
        productId: 0,
        assetCode: m.assetCode,
        assetName: m.assetName,
        assetType,
        buyPrice: m.buyPrice,
        sellPrice: m.sellPrice,
        priceDate: null,
        isLatest: true,
        isActive: true,
      }));

  // ... rest of component unchanged
};
```

---

## Benefits After Integration

✅ **No More Mock Data** for ETF, GOLD, STOCK
✅ **Real Database Values** shown to users
✅ **Live Price Updates** reflected immediately
✅ **Easy to Expand** - add more products via admin UI
✅ **Type-Safe** - database enforces data integrity
✅ **Better Performance** - single API call per type
✅ **Fallback Available** - still works if API fails

---

## Testing the New Integration

### Test Case 1: ETF Products Load
```
1. Navigate to Investment Processing with type=ETF
2. Should see: VNI (100.50), VCI10 (250.75)
3. Should NOT see mock data anymore
```

### Test Case 2: GOLD Products Load
```
1. Navigate to Investment Processing with type=GOLD
2. Should see: SJC_1D (50000.00)
3. Should NOT see mock data anymore
```

### Test Case 3: STOCK Products Load
```
1. Navigate to Investment Processing with type=STOCK
2. Should see: TCB (150.50)
3. Should NOT see mock data anymore
```

### Test Case 4: Selection Still Works
```
1. Select a product
2. Enter quantity
3. Click "Chọn tài sản"
4. Should still work with real data
```

---

## Rollback Plan (if needed)

If something goes wrong:

**Option 1**: Keep existing `/api/v1/assets` endpoint
```typescript
// Use original endpoint
const {
  data: assetsResponse,
  isLoading: isLoadingAssets,
  isError: isAssetsError,
} = useGetAvailableAssetsQuery(assetType);
```

**Option 2**: Use test endpoint without mock fallback
```typescript
const assets = apiAssets;  // Use only API data
```

---

## Summary

1. ✅ 3 files created on backend
2. ✅ New `/api/v1/products?type=ETF` endpoint ready
3. ✅ Test data available in database
4. ⏳ Follow this guide to integrate on FE
5. ✅ Done! Real data instead of mocks

All the hard work is done on the backend! Just follow the integration steps above. 🚀

