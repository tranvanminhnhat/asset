export interface InvestmentCompleteAllocation {
  type: string;
  amount: number;
  quantity: number;
  percent: number;
}

export interface InvestmentCompleteRequest {
  amount: number;
  type: string;
  allocations: InvestmentCompleteAllocation[];
}

export interface InvestmentCompleteResponse {
  sessionId: string;
  planId: number;
  userId: string;
  type: string;
  totalAmount: number;
  status?: string;
  recommendationSnapshot?: string | null;
  createdAt: string;
  allocations: InvestmentCompleteAllocation[];
}

export interface InvestmentCompleteApiResponse {
  data: InvestmentCompleteResponse;
  message: {
    code: string;
    message: string;
    service: string;
  };
}
