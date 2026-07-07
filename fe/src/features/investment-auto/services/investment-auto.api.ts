import { baseApi } from '@/store/api/baseApi';

export interface AutoInvestRequest {
  amount: number;
}

export interface AutoInvestResponse {
  sessionId: number;
  status: string;
  totalInvested: number;
  totalResidual: number;
  updatedCashBalance: number;
  completedAt: string;
}

export interface AutoInvestApiResponse {
  data: AutoInvestResponse;
  message?: {
    code: string;
    message: string;
    service: string;
  };
}

export const investmentAutoApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    executeAutoInvest: builder.mutation<AutoInvestApiResponse, AutoInvestRequest>({
      query: (body) => ({ url: '/api/v1/investment/auto', method: 'POST', body }),
      invalidatesTags: ['Notifications', 'SessionDetail'],
    }),
  }),
});

export const { useExecuteAutoInvestMutation } = investmentAutoApi;