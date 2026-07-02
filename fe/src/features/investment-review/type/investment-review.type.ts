// Investment Review Types

export interface AllocationDetailReview {
  allocationId: number;
  assetType: string;
  recommendedAmount: number;
  percentage: number;
  status: string;
  investedAmount: number;
  residualAmount: number;
  selections: AssetSelectionReview[];
}

export interface AssetSelectionReview {
  selectionId: number;
  assetCode: string;
  assetName: string;
  quantity: number;
  buyPrice?: number;
  currentPrice: number;
  investedAmount: number;
  createdAt: string;
}

export interface AssetSelectionReviewApiItem {
  selectionId: number;
  assetCode: string;
  assetName: string;
  quantity: number;
  buyPrice?: number;
  currentPrice?: number;
  investedAmount: number;
  createdAt: string;
}

export interface AllocationDetailReviewApiItem extends Omit<AllocationDetailReview, 'selections'> {
  selections: AssetSelectionReviewApiItem[];
}

export interface SessionDetailReviewApiItem extends Omit<SessionDetailReview, 'allocations'> {
  allocations: AllocationDetailReviewApiItem[];
}

export interface SessionDetailReview {
  sessionId: number;
  userId: string;
  strategyCode: string;
  totalAmount: number;
  status: string;
  currentStep: string;
  createdAt: string;
  updatedAt: string;
  allocations: AllocationDetailReview[];
}

export interface SessionDetailReviewApiResponse {
  data: SessionDetailReview;
  message?: {
    code: string;
    message: string;
    service: string;
  };
}

export interface SessionDetailReviewApiRawResponse {
  data: SessionDetailReviewApiItem;
  message?: {
    code: string;
    message: string;
    service: string;
  };
}
