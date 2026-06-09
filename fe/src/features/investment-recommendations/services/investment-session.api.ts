import { baseApi } from '@/store/api/baseApi';
import { ReviewSessionApiResponse, SessionDetailApiResponse } from '../type/investment-recommendations.type';

export const investmentSessionApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getSessionDetail: builder.query<SessionDetailApiResponse, number>({
      query: (sessionId) => ({
        url: `/api/v1/investment/${sessionId}/detail`,
        method: 'GET',
      }),
    }),
    reviewSession: builder.mutation<ReviewSessionApiResponse, number>({
      query: (sessionId) => ({
        url: `/api/v1/investment/${sessionId}/review`,
        method: 'POST',
      }),
    }),
  }),
});

export const { useGetSessionDetailQuery, useReviewSessionMutation } = investmentSessionApi;
