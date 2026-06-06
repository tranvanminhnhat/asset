import { InvestmentRecommendationsApiResponse } from '../type/investment-amount.type';

export const mapInvestmentAmountResponse = (response: InvestmentRecommendationsApiResponse) => {
  return {
    allocations: response.data.allocations,
    message: response.message,
  };
};