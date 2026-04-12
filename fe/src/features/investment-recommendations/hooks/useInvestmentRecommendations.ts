import { useState } from 'react';
import { InvestmentData } from '../type/investment-recommendations.type';

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

  return {
    data,
    isLoading,
  };
};