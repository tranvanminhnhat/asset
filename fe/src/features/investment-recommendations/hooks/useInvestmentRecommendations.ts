import { useGetSessionDetailQuery, useReviewSessionMutation } from '../services/investment-session.api';
import { ReviewSessionResponse, SessionDetailResponse } from '../type/investment-recommendations.type';

const getSessionId = (): number | null => {
  if (typeof window === 'undefined') return null;
  const raw = localStorage.getItem('investmentSessionId');
  if (!raw) return null;
  const id = Number(raw);
  return Number.isNaN(id) ? null : id;
};

export const useInvestmentRecommendations = () => {
  const sessionId = getSessionId();

  const {
    data: sessionDetailResponse,
    isLoading,
    isError,
    refetch,
  } = useGetSessionDetailQuery(sessionId!, { skip: !sessionId });

  const [reviewSession, { isLoading: isReviewing, isError: isReviewError }] = useReviewSessionMutation();

  const sessionDetail: SessionDetailResponse | null = sessionDetailResponse?.data ?? null;

  const submitReview = async (): Promise<ReviewSessionResponse | null> => {
    if (!sessionId) return null;
    const response = await reviewSession(sessionId).unwrap();
    return response.data ?? null;
  };

  return {
    sessionId,
    sessionDetail,
    isLoading,
    isError,
    isReviewing,
    isReviewError,
    refetch,
    submitReview,
  };
};
