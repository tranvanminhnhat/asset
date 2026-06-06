// Investment Amount Types

export interface InvestmentAmountRequest {
  amount: number;
  type: string;
}

export interface Allocation {
  type: string;
  amount: number;
  percent: number;
  carryOverAmount: number;
  investableAmount: number;
  residualAmount: number;
}

export interface InvestmentRecommendationsData {
  allocations: Allocation[];
}

export interface InvestmentRecommendationsApiResponse {
  data: InvestmentRecommendationsData;
  message: {
    code: string;
    message: string;
    service: string;
  };
}

export interface InvestmentTypeOption {
  type: string;
  label: string;
  description: string;
}

export interface AllocationMasterDetail {
  id: number;
  strategyId: number;
  assetType: string;
  percentage: number;
  minimumLotSizeAmount: number;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface AllocationMasterItem {
  id: number;
  code: string;
  name: string;
  createdAt: string | null;
  updatedAt: string | null;
  details: AllocationMasterDetail[];
}

export interface AllocationMasterListResponse {
  data: AllocationMasterItem[];
  message?: {
    code: string;
    message: string;
    service: string;
  };
}


