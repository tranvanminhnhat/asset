import { baseApi } from '@/store/api/baseApi';
import {
  ApiResponse,
  AllocationMasterResponse,
  AllocationMasterRequest,
} from '../type/allocation-master.type';

export const allocationMasterApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getAllocationMasters: builder.query<ApiResponse<AllocationMasterResponse[]>, void>({
      query: () => ({
        url: '/api/v1/allocation-master',
        method: 'GET',
      }),
      providesTags: ['AllocationMaster'],
    }),
    getAllocationMasterById: builder.query<ApiResponse<AllocationMasterResponse>, number>({
      query: (id) => ({
        url: `/api/v1/allocation-master/${id}`,
        method: 'GET',
      }),
      providesTags: ['AllocationMaster'],
    }),
    createAllocationMaster: builder.mutation<ApiResponse<AllocationMasterResponse>, AllocationMasterRequest>({
      query: (body) => ({
        url: '/api/v1/allocation-master',
        method: 'POST',
        body,
      }),
      invalidatesTags: ['AllocationMaster'],
    }),
    updateAllocationMaster: builder.mutation<
      ApiResponse<AllocationMasterResponse>,
      { id: number; body: AllocationMasterRequest }
    >({
      query: ({ id, body }) => ({
        url: `/api/v1/allocation-master/${id}`,
        method: 'PUT',
        body,
      }),
      invalidatesTags: ['AllocationMaster'],
    }),
    deleteAllocationMaster: builder.mutation<ApiResponse<null>, number>({
      query: (id) => ({
        url: `/api/v1/allocation-master/${id}`,
        method: 'DELETE',
      }),
      invalidatesTags: ['AllocationMaster'],
    }),
  }),
});

export const {
  useGetAllocationMastersQuery,
  useGetAllocationMasterByIdQuery,
  useLazyGetAllocationMasterByIdQuery,
  useCreateAllocationMasterMutation,
  useUpdateAllocationMasterMutation,
  useDeleteAllocationMasterMutation,
} = allocationMasterApi;
