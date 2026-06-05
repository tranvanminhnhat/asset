// Portfolio Selection Types
export interface PortfolioOption {
  id: string;
  name: string;
  description: string;
  riskLevel: 'LOW' | 'MEDIUM' | 'HIGH';
  expectedReturn: number;
}

export interface SelectedPortfolio {
  portfolioId: string;
  portfolioName: string;
  allocations: AllocationItem[];
}

export interface AllocationItem {
  id: string;
  name: string;
  percentage: number;
  description: string;
}

export interface StockOption {
  id: string;
  code: string;
  name: string;
  currentPrice: number;
  change: number;
  changePercent: number;
  volume: number;
  advantages: string[];
  risks: string[];
}
