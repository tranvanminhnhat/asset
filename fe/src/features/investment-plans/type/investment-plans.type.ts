// Investment Plans Types
// Matches BE: InvestmentPlanResponse, InvestmentPlanAllocationResponse

export interface InvestmentPlanAllocation {
  allocationId: number;
  assetType: string;
  amount: number;
  percentage: number;
}

export interface InvestmentPlanResponse {
  planId: number;
  userId: number;
  strategyId: number | null;
  totalAmount: number;
  status: string;
  recommendationSnapshot: string | null;
  allocations: InvestmentPlanAllocation[];
  createdAt: string;
  updatedAt: string | null;
}

export interface InvestmentPlanListApiResponse {
  data: InvestmentPlanResponse[];
  message?: string;
  statusCode?: number;
}

export interface InvestmentPlanDetailApiResponse {
  data: InvestmentPlanResponse;
  message?: string;
  statusCode?: number;
}
