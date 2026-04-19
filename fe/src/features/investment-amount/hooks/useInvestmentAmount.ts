import { useGetRecommendationMutation } from '../services/investment-amount.api';
import {
  InvestmentAmountRequest,
  InvestmentAmountResponse,
  RecommendationResponse,
} from '../type/investment-amount.type';

const getRecommendationPayload = (
  response: InvestmentAmountResponse | RecommendationResponse,
): RecommendationResponse | null => {
  if ('data' in response) {
    return response.data;
  }

  return response;
};

export const useInvestmentAmount = () => {
  const [getRecommendation, { isLoading, error }] = useGetRecommendationMutation();

  const submitInvestmentAmount = async (request: InvestmentAmountRequest): Promise<RecommendationResponse | null> => {
    const response = await getRecommendation(request).unwrap();

    return getRecommendationPayload(response);
  };

  return {
    submitInvestmentAmount,
    isLoading,
    error: error ? 'Không thể lấy đề xuất đầu tư.' : null,
  };
};