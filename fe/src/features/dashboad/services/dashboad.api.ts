import { baseApi } from "@/store/api/baseApi";
import {
  ApiResponse,
  DashboardResponse,
  TransactionRequest,
  TransactionResponse,
} from "../type/dashboard.type";

export const dashboardApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getDashboard: builder.query<ApiResponse<DashboardResponse>, void>({
      query: () => ({
        url: "/dashboard",
        method: "GET",
      }),
    }),
    getTransactions: builder.query<ApiResponse<TransactionResponse[]>, number>({
      query: (portfolioId) => ({
        url: `/api/v1/portfolios/${portfolioId}/transactions`,
        method: "GET",
      }),
    }),
    addTransaction: builder.mutation<ApiResponse<TransactionResponse>, TransactionRequest>({
      query: (body) => ({
        url: `/api/v1/portfolios/${body.portfolioId}/transactions`,
        method: "POST",
        body,
      }),
    }),
  }),
});

export const {
  useAddTransactionMutation,
  useGetDashboardQuery,
  useGetTransactionsQuery,
} = dashboardApi;
