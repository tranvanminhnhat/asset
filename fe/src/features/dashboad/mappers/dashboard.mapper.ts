import {
  ApiResponse,
  DashboardResponse,
  DashboardSummary,
  TransactionResponse,
} from '../type/dashboard.type';

const getPayload = <T>(response?: ApiResponse<T> | T): T | undefined => {
  if (!response) {
    return undefined;
  }

  if (typeof response === 'object' && response !== null && 'data' in response) {
    return response.data;
  }

  return response;
};

export const toDashboardPayload = (
  response?: ApiResponse<DashboardResponse> | DashboardResponse,
): DashboardResponse | null => {
  const payload = getPayload(response);

  if (!payload) {
    return null;
  }

  const rawSummary = payload.summary as Partial<DashboardSummary> & {
    totalInvested?: number;
    totalAssetTypes?: number;
    totalTransactions?: number;
  };

  const currency = rawSummary.currency ?? 'USD';
  const totalPurchaseValue = rawSummary.totalPurchaseValue ?? rawSummary.totalInvested ?? 0;
  const totalCurrentValue = rawSummary.totalCurrentValue ?? totalPurchaseValue;
  const totalProfit = rawSummary.totalProfit ?? totalCurrentValue - totalPurchaseValue;
  const profitPercentage = rawSummary.profitPercentage ?? (totalPurchaseValue > 0
    ? (totalProfit / totalPurchaseValue) * 100
    : 0);

  return {
    ...payload,
    summary: {
      totalCurrentValue,
      totalPurchaseValue,
      totalProfit,
      profitPercentage,
      totalAssetTypes: rawSummary.totalAssetTypes ?? 0,
      totalTransactions: rawSummary.totalTransactions ?? 0,
      currency,
    },
    portfolios: payload.portfolios ?? [],
  };
};

export const toTransactions = (
  response?: ApiResponse<TransactionResponse[]> | TransactionResponse[],
): TransactionResponse[] => {
  const transactions = getPayload(response) ?? [];

  return [...transactions].sort((left, right) => {
    return new Date(right.transactionDate).getTime() - new Date(left.transactionDate).getTime();
  });
};
