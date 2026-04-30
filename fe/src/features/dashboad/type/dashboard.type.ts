export type TransactionType = 'BUY' | 'SELL' | 'DIVIDEND' | 'ADJUSTMENT' | string;

export interface ApiMessage {
  code: string;
  message: string;
  service: string;
}

export interface ApiResponse<T> {
  data: T;
  message?: ApiMessage | string;
  statusCode?: number;
  timestamp?: string;
}

export interface DashboardSummary {
  totalInvestedCost: number;
  totalCurrentValue: number;
  totalPurchaseValue: number;
  totalProfit: number;
  totalProfitRate: number;
  profitPercentage: number;
  totalAssetTypes?: number;
  totalTransactions?: number;
  currency: string;
}

export interface DashboardPortfolioItem {
  id: number;
  name: string;
  type: string;
  assetType: string;
  quantity: number;
  investedCost: number;
  purchaseValue: number;
  currentValue: number;
  profit: number;
  profitRate: number;
  profitPercentage: number;
  isProfit: boolean;
}

export interface DashboardResponse {
  summary: DashboardSummary;
  portfolios: DashboardPortfolioItem[];
}

export interface TransactionRequest {
  portfolioId: number;
  transactionType: TransactionType;
  assetType: string;
  quantity: number;
  unitPrice: number;
  transactionDate: string;
  notes?: string;
  referenceId?: string;
}

export interface TransactionResponse {
  transactionId: number;
  portfolioId: number;
  transactionType: TransactionType;
  assetType: string;
  quantity: number;
  unitPrice: number;
  totalAmount: number;
  transactionDate: string;
  notes: string | null;
  referenceId: string | null;
}