import { baseApi } from '@/store/api/baseApi';
import { InvestmentAmountRequest, InvestmentAmountResponse, RecommendationResponse } from '../type/investment-amount.type';

export const investmentAmountApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getRecommendation: builder.mutation<InvestmentAmountResponse | RecommendationResponse, InvestmentAmountRequest>({
      query: (body) => ({
        url: '/investment-amount',
        method: 'POST',
        body,
      }),
    }),
  }),
});

export const { useGetRecommendationMutation } = investmentAmountApi;