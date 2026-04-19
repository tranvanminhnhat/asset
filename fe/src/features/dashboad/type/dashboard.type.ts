export type TransactionType = 'BUY' | 'SELL' | 'DIVIDEND' | 'ADJUSTMENT' | string;

export interface ApiMessage {
  code: string;
  message: string;
  service: string;
}

export interface ApiResponse<T> {
  data: T;
  message: ApiMessage;
}

export interface DashboardSummary {
  totalCurrentValue: number;
  totalPurchaseValue: number;
  totalProfit: number;
  profitPercentage: number;
  totalAssetTypes?: number;
  totalTransactions?: number;
  currency: string;
}

export interface DashboardPortfolioItem {
  id: number;
  name: string;
  type: string;
  purchaseValue: number;
  currentValue: number;
  profit: number;
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