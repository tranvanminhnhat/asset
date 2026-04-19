// Investment Amount Types
export type InvestmentType = 'SAFE' | 'BALANCED' | 'GROWTH';

export interface InvestmentAmountRequest {
  amount: number;
  type: InvestmentType;
}

export interface Allocation {
  type: string;
  amount: number;
  percent: number;
}

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
