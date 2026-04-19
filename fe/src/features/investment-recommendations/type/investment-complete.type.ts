export interface InvestmentCompleteAllocation {
  type: string;
  amount: number;
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
  userId: number;
  type: string;
  totalAmount: number;
  status: string;
  recommendationSnapshot: string;
  note: string;
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
