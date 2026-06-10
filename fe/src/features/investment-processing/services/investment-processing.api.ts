import { baseApi } from '@/store/api/baseApi';
import {
  AssetsApiResponse,
  CompleteAllocationApiResponse,
  ProductsApiResponse,
  SelectAssetApiResponse,
  SelectAssetRequest,
} from '../type/investment-processing.type';

export const investmentProcessingApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getProductsByAssetType: builder.query<ProductsApiResponse, string>({
      query: (type) => ({
        url: `/api/v1/products?type=${type}`,
        method: 'GET',
      }),
    }),
    getAvailableAssets: builder.query<AssetsApiResponse, string>({
      query: (type) => ({
        url: `/api/v1/assets?type=${type}`,
        method: 'GET',
      }),
    }),
    selectAssetForAllocation: builder.mutation<
      SelectAssetApiResponse,
      { sessionId: number; allocationId: number; body: SelectAssetRequest }
    >({
      query: ({ sessionId, allocationId, body }) => ({
        url: `/api/v1/investment/${sessionId}/allocation/${allocationId}/select`,
        method: 'POST',
        body,
      }),
    }),
    completeAllocation: builder.mutation<
      CompleteAllocationApiResponse,
      { sessionId: number; allocationId: number }
    >({
      query: ({ sessionId, allocationId }) => ({
        url: `/api/v1/investment/${sessionId}/allocation/${allocationId}/complete`,
        method: 'POST',
      }),
      invalidatesTags: (_result, _error, { sessionId }) => [{ type: 'SessionDetail', id: sessionId }],
    }),
  }),
});

export const {
  useGetProductsByAssetTypeQuery,
  useGetAvailableAssetsQuery,
  useSelectAssetForAllocationMutation,
  useCompleteAllocationMutation,
} = investmentProcessingApi;
