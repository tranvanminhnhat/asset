export interface ApiMessage {
  code: string;
  message: string;
  service: string;
}

export interface ApiResponse<T> {
  data: T;
  message?: ApiMessage | string;
  statusCode?: number;
  timestamp?: string;
}

export interface AllocationMasterDetailResponse {
  id: number;
  strategyId: number;
  assetType: string;
  percentage: number;
  minimumLotSizeAmount: number;
  createdAt: string;
  updatedAt: string;
}

export interface AllocationMasterResponse {
  id: number;
  code: string;
  name: string;
  createdAt: string;
  updatedAt: string;
  details: AllocationMasterDetailResponse[];
}

export interface AllocationMasterDetailRequest {
  assetType: string;
  percentage: number;
  minimumLotSizeAmount: number;
}

export interface AllocationMasterRequest {
  code: string;
  name?: string;
  details: AllocationMasterDetailRequest[];
}
