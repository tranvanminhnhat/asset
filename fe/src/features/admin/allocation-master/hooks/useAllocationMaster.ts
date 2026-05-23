import { useMemo } from 'react';
import {
  useGetAllocationMastersQuery,
  useCreateAllocationMasterMutation,
  useUpdateAllocationMasterMutation,
  useDeleteAllocationMasterMutation,
  useLazyGetAllocationMasterByIdQuery,
} from '../services/allocation-master.api';
import { toAllocationMasterList } from '../mappers/allocation-master.mapper';

export const useAllocationMaster = () => {
  const query = useGetAllocationMastersQuery();
  const [createAllocationMaster, createResult] = useCreateAllocationMasterMutation();
  const [updateAllocationMaster, updateResult] = useUpdateAllocationMasterMutation();
  const [deleteAllocationMaster, deleteResult] = useDeleteAllocationMasterMutation();
  const [triggerGetById, getByIdResult] = useLazyGetAllocationMasterByIdQuery();

  const allocationMasters = useMemo(
    () => toAllocationMasterList(query.data),
    [query.data],
  );

  return {
    allocationMasters,
    isLoading: query.isLoading,
    error: query.error,
    createAllocationMaster,
    createResult,
    updateAllocationMaster,
    updateResult,
    deleteAllocationMaster,
    deleteResult,
    triggerGetById,
    getByIdResult,
  };
};
