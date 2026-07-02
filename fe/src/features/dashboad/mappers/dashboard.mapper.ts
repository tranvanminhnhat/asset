import {
  ApiResponse,
  DashboardResponse,
  DashboardSummary,
  TransactionResponse,
} from '../type/dashboard.type';

const toNumber = (value: number | string | null | undefined): number => {
  if (typeof value === 'number') {
    return Number.isFinite(value) ? value : 0;
  }

  if (typeof value === 'string') {
    const parsedValue = Number(value);
    return Number.isFinite(parsedValue) ? parsedValue : 0;
  }

  return 0;
};

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
    totalInvestedCost?: number | string;
    totalCurrentValue?: number | string;
    totalProfit?: number | string;
    totalProfitRate?: number | string;
    totalAssetTypes?: number;
    totalTransactions?: number;
  };

  const currency = rawSummary.currency ?? 'VND';
  const totalInvestedCost = toNumber(rawSummary.totalInvestedCost ?? rawSummary.totalPurchaseValue ?? rawSummary.totalInvested);
  const totalPurchaseValue = totalInvestedCost;
  const totalCurrentValue = toNumber(rawSummary.totalCurrentValue ?? totalPurchaseValue);
  const totalProfit = toNumber(rawSummary.totalProfit ?? totalCurrentValue - totalPurchaseValue);
  const totalProfitRate = toNumber(rawSummary.totalProfitRate ?? rawSummary.profitPercentage);
  const profitPercentage = rawSummary.profitPercentage ?? totalProfitRate ?? (totalPurchaseValue > 0
    ? (totalProfit / totalPurchaseValue) * 100
    : 0);

  const normalizedPortfolios = (payload.portfolios ?? []).map((portfolio, index) => {
    const assetType = portfolio.assetType ?? portfolio.type ?? 'UNKNOWN';
    const investedCost = toNumber(portfolio.investedCost ?? portfolio.purchaseValue);
    const currentValue = toNumber(portfolio.currentValue);
    const profit = toNumber(portfolio.profit ?? currentValue - investedCost);
    const profitRate = toNumber(portfolio.profitRate ?? portfolio.profitPercentage ?? (investedCost > 0
      ? (profit / investedCost) * 100
      : 0));

    return {
      ...portfolio,
      id: portfolio.id ?? index + 1,
      name: portfolio.name ?? assetType,
      type: portfolio.type ?? assetType,
      assetType,
      quantity: toNumber(portfolio.quantity),
      investedCost,
      purchaseValue: investedCost,
      currentValue,
      profit,
      profitRate,
      profitPercentage: profitRate,
      isProfit: portfolio.isProfit ?? profit >= 0,
    };
  });

  return {
    ...payload,
    summary: {
      totalInvestedCost,
      totalCurrentValue,
      totalPurchaseValue,
      totalProfit,
      totalProfitRate,
      profitPercentage,
      totalAssetTypes: rawSummary.totalAssetTypes ?? normalizedPortfolios.length,
      totalTransactions: rawSummary.totalTransactions ?? 0,
      currency,
    },
    portfolios: normalizedPortfolios,
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
