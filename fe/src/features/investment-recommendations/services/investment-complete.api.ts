import { baseApi } from '@/store/api/baseApi';
import { InvestmentCompleteApiResponse, InvestmentCompleteRequest } from '../type/investment-complete.type';

export const investmentCompleteApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    completeInvestment: builder.mutation<InvestmentCompleteApiResponse, InvestmentCompleteRequest>({
      query: (body) => ({
        url: '/investment-complete',
        method: 'POST',
        body,
      }),
    }),
  }),
});

export const { useCompleteInvestmentMutation } = investmentCompleteApi;
