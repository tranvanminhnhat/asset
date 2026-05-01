// Investment Amount Types
export type InvestmentType = 'SAFE' | 'BALANCED' | 'GROWTH';

export interface InvestmentAmountRequest {
  amount: number;
  type: InvestmentType;
}

export interface Allocation {
  type: string;
  amount: number;
  quantity?: number;
  percent: number;
  carryOverAmount?: AmountLike;
}

export type AmountLike = number | string | null;

export interface InvestmentAmountResponse {
  data: RecommendationResponse;
  message: {
    code: string;
    message: string;
    service: string;
  };
}

export interface RecommendationResponse {
  allocations: Allocation[];
}

export interface InvestmentTypeOption {
  type: InvestmentType;
  label: string;
  description: string;
}

export interface AllocationMasterItem {
  code: string;
  stockPercent: number;
  emergencyPercent: number;
  goldPercent: number;
  etfPercent: number;
}

export interface AllocationMasterListResponse {
  data: AllocationMasterItem[];
  message?: {
    code: string;
    message: string;
    service: string;
  };
}

export interface InvestmentTypeOptionsData {
  options: InvestmentTypeOption[];
}

export interface InvestmentTypeOptionsResponse {
  data: InvestmentTypeOptionsData | InvestmentTypeOption[];
  message?: {
    code: string;
    message: string;
    service: string;
  };
}

export interface InvestmentGoal {
  riskLevel: 'LOW' | 'MEDIUM' | 'HIGH';
  monthlyAmount: number;
  investmentPeriod: number;
}

export interface InvestmentPlan {
  riskLevel: 'LOW' | 'MEDIUM' | 'HIGH';
  monthlyAmount: number;
  investmentPeriod: number;
  projectedReturn: number;
  totalAmount: number;
}

export interface ProjectionResult {
  riskLevel: string;
  riskDescription: string;
  monthlyAmount: number;
  investmentPeriod: number;
  totalAmount: number;
  projectedReturn: number;
  projectedValue: number;
}
