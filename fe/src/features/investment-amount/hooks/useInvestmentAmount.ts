import { useState } from 'react';
import { callInvestmentAmountApi } from '../services/investment-amount.api';
import { InvestmentAmountRequest, InvestmentAmountResponse } from '../type/investment-amount.type';

export const useInvestmentAmount = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const submitInvestmentAmount = async (request: InvestmentAmountRequest): Promise<InvestmentAmountResponse | null> => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await callInvestmentAmountApi(request);
      return response;
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
      return null;
    } finally {
      setIsLoading(false);
    }
  };

  return {
    submitInvestmentAmount,
    isLoading,
    error,
  };
};