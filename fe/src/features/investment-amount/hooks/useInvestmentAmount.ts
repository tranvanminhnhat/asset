import { useGetInvestmentTypeOptionsQuery, useGetInvestmentRecommendationsMutation } from '../services/investment-amount.api';
import {
  AllocationMasterItem,
  InvestmentAmountRequest,
  InvestmentRecommendationsData,
  InvestmentTypeOption,
} from '../type/investment-amount.type';

const ASSET_TYPE_LABELS: Record<string, string> = {
  STOCK: 'Cổ phiếu',
  EMERGENCY: 'Quỹ dự phòng',
  GOLD: 'Vàng',
  ETF: 'ETF',
};

const toOption = (item: AllocationMasterItem): InvestmentTypeOption => {
  const description = item.details
    .map((d) => `${ASSET_TYPE_LABELS[d.assetType] ?? d.assetType} ${d.percentage}%`)
    .join(' • ');

  return {
    type: item.code,
    label: item.name,
    description,
  };
};

export const useInvestmentAmount = () => {
  const {
    data: investmentTypeOptionsResponse,
    isLoading: isLoadingTypeOptions,
    isError: isTypeOptionsError,
  } = useGetInvestmentTypeOptionsQuery();
  const [getInvestmentRecommendations, { isLoading, error }] = useGetInvestmentRecommendationsMutation();

  const investmentTypeOptions = investmentTypeOptionsResponse?.data?.map(toOption) ?? [];

  const submitInvestmentAmount = async (request: InvestmentAmountRequest): Promise<InvestmentRecommendationsData | null> => {
    const response = await getInvestmentRecommendations(request).unwrap();
    return response.data ?? null;
  };

  return {
    investmentTypeOptions,
    isLoadingTypeOptions,
    isTypeOptionsError,
    submitInvestmentAmount,
    isLoading,
    error: error ? 'Không thể lấy đề xuất đầu tư.' : null,
  };
};