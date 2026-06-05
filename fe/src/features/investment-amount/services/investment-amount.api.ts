import { baseApi } from '@/store/api/baseApi';
import {
  AllocationMasterListResponse,
  InvestmentAmountRequest,
  InvestmentRecommendationsApiResponse,
} from '../type/investment-amount.type';

const INVESTMENT_TYPE_OPTIONS_URL = '/api/v1/allocation-master';
const INVESTMENT_RECOMMENDATIONS_URL = '/investment-recommendations';

export const investmentAmountApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getInvestmentTypeOptions: builder.query<AllocationMasterListResponse, void>({
      query: () => ({
        url: INVESTMENT_TYPE_OPTIONS_URL,
        method: 'GET',
      }),
    }),
    getInvestmentRecommendations: builder.mutation<InvestmentRecommendationsApiResponse, InvestmentAmountRequest>({
      query: (body) => ({
        url: INVESTMENT_RECOMMENDATIONS_URL,
        method: 'POST',
        body,
      }),
    }),
  }),
});

export const { useGetInvestmentTypeOptionsQuery, useGetInvestmentRecommendationsMutation } = investmentAmountApi;