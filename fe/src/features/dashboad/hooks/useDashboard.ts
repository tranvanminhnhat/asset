import { useGetDashboardQuery } from "../services/dashboad.api";

export const useDashboard = () => {
  const { data, isLoading, error } = useGetDashboardQuery();

  return {
    dashboardData: data?.data,
    isLoading,
    error,
  };
};