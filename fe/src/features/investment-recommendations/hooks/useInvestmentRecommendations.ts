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
      return storedData ? (JSON.parse(storedData) as InvestmentData) : null;
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

    const totalAmount = data.allocations.reduce((sum, allocation) => {
      const quantity = typeof allocation.quantity === 'number' && Number.isFinite(allocation.quantity)
        ? Math.max(1, Math.floor(allocation.quantity))
        : 1;
      return sum + allocation.amount * quantity;
    }, 0);

    const request: InvestmentCompleteRequest = {
      amount: totalAmount > 0 ? totalAmount : data.amount,
      type: data.type,
      allocations: data.allocations.map((allocation) => ({
        type: allocation.type,
        amount: Number.isFinite(allocation.amount) ? allocation.amount : 0,
        percent: Number.isFinite(allocation.percent) ? allocation.percent : 0,
        residualAmount:
          typeof allocation.residualAmount === 'number' && Number.isFinite(allocation.residualAmount)
            ? allocation.residualAmount
            : 0,
        quantity: typeof allocation.quantity === 'number' && Number.isFinite(allocation.quantity)
          ? Math.max(1, Math.floor(allocation.quantity))
          : 1,
      })),
    };

    const response = await completeInvestment(request).unwrap();
    const payload = getCompletePayload(response);

    if (payload) {
      localStorage.setItem(INVESTMENT_COMPLETE_STORAGE_KEY, JSON.stringify(payload));
    }

    return payload;
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