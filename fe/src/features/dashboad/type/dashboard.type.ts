export interface DashboardSummary {
  totalCurrentValue: number;
  totalPurchaseValue: number;
  totalProfit: number;
  profitPercentage: number;
  currency: string;
}

export interface Portfolio {
  id: number;
  name: string;
  type: string;
  purchaseValue: number;
  currentValue: number;
  profit: number;
  profitPercentage: number | null;
  isProfit: boolean;
}

export interface DashboardData {
  summary: DashboardSummary;
  portfolios: Portfolio[];
}

export interface DashboardResponse {
  data: DashboardData;
  message: {
    code: string;
    message: string;
    service: string;
  };
}