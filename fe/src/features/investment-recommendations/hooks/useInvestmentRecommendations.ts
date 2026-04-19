import { useState } from 'react';
import { InvestmentData } from '../type/investment-recommendations.type';
import { useCompleteInvestmentMutation } from '../services/investment-complete.api';
import {
  InvestmentCompleteApiResponse,
  InvestmentCompleteRequest,
  InvestmentCompleteResponse,
} from '../type/investment-complete.type';

const INVESTMENT_COMPLETE_STORAGE_KEY = 'investmentCompleteResult';

const getCompletePayload = (
  response: InvestmentCompleteApiResponse | InvestmentCompleteResponse,
): InvestmentCompleteResponse | null => {
  const candidate = 'data' in response ? response.data : response;

  if (!candidate || typeof candidate !== 'object') {
    return null;
  }

  if (!('sessionId' in candidate) || !('allocations' in candidate) || !Array.isArray(candidate.allocations)) {
    return null;
  }

  return candidate as InvestmentCompleteResponse;
};

export const useInvestmentRecommendations = () => {
  const getInitialData = () => {
    if (typeof window !== 'undefined') {
      const storedData = localStorage.getItem('investmentAllocations');
      return storedData ? JSON.parse(storedData) : null;
    }
    return null;
  };

  const [data] = useState<InvestmentData | null>(getInitialData);
  const [isLoading] = useState(false);
  const [completeInvestment, { isLoading: isSubmitting, isError, error }] = useCompleteInvestmentMutation();

  const submitInvestmentComplete = async (): Promise<InvestmentCompleteResponse | null> => {
    if (!data) {
      return null;
    }

    const request: InvestmentCompleteRequest = {
      amount: data.amount,
      type: data.type,
      allocations: data.allocations,
    };

    const response = await completeInvestment(request).unwrap();
    localStorage.setItem(INVESTMENT_COMPLETE_STORAGE_KEY, JSON.stringify(response));

    return getCompletePayload(response);
  };

  return {
    data,
    isLoading,
    isSubmitting,
    isError,
    error,
    submitInvestmentComplete,
  };
};