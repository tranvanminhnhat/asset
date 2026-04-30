import { useGetInvestmentTypeOptionsQuery, useGetRecommendationMutation } from '../services/investment-amount.api';
import {
  AllocationMasterItem,
  AllocationMasterListResponse,
  InvestmentAmountRequest,
  InvestmentAmountResponse,
  InvestmentType,
  InvestmentTypeOption,
  InvestmentTypeOptionsData,
  InvestmentTypeOptionsResponse,
  RecommendationResponse,
} from '../type/investment-amount.type';

const INVESTMENT_TYPE_LABELS: Record<InvestmentType, string> = {
  SAFE: 'An Toan',
  BALANCED: 'Can Bang',
  GROWTH: 'Tang Truong',
};

const INVESTMENT_TYPE_ORDER: Record<InvestmentType, number> = {
  SAFE: 1,
  BALANCED: 2,
  GROWTH: 3,
};

const isInvestmentType = (code: string): code is InvestmentType => {
  return code === 'SAFE' || code === 'BALANCED' || code === 'GROWTH';
};

const toOption = (item: AllocationMasterItem): InvestmentTypeOption | null => {
  if (!isInvestmentType(item.code)) {
    return null;
  }

  return {
    type: item.code,
    label: INVESTMENT_TYPE_LABELS[item.code],
    description: `Co phieu ${item.stockPercent}% • Quy du phong ${item.emergencyPercent}% • Vang ${item.goldPercent}% • ETF ${item.etfPercent}%`,
  };
};

const getRecommendationPayload = (
  response: InvestmentAmountResponse | RecommendationResponse,
): RecommendationResponse | null => {
  if ('data' in response) {
    return response.data;
  }

  return response;
};

const getInvestmentTypeOptionsPayload = (
  response:
    | AllocationMasterListResponse
    | AllocationMasterItem[]
    | InvestmentTypeOptionsResponse
    | InvestmentTypeOptionsData
    | InvestmentTypeOption[]
    | undefined,
): InvestmentTypeOption[] => {
  if (!response) {
    return [];
  }

  if (Array.isArray(response)) {
    if (!response.length) {
      return [];
    }

    if ('type' in response[0]) {
      return response as InvestmentTypeOption[];
    }

    return (response as AllocationMasterItem[])
      .map(toOption)
      .filter((option): option is InvestmentTypeOption => Boolean(option))
      .sort((a, b) => INVESTMENT_TYPE_ORDER[a.type] - INVESTMENT_TYPE_ORDER[b.type]);
  }

  if ('data' in response) {
    const data = response.data;

    if (Array.isArray(data)) {
      if (!data.length) {
        return [];
      }

      if ('type' in data[0]) {
        return data as InvestmentTypeOption[];
      }

      return (data as AllocationMasterItem[])
        .map(toOption)
        .filter((option): option is InvestmentTypeOption => Boolean(option))
        .sort((a, b) => INVESTMENT_TYPE_ORDER[a.type] - INVESTMENT_TYPE_ORDER[b.type]);
    }

    if (data && Array.isArray(data.options)) {
      return data.options;
    }
  }

  if ('options' in response && Array.isArray(response.options)) {
    return response.options;
  }

  return [];
};

export const useInvestmentAmount = () => {
  const {
    data: investmentTypeOptionsResponse,
    isLoading: isLoadingTypeOptions,
    isError: isTypeOptionsError,
  } = useGetInvestmentTypeOptionsQuery();
  const [getRecommendation, { isLoading, error }] = useGetRecommendationMutation();

  const investmentTypeOptions = getInvestmentTypeOptionsPayload(investmentTypeOptionsResponse);

  const submitInvestmentAmount = async (request: InvestmentAmountRequest): Promise<RecommendationResponse | null> => {
    const response = await getRecommendation(request).unwrap();

    return getRecommendationPayload(response);
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