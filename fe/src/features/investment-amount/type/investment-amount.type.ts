// Investment Amount Types
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
