// Admin Asset Price Types
// Matches BE: admin.dto.request.CreateAssetPriceRequest & admin.dto.response.AssetPriceResponse

export interface AssetPriceAdminResponse {
  id: number;
  productId: number;
  buyPrice: number;
  sellPrice: number;
  priceDate: string | null;
  isLatest: boolean | null;
  isActive: boolean | null;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface CreateAssetPriceRequest {
  productId: number;
  buyPrice: number;
  sellPrice: number;
  priceDate: string | null;
}

export interface UpdateAssetPriceRequest {
  id: number;
  productId: number;
  buyPrice: number;
  sellPrice: number;
  priceDate: string | null;
}

// Admin Asset Product Types
export interface AssetProductResponse {
  id: number;
  code: string;
  name: string;
  categoryId: number | null;
  platformId: number | null;
  minimumQuantity: number | null;
  unitName: string | null;
  description: string | null;
  isActive: boolean | null;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface CreateAssetProductRequest {
  code: string;
  name: string;
  categoryId: number | null;
  platformId: number | null;
  minimumQuantity: number | null;
  unitName: string | null;
  description: string | null;
}

export interface UpdateAssetProductRequest {
  id: number;
  code: string;
  name: string;
  categoryId: number | null;
  platformId: number | null;
  minimumQuantity: number | null;
  unitName: string | null;
  description: string | null;
}

// Admin Asset Category Types
export interface AssetCategoryResponse {
  id: number;
  code: string;
  name: string;
  description: string | null;
  isActive: boolean | null;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface CreateAssetCategoryRequest {
  code: string;
  name: string;
  description: string | null;
}

export interface UpdateAssetCategoryRequest {
  id: number;
  code: string;
  name: string;
  description: string | null;
}

// Admin Asset Platform Types
export interface AssetPlatformResponse {
  id: number;
  code: string;
  name: string;
  description: string | null;
  isActive: boolean | null;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface CreateAssetPlatformRequest {
  code: string;
  name: string;
  description: string | null;
}

export interface UpdateAssetPlatformRequest {
  id: number;
  code: string;
  name: string;
  description: string | null;
}

export interface ApiMessage {
  code: string;
  message: string;
  service: string;
}

export interface AdminApiResponse<T> {
  data: T;
  message?: ApiMessage | string;
  statusCode?: number;
}
