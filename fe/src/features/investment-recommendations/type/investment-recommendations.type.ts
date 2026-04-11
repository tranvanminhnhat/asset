// Investment Recommendations Types
export interface FundAllocation {
  id: string;
  name: string;
  type: string;
  amount: number;
  currency: string;
  percentage: number;
  description: string;
}

export interface InvestmentPortfolio {
  id: string;
  title: string;
  description: string;
  totalAmount: number;
  currency: string;
  funds: FundAllocation[];
  investmentStrategy: string;
}

export interface RecommendedStock {
  id: string;
  code: string;
  name: string;
  currentPrice: number;
  estimatedPrice: number;
  potentialReturn: number;
  riskLevel: string;
  advantages: string[];
  risks: string[];
  sector: string;
}

export interface StockRecommendation {
  portfolio: InvestmentPortfolio;
  recommendedStocks: RecommendedStock[];
  allocationStrategy: string;
}
