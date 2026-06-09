import { baseApi } from '@/store/api/baseApi';
import {
  AdminApiResponse,
  AssetPriceAdminResponse,
  AssetProductResponse,
  AssetCategoryResponse,
  AssetPlatformResponse,
  CreateAssetPriceRequest,
  UpdateAssetPriceRequest,
  CreateAssetProductRequest,
  UpdateAssetProductRequest,
  CreateAssetCategoryRequest,
  UpdateAssetCategoryRequest,
  CreateAssetPlatformRequest,
  UpdateAssetPlatformRequest,
} from '../type/asset-management.type';

export const assetManagementApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    // ── Asset Price ──────────────────────────────────────────
    getAssetPriceById: builder.query<AdminApiResponse<AssetPriceAdminResponse>, number>({
      query: (id) => ({ url: `/api/v1/admin/price/${id}`, method: 'GET' }),
      providesTags: ['AssetPrice'],
    }),
    createAssetPrice: builder.mutation<AdminApiResponse<AssetPriceAdminResponse>, CreateAssetPriceRequest>({
      query: (body) => ({ url: '/api/v1/admin/price', method: 'POST', body }),
      invalidatesTags: ['AssetPrice'],
    }),
    updateAssetPrice: builder.mutation<AdminApiResponse<AssetPriceAdminResponse>, UpdateAssetPriceRequest>({
      query: (body) => ({ url: '/api/v1/admin/price', method: 'PUT', body }),
      invalidatesTags: ['AssetPrice'],
    }),
    deleteAssetPrice: builder.mutation<AdminApiResponse<void>, number>({
      query: (id) => ({ url: `/api/v1/admin/price/${id}`, method: 'DELETE' }),
      invalidatesTags: ['AssetPrice'],
    }),

    // ── Asset Product ─────────────────────────────────────────
    getAssetProductById: builder.query<AdminApiResponse<AssetProductResponse>, number>({
      query: (id) => ({ url: `/api/v1/admin/product/${id}`, method: 'GET' }),
      providesTags: ['AssetProduct'],
    }),
    createAssetProduct: builder.mutation<AdminApiResponse<AssetProductResponse>, CreateAssetProductRequest>({
      query: (body) => ({ url: '/api/v1/admin/product', method: 'POST', body }),
      invalidatesTags: ['AssetProduct'],
    }),
    updateAssetProduct: builder.mutation<AdminApiResponse<AssetProductResponse>, UpdateAssetProductRequest>({
      query: (body) => ({ url: '/api/v1/admin/product', method: 'PUT', body }),
      invalidatesTags: ['AssetProduct'],
    }),
    deleteAssetProduct: builder.mutation<AdminApiResponse<void>, number>({
      query: (id) => ({ url: `/api/v1/admin/product/${id}`, method: 'DELETE' }),
      invalidatesTags: ['AssetProduct'],
    }),

    // ── Asset Category ────────────────────────────────────────
    getAssetCategoryById: builder.query<AdminApiResponse<AssetCategoryResponse>, number>({
      query: (id) => ({ url: `/api/v1/admin/category/${id}`, method: 'GET' }),
      providesTags: ['AssetCategory'],
    }),
    createAssetCategory: builder.mutation<AdminApiResponse<AssetCategoryResponse>, CreateAssetCategoryRequest>({
      query: (body) => ({ url: '/api/v1/admin/category', method: 'POST', body }),
      invalidatesTags: ['AssetCategory'],
    }),
    updateAssetCategory: builder.mutation<AdminApiResponse<AssetCategoryResponse>, UpdateAssetCategoryRequest>({
      query: (body) => ({ url: '/api/v1/admin/category', method: 'PUT', body }),
      invalidatesTags: ['AssetCategory'],
    }),
    deleteAssetCategory: builder.mutation<AdminApiResponse<void>, number>({
      query: (id) => ({ url: `/api/v1/admin/category/${id}`, method: 'DELETE' }),
      invalidatesTags: ['AssetCategory'],
    }),

    // ── Asset Platform ────────────────────────────────────────
    getAssetPlatformById: builder.query<AdminApiResponse<AssetPlatformResponse>, number>({
      query: (id) => ({ url: `/api/v1/admin/platform/${id}`, method: 'GET' }),
      providesTags: ['AssetPlatform'],
    }),
    createAssetPlatform: builder.mutation<AdminApiResponse<AssetPlatformResponse>, CreateAssetPlatformRequest>({
      query: (body) => ({ url: '/api/v1/admin/platform', method: 'POST', body }),
      invalidatesTags: ['AssetPlatform'],
    }),
    updateAssetPlatform: builder.mutation<AdminApiResponse<AssetPlatformResponse>, UpdateAssetPlatformRequest>({
      query: (body) => ({ url: '/api/v1/admin/platform', method: 'PUT', body }),
      invalidatesTags: ['AssetPlatform'],
    }),
    deleteAssetPlatform: builder.mutation<AdminApiResponse<void>, number>({
      query: (id) => ({ url: `/api/v1/admin/platform/${id}`, method: 'DELETE' }),
      invalidatesTags: ['AssetPlatform'],
    }),
  }),
});

export const {
  useGetAssetPriceByIdQuery,
  useLazyGetAssetPriceByIdQuery,
  useCreateAssetPriceMutation,
  useUpdateAssetPriceMutation,
  useDeleteAssetPriceMutation,
  useGetAssetProductByIdQuery,
  useLazyGetAssetProductByIdQuery,
  useCreateAssetProductMutation,
  useUpdateAssetProductMutation,
  useDeleteAssetProductMutation,
  useGetAssetCategoryByIdQuery,
  useLazyGetAssetCategoryByIdQuery,
  useCreateAssetCategoryMutation,
  useUpdateAssetCategoryMutation,
  useDeleteAssetCategoryMutation,
  useGetAssetPlatformByIdQuery,
  useLazyGetAssetPlatformByIdQuery,
  useCreateAssetPlatformMutation,
  useUpdateAssetPlatformMutation,
  useDeleteAssetPlatformMutation,
} = assetManagementApi;
