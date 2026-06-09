// Investment Recommendations Types - Session-based workflow

export interface AllocationDetail {
  allocationId: number;
  assetType: string;
  recommendedAmount: number;
  percentage: number;
  status: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED' | 'LOCKED';
  investedAmount: number;
  residualAmount: number;
  selections: AssetSelection[];
}

export interface AssetSelection {
  selectionId: number;
  assetCode: string;
  assetName: string;
  quantity: number;
  buyPrice: number;
  investedAmount: number;
  createdAt: string;
}

export interface SessionDetailResponse {
  sessionId: number;
  userId: string;
  strategyCode: string;
  totalAmount: number;
  status: string;
  currentStep: string;
  createdAt: string;
  updatedAt: string;
  allocations: AllocationDetail[];
}

export interface SessionDetailApiResponse {
  data: SessionDetailResponse;
  message?: {
    code: string;
    message: string;
    service: string;
  };
}

export interface ReviewSessionResponse {
  sessionId: number;
  status: string;
  totalInvested: number;
  totalResidual: number;
  updatedAt: string;
}

export interface ReviewSessionApiResponse {
  data: ReviewSessionResponse;
  message?: {
    code: string;
    message: string;
    service: string;
  };
}
