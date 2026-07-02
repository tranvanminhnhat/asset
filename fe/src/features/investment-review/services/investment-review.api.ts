import { baseApi } from '@/store/api/baseApi';
import { SessionDetailReviewApiRawResponse, SessionDetailReviewApiResponse } from '../type/investment-review.type';

export const investmentReviewApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getSessionDetailForReview: builder.query<SessionDetailReviewApiResponse, number>({
      query: (sessionId) => ({
        url: `/api/v1/investment/${sessionId}/detail`,
        method: 'GET',
      }),
      transformResponse: (response: SessionDetailReviewApiRawResponse): SessionDetailReviewApiResponse => ({
        data: {
          ...response.data,
          allocations: (response.data.allocations ?? []).map((allocation) => ({
            ...allocation,
            selections: (allocation.selections ?? []).map((selection) => ({
              ...selection,
              currentPrice: selection.currentPrice ?? selection.buyPrice ?? 0,
            })),
          })),
        },
        message: response.message,
      }),
    }),
  }),
});

export const { useGetSessionDetailForReviewQuery } = investmentReviewApi;
