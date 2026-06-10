import { baseApi } from '@/store/api/baseApi';

export interface FinalizeAllocationItem {
  assetType: string;
  finalAmount: number;
}

export interface FinalizeSessionRequest {
  allocations: FinalizeAllocationItem[];
}

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
    finalizeSession: builder.mutation<FinalizeSessionApiResponse, { sessionId: number; body: FinalizeSessionRequest }>({
      query: ({ sessionId, body }) => ({
        url: `/api/v1/investment/${sessionId}/finalize`,
        method: 'POST',
        body,
      }),
    }),
  }),
});

export const { useFinalizeSessionMutation } = finalizeSessionApi;
