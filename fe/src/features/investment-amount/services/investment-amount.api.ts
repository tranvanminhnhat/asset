import { baseApi } from '@/store/api/baseApi';
import {
  AllocationMasterItem,
  AllocationMasterListResponse,
  InvestmentAmountRequest,
  InvestmentAmountResponse,
  InvestmentTypeOption,
  InvestmentTypeOptionsData,
  InvestmentTypeOptionsResponse,
  RecommendationResponse,
} from '../type/investment-amount.type';

const INVESTMENT_TYPE_OPTIONS_URL = '/api/v1/allocation-master';

export const investmentAmountApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getInvestmentTypeOptions: builder.query<
      AllocationMasterListResponse | AllocationMasterItem[] | InvestmentTypeOptionsResponse | InvestmentTypeOptionsData | InvestmentTypeOption[],
      void
    >({
      query: () => ({
        url: INVESTMENT_TYPE_OPTIONS_URL,
        method: 'GET',
      }),
    }),
    getRecommendation: builder.mutation<InvestmentAmountResponse | RecommendationResponse, InvestmentAmountRequest>({
      query: (body) => ({
        url: '/investment-amount',
        method: 'POST',
        body,
      }),
    }),
  }),
});

export const { useGetInvestmentTypeOptionsQuery, useGetRecommendationMutation } = investmentAmountApi;