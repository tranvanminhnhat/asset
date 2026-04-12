const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:3001';

import { InvestmentAmountRequest, InvestmentAmountResponse } from '../type/investment-amount.type';

export const callInvestmentAmountApi = async (request: InvestmentAmountRequest): Promise<InvestmentAmountResponse> => {
  const response = await fetch(`${API_BASE_URL}/investment-amount`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    throw new Error('Failed to fetch investment amount');
  }

  return response.json();
};