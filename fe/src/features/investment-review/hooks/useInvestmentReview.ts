import { useGetSessionDetailForReviewQuery } from '../services/investment-review.api';
import { SessionDetailReview } from '../type/investment-review.type';

const getSessionId = (): number | null => {
  if (typeof window === 'undefined') return null;
  const raw = localStorage.getItem('investmentSessionId');
  if (!raw) return null;
  const id = Number(raw);
  return Number.isNaN(id) ? null : id;
};

export const useInvestmentReview = () => {
  const sessionId = getSessionId();

  const {
    data: sessionDetailResponse,
    isLoading,
    isError,
  } = useGetSessionDetailForReviewQuery(sessionId!, { skip: !sessionId });

  const sessionDetail: SessionDetailReview | null = sessionDetailResponse?.data ?? null;

  return {
    sessionId,
    sessionDetail,
    isLoading,
    isError,
  };
};
