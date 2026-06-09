import { baseApi } from '@/store/api/baseApi';
import { SessionDetailReviewApiResponse } from '../type/investment-review.type';

export const investmentReviewApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getSessionDetailForReview: builder.query<SessionDetailReviewApiResponse, number>({
      query: (sessionId) => ({
        url: `/api/v1/investment/${sessionId}/detail`,
        method: 'GET',
      }),
    }),
  }),
});

export const { useGetSessionDetailForReviewQuery } = investmentReviewApi;
