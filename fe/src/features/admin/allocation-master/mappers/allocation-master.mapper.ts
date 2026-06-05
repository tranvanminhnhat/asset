import { ApiResponse, AllocationMasterResponse } from '../type/allocation-master.type';

const getPayload = <T>(response?: ApiResponse<T> | T): T | undefined => {
  if (!response) return undefined;
  if (typeof response === 'object' && response !== null && 'data' in response) {
    return (response as ApiResponse<T>).data;
  }
  return response as T;
};

export const toAllocationMasterList = (
  response?: ApiResponse<AllocationMasterResponse[]> | AllocationMasterResponse[],
): AllocationMasterResponse[] => {
  const payload = getPayload(response);
  if (!Array.isArray(payload)) return [];
  return payload;
};

export const toAllocationMasterDetail = (
  response?: ApiResponse<AllocationMasterResponse> | AllocationMasterResponse,
): AllocationMasterResponse | null => {
  const payload = getPayload(response);
  if (!payload) return null;
  return payload;
};
