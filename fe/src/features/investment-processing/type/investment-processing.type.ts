// Investment Processing Types

// Matches BE: investment.dto.response.AssetPriceResponse
export interface AssetPrice {
  // Legacy fields (may be null for newer assets)
  assetCode?: string | null;
  assetType?: string | null;
  assetName?: string | null;
  // New fields
  id: number;
  productId: number;
  buyPrice: number;
  sellPrice: number;
  priceDate: string | null;
  isLatest: boolean;
  isActive: boolean;
}

export interface AssetsApiResponse {
  data: AssetPrice[];
  message?: {
    code: string;
    message: string;
    service: string;
  };
}

export interface SelectAssetRequest {
  assetCode: string;
  quantity: number;
}

export interface SelectAssetResponse {
  selectionId: number;
  allocationId: number;
  assetCode: string;
  assetName: string;
  quantity: number;
  buyPrice: number;
  investedAmount: number;
  residualAmount: number;
  createdAt: string;
}

export interface SelectAssetApiResponse {
  data: SelectAssetResponse;
  message?: {
    code: string;
    message: string;
    service: string;
  };
}

export interface CompleteAllocationResponse {
  allocationId: number;
  assetType: string;
  status: string;
  investedAmount: number;
  residualAmount: number;
  updatedAt: string;
}

export interface CompleteAllocationApiResponse {
  data: CompleteAllocationResponse;
  message?: {
    code: string;
    message: string;
    service: string;
  };
}
