import { useMemo } from 'react';
import {
  useGetDashboardQuery,
} from '../services/dashboad.api';
import { toDashboardPayload } from '../mappers/dashboard.mapper';

export const useDashboard = () => {
  const dashboardQuery = useGetDashboardQuery();
  const dashboardData = useMemo(() => toDashboardPayload(dashboardQuery.data), [dashboardQuery.data]);

  return {
    dashboardData,
    isLoading: dashboardQuery.isLoading,
    error: dashboardQuery.error,
  };
};