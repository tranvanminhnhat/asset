'use client';
import React, { useEffect, useState } from 'react';
import { useAllocationMaster } from '../hooks/useAllocationMaster';
import AllocationMasterTable from '../components/AllocationMasterTable';
import AllocationMasterFormModal from '../components/AllocationMasterFormModal';
import AllocationMasterDeleteModal from '../components/AllocationMasterDeleteModal';
import {
  AllocationMasterResponse,
  AllocationMasterRequest,
  ApiMessage,
} from '../type/allocation-master.type';
import Button from '@/common/components/ui/Button';
import PageHeader from '@/common/components/ui/PageHeader';
import PageLoader from '@/common/components/ui/PageLoader';
import Toast from '@/common/components/ui/Toast';
import { ToastType } from '@/common/components/ui/Toast';

interface ToastState {
  type: ToastType;
  message: string;
}

const getMessageText = (message?: ApiMessage | string): string => {
  if (!message) return '';
  if (typeof message === 'string') return message;
  return message.message ?? '';
};

const AllocationMasterPage: React.FC = () => {
  const {
    allocationMasters,
    isLoading,
    error,
    createAllocationMaster,
    createResult,
    updateAllocationMaster,
    updateResult,
    deleteAllocationMaster,
    deleteResult,
    triggerGetById,
    getByIdResult,
  } = useAllocationMaster();

  const [formOpen, setFormOpen] = useState(false);
  const [editTarget, setEditTarget] = useState<AllocationMasterResponse | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<AllocationMasterResponse | null>(null);
  const [toast, setToast] = useState<ToastState | null>(null);

  // Xá»­ lÃ½ káº¿t quáº£ create
  useEffect(() => {
    if (createResult.isSuccess && createResult.data) {
      const msg = getMessageText(createResult.data.message);
      setToast({ type: 'success', message: msg || 'Táº¡o má»›i thÃ nh cÃ´ng!' });
      setFormOpen(false);
      setEditTarget(null);
    }
    if (createResult.isError) {
      setToast({ type: 'error', message: 'Táº¡o má»›i tháº¥t báº¡i. Vui lÃ²ng thá»­ láº¡i.' });
    }
  }, [createResult.isSuccess, createResult.isError, createResult.data]);

  // Láº¥y dá»¯ liá»‡u chi tiáº¿t khi edit, má»Ÿ modal sau khi fetch xong
  useEffect(() => {
    if (getByIdResult.isSuccess && getByIdResult.data) {
      const detail = getByIdResult.data.data ?? getByIdResult.data;
      setEditTarget(detail as AllocationMasterResponse);
      setFormOpen(true);
    }
  }, [getByIdResult.isSuccess, getByIdResult.data]);

  // Xá»­ lÃ½ káº¿t quáº£ update
  useEffect(() => {
    if (updateResult.isSuccess && updateResult.data) {
      const msg = getMessageText(updateResult.data.message);
      setToast({ type: 'success', message: msg || 'Cáº­p nháº­t thÃ nh cÃ´ng!' });
      setFormOpen(false);
      setEditTarget(null);
    }
    if (updateResult.isError) {
      setToast({ type: 'error', message: 'Cáº­p nháº­t tháº¥t báº¡i. Vui lÃ²ng thá»­ láº¡i.' });
    }
  }, [updateResult.isSuccess, updateResult.isError, updateResult.data]);

  // Xá»­ lÃ½ káº¿t quáº£ delete
  useEffect(() => {
    if (deleteResult.isSuccess && deleteResult.data) {
      const msg = getMessageText(deleteResult.data.message);
      setToast({ type: 'success', message: msg || 'XÃ³a thÃ nh cÃ´ng!' });
      setDeleteTarget(null);
    }
    if (deleteResult.isError) {
      setToast({ type: 'error', message: 'XÃ³a tháº¥t báº¡i. Vui lÃ²ng thá»­ láº¡i.' });
    }
  }, [deleteResult.isSuccess, deleteResult.isError, deleteResult.data]);

  const handleCreate = () => {
    setEditTarget(null);
    setFormOpen(true);
  };

  const handleEdit = (item: AllocationMasterResponse) => {
    triggerGetById(item.id);
  };

  const handleDelete = (item: AllocationMasterResponse) => {
    setDeleteTarget(item);
  };

  const handleFormSubmit = async (data: AllocationMasterRequest) => {
    if (editTarget) {
      await updateAllocationMaster({ id: editTarget.id, body: data });
    } else {
      await createAllocationMaster(data);
    }
  };

  const handleDeleteConfirm = async (id: number) => {
    await deleteAllocationMaster(id);
  };

  return (
    <div className="px-6 py-7 animate-fade-up">
      {/* Toast */}
      {toast && (
        <Toast
          type={toast.type}
          message={toast.message}
          onDismiss={() => setToast(null)}
        />
      )}

      {/* Loading overlay for getById */}
      {getByIdResult.isFetching && (
        <div className="fixed inset-0 z-40 bg-slate-900/20 backdrop-blur-[1px] flex items-center justify-center">
          <div className="bg-white rounded-xl px-6 py-4 shadow-xl flex items-center gap-3">
            <svg className="animate-spin w-5 h-5 text-emerald-600" fill="none" viewBox="0 0 24 24">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
            </svg>
            <span className="text-sm font-medium text-slate-700">Đang tải dữ liệu...</span>
          </div>
        </div>
      )}

      <PageHeader
        title="Allocation Master"
        description="Quản lý danh sách cấu hình phân bổ tài sản"
        actions={
          <Button
            onClick={handleCreate}
            leftIcon={
              <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M12 4v16m8-8H4" />
              </svg>
            }
          >
            Tạo mới
          </Button>
        }
      />

      {/* Stats row */}
      <div className="grid grid-cols-3 gap-4 mb-6">
        <div className="bg-white rounded-xl border border-slate-200 px-5 py-4 shadow-sm">
          <p className="text-xs font-medium text-slate-500 uppercase tracking-wider mb-1">Tổng bản ghi</p>
          <p className="text-2xl font-bold text-slate-900">{allocationMasters.length}</p>
        </div>
        <div className="bg-white rounded-xl border border-slate-200 px-5 py-4 shadow-sm">
          <p className="text-xs font-medium text-slate-500 uppercase tracking-wider mb-1">Đang tải</p>
          <p className="text-2xl font-bold text-slate-900">{isLoading ? '...' : 'Xong'}</p>
        </div>
        <div className="bg-white rounded-xl border border-slate-200 px-5 py-4 shadow-sm">
          <p className="text-xs font-medium text-slate-500 uppercase tracking-wider mb-1">Trạng thái</p>
          <p className="text-sm font-semibold text-emerald-600 mt-1">
            {error ? 'Lỗi' : 'Hoạt động'}
          </p>
        </div>
      </div>

      {/* Table area */}
      {isLoading ? (
        <PageLoader />
      ) : error ? (
        <div className="bg-red-50 border border-red-200 rounded-xl p-6 text-center">
          <p className="text-sm font-medium text-red-700">Có lỗi xảy ra khi tải dữ liệu Allocation Master.</p>
        </div>
      ) : (
        <AllocationMasterTable
          data={allocationMasters}
          onEdit={handleEdit}
          onDelete={handleDelete}
        />
      )}

      <AllocationMasterFormModal
        open={formOpen}
        initialData={editTarget}
        onClose={() => {
          setFormOpen(false);
          setEditTarget(null);
        }}
        onSubmit={handleFormSubmit}
        isSubmitting={createResult.isLoading || updateResult.isLoading || getByIdResult.isFetching}
      />

      <AllocationMasterDeleteModal
        open={!!deleteTarget}
        target={deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleDeleteConfirm}
        isDeleting={deleteResult.isLoading}
      />
    </div>
  );
};

export default AllocationMasterPage;
