import { baseApi } from '@/store/api/baseApi';
import {
  InvestmentPlanListApiResponse,
  InvestmentPlanDetailApiResponse,
} from '../type/investment-plans.type';

export const investmentPlansApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getInvestmentPlans: builder.query<InvestmentPlanListApiResponse, void>({
      query: () => ({ url: '/api/v1/investment-plans', method: 'GET' }),
    }),
    getInvestmentPlanById: builder.query<InvestmentPlanDetailApiResponse, number>({
      query: (planId) => ({ url: `/api/v1/investment-plans/${planId}`, method: 'GET' }),
    }),
  }),
});

export const {
  useGetInvestmentPlansQuery,
  useGetInvestmentPlanByIdQuery,
} = investmentPlansApi;
