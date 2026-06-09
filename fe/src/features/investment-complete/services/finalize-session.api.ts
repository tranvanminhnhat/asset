import { baseApi } from '@/store/api/baseApi';

export interface FinalizeSessionResponse {
  sessionId: number;
  status: string;
  totalInvested: number;
  totalResidual: number;
  updatedCashBalance: number;
  completedAt: string;
}

export interface FinalizeSessionApiResponse {
  data: FinalizeSessionResponse;
  message?: {
    code: string;
    message: string;
    service: string;
  };
}

export const finalizeSessionApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    finalizeSession: builder.mutation<FinalizeSessionApiResponse, number>({
      query: (sessionId) => ({
        url: `/api/v1/investment/${sessionId}/finalize`,
        method: 'POST',
      }),
    }),
  }),
});

export const { useFinalizeSessionMutation } = finalizeSessionApi;
