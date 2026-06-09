// Investment Amount Types

// Session-based investment workflow
export interface CreateSessionRequest {
  strategyCode: string;
  totalAmount: number;
}

export interface CreateSessionResponse {
  sessionId: number;
  userId: string;
  strategyCode: string;
  totalAmount: number;
  status: string;
  currentStep: string;
  createdAt: string;
}

export interface CreateSessionApiResponse {
  data: CreateSessionResponse;
  message?: {
    code: string;
    message: string;
    service: string;
  };
}

export interface InvestmentTypeOption {
  type: string;
  label: string;
  description: string;
}

export interface AllocationMasterDetail {
  id: number;
  strategyId: number;
  assetType: string;
  percentage: number;
  minimumLotSizeAmount: number;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface AllocationMasterItem {
  id: number;
  code: string;
  name: string;
  createdAt: string | null;
  updatedAt: string | null;
  details: AllocationMasterDetail[];
}

export interface AllocationMasterListResponse {
  data: AllocationMasterItem[];
  message?: {
    code: string;
    message: string;
    service: string;
  };
}


