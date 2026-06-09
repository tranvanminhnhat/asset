import { useState } from 'react';
import {
  useGetAvailableAssetsQuery,
  useSelectAssetForAllocationMutation,
  useCompleteAllocationMutation,
} from '../services/investment-processing.api';
import { CompleteAllocationResponse, SelectAssetRequest, SelectAssetResponse } from '../type/investment-processing.type';

interface UseInvestmentProcessingParams {
  sessionId: number;
  allocationId: number;
  assetType: string;
}

export const useInvestmentProcessing = ({ sessionId, allocationId, assetType }: UseInvestmentProcessingParams) => {
  const {
    data: assetsResponse,
    isLoading: isLoadingAssets,
    isError: isAssetsError,
  } = useGetAvailableAssetsQuery(assetType);

  const [selectAsset, { isLoading: isSelecting, isError: isSelectError, error: selectError }] =
    useSelectAssetForAllocationMutation();
  const [completeAllocation, { isLoading: isCompleting, isError: isCompleteError }] =
    useCompleteAllocationMutation();

  const [selectResult, setSelectResult] = useState<SelectAssetResponse | null>(null);

  const assets = assetsResponse?.data?.filter((a) => a.isActive) ?? [];

  const submitSelect = async (request: SelectAssetRequest): Promise<SelectAssetResponse | null> => {
    const response = await selectAsset({ sessionId, allocationId, body: request }).unwrap();
    const result = response.data ?? null;
    setSelectResult(result);
    return result;
  };

  const submitComplete = async (): Promise<CompleteAllocationResponse | null> => {
    const response = await completeAllocation({ sessionId, allocationId }).unwrap();
    return response.data ?? null;
  };

  return {
    assets,
    isLoadingAssets,
    isAssetsError,
    selectResult,
    isSelecting,
    isSelectError,
    selectError,
    isCompleting,
    isCompleteError,
    submitSelect,
    submitComplete,
  };
};
