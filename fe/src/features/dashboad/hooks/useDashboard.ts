import { useMemo, useState } from 'react';
import {
  useAddTransactionMutation,
  useGetDashboardQuery,
  useGetTransactionsQuery,
} from '../services/dashboad.api';
import { toDashboardPayload, toTransactions } from '../mappers/dashboard.mapper';
import { TransactionRequest } from '../type/dashboard.type';

export const useDashboard = () => {
  const dashboardQuery = useGetDashboardQuery();
  const dashboardData = useMemo(() => toDashboardPayload(dashboardQuery.data), [dashboardQuery.data]);
  const [selectedPortfolioId, setSelectedPortfolioId] = useState<number | null>(null);
  const activePortfolioId = selectedPortfolioId ?? dashboardData?.portfolios[0]?.id ?? null;
  const transactionsQuery = useGetTransactionsQuery(activePortfolioId ?? 0, {
    skip: activePortfolioId === null,
  });
  const [addTransaction, addTransactionState] = useAddTransactionMutation();

  const submitTransaction = async (request: TransactionRequest) => {
    await addTransaction(request).unwrap();
    await Promise.all([dashboardQuery.refetch(), transactionsQuery.refetch()]);
  };

  return {
    dashboardData,
    isLoading: dashboardQuery.isLoading || transactionsQuery.isLoading,
    error: dashboardQuery.error || transactionsQuery.error,
    transactionError: addTransactionState.error,
    isSubmittingTransaction: addTransactionState.isLoading,
    selectedPortfolioId: activePortfolioId,
    setSelectedPortfolioId,
    transactions: toTransactions(transactionsQuery.data),
    submitTransaction,
  };
};