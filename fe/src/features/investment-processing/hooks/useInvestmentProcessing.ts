import { useState } from 'react';
import {
  useGetProductsByAssetTypeQuery,
  useSelectAssetForAllocationMutation,
  useCompleteAllocationMutation,
} from '../services/investment-processing.api';
import { AssetPrice, CompleteAllocationResponse, SelectAssetRequest, SelectAssetResponse } from '../type/investment-processing.type';

interface UseInvestmentProcessingParams {
  sessionId: number;
  allocationId: number;
  assetType: string;
}

export const useInvestmentProcessing = ({ sessionId, allocationId, assetType }: UseInvestmentProcessingParams) => {
  const {
    data: productsResponse,
    isLoading: isLoadingAssets,
    isError: isAssetsError,
  } = useGetProductsByAssetTypeQuery(assetType);

  const [selectAsset, { isLoading: isSelecting, isError: isSelectError, error: selectError }] =
    useSelectAssetForAllocationMutation();
  const [completeAllocation, { isLoading: isCompleting, isError: isCompleteError }] =
    useCompleteAllocationMutation();

  const [selectResult, setSelectResult] = useState<SelectAssetResponse | null>(null);

  const assets: AssetPrice[] = (productsResponse?.data ?? []).map((p) => ({
    id: p.productId,
    productId: p.productId,
    assetCode: p.assetCode,
    assetType: p.assetType,
    assetName: p.assetName,
    buyPrice: p.buyPrice,
    sellPrice: p.sellPrice,
    priceDate: null,
    isLatest: true,
    isActive: true,
  }));

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
