import { InvestmentAmountResponse } from '../type/investment-amount.type';

export const mapInvestmentAmountResponse = (response: InvestmentAmountResponse) => {
  return {
    allocations: response.data.allocations,
    message: response.message,
  };
};