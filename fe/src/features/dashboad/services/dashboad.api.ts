import { baseApi } from "@/store/api/baseApi";
import { DashboardResponse } from "../type/dashboard.type";

export const dashboardApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getDashboard: builder.query<DashboardResponse, void>({
      query: () => ({
        url: "/dashboard",
        method: "GET",
      }),
    }),
  }),
});

export const { useGetDashboardQuery } = dashboardApi;
