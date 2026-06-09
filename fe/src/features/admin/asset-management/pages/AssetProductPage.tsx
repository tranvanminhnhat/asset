'use client';
import React, { useState } from 'react';
import PageHeader from '@/common/components/ui/PageHeader';
import Button from '@/common/components/ui/Button';
import Toast, { ToastType } from '@/common/components/ui/Toast';
import {
  AssetProductResponse,
  CreateAssetProductRequest,
  UpdateAssetProductRequest,
} from '../type/asset-management.type';
import {
  useLazyGetAssetProductByIdQuery,
  useCreateAssetProductMutation,
  useUpdateAssetProductMutation,
  useDeleteAssetProductMutation,
} from '../services/asset-management.api';

interface ToastState { type: ToastType; message: string; }

const AssetProductPage: React.FC = () => {
  const [searchId, setSearchId] = useState('');
  const [items, setItems] = useState<AssetProductResponse[]>([]);
  const [formOpen, setFormOpen] = useState(false);
  const [editItem, setEditItem] = useState<AssetProductResponse | null>(null);
  const [toast, setToast] = useState<ToastState | null>(null);

  const [fetchById, { isFetching }] = useLazyGetAssetProductByIdQuery();
  const [createProduct, { isLoading: isCreating }] = useCreateAssetProductMutation();
  const [updateProduct, { isLoading: isUpdating }] = useUpdateAssetProductMutation();
  const [deleteProduct, { isLoading: isDeleting }] = useDeleteAssetProductMutation();

  const showToast = (type: ToastType, message: string) => setToast({ type, message });

  const handleSearch = async () => {
    const id = Number(searchId);
    if (!id) return;
    try {
      const res = await fetchById(id).unwrap();
      if (res.data) {
        setItems((prev) => {
          const exists = prev.find((i) => i.id === res.data.id);
          if (exists) return prev.map((i) => (i.id === res.data.id ? res.data : i));
          return [...prev, res.data];
        });
        showToast('success', 'Tìm thấy sản phẩm');
      }
    } catch {
      showToast('error', 'Không tìm thấy ID này');
    }
  };

  const handleCreate = async (form: CreateAssetProductRequest) => {
    try {
      const res = await createProduct(form).unwrap();
      if (res.data) {
        setItems((prev) => [res.data, ...prev]);
        showToast('success', 'Tạo sản phẩm thành công!');
        setFormOpen(false);
      }
    } catch {
      showToast('error', 'Tạo thất bại. Vui lòng thử lại.');
    }
  };

  const handleUpdate = async (form: UpdateAssetProductRequest) => {
    try {
      const res = await updateProduct(form).unwrap();
      if (res.data) {
        setItems((prev) => prev.map((i) => (i.id === res.data.id ? res.data : i)));
        showToast('success', 'Cập nhật thành công!');
        setFormOpen(false);
        setEditItem(null);
      }
    } catch {
      showToast('error', 'Cập nhật thất bại. Vui lòng thử lại.');
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Bạn có chắc chắn muốn xoá?')) return;
    try {
      await deleteProduct(id).unwrap();
      setItems((prev) => prev.filter((i) => i.id !== id));
      showToast('success', 'Xoá thành công!');
    } catch {
      showToast('error', 'Xoá thất bại. Vui lòng thử lại.');
    }
  };

  return (
    <div className="p-6 animate-fade-up">
      <PageHeader
        title="Quản lý Sản phẩm"
        description="CRUD sản phẩm tài sản (POST /api/v1/admin/product)"
        actions={
          <Button variant="primary" size="sm" onClick={() => { setEditItem(null); setFormOpen(true); }}>
            + Thêm sản phẩm
          </Button>
        }
      />

      <div className="flex gap-2 mb-6">
        <input
          type="number" value={searchId} onChange={(e) => setSearchId(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
          placeholder="Tìm theo ID..."
          className="border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500/30 focus:border-emerald-500 w-48"
        />
        <Button variant="secondary" size="sm" onClick={handleSearch} disabled={isFetching}>
          {isFetching ? 'Đang tìm...' : 'Tìm kiếm'}
        </Button>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden mb-6">
        <div className="overflow-x-auto">
          <table className="min-w-full">
            <thead>
              <tr className="bg-slate-900">
                {['ID', 'Code', 'Tên', 'Category', 'Platform', 'Đơn vị', 'Active', 'Thao tác'].map((h) => (
                  <th key={h} className="px-4 py-3.5 text-left text-[11px] font-semibold text-slate-400 uppercase tracking-wider whitespace-nowrap">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {items.length === 0 ? (
                <tr><td colSpan={8} className="px-4 py-8 text-center text-sm text-slate-400">Chưa có dữ liệu. Tìm theo ID hoặc tạo mới.</td></tr>
              ) : items.map((item) => (
                <tr key={item.id} className="hover:bg-slate-50/60 transition-colors">
                  <td className="px-4 py-3 text-sm text-slate-500 font-mono">#{item.id}</td>
                  <td className="px-4 py-3"><span className="inline-flex items-center px-2.5 py-1 rounded-md bg-slate-100 text-slate-700 text-xs font-semibold font-mono">{item.code}</span></td>
                  <td className="px-4 py-3 text-sm font-medium text-slate-900">{item.name}</td>
                  <td className="px-4 py-3 text-sm text-slate-500">{item.categoryId ?? '—'}</td>
                  <td className="px-4 py-3 text-sm text-slate-500">{item.platformId ?? '—'}</td>
                  <td className="px-4 py-3 text-sm text-slate-500">{item.unitName ?? '—'}</td>
                  <td className="px-4 py-3">
                    <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${item.isActive ? 'bg-emerald-100 text-emerald-700' : 'bg-slate-100 text-slate-500'}`}>
                      {item.isActive ? 'Hoạt động' : 'Tắt'}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex items-center gap-1.5">
                      <button onClick={() => { setEditItem(item); setFormOpen(true); }} className="px-2.5 py-1.5 text-xs rounded-lg font-medium bg-blue-50 text-blue-700 hover:bg-blue-100 border border-blue-200/80 transition-colors">Sửa</button>
                      <button onClick={() => handleDelete(item.id)} disabled={isDeleting} className="px-2.5 py-1.5 text-xs rounded-lg font-medium bg-red-50 text-red-700 hover:bg-red-100 border border-red-200/80 transition-colors">Xoá</button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {formOpen && (
        <AssetProductFormModal
          item={editItem}
          onClose={() => { setFormOpen(false); setEditItem(null); }}
          onCreate={handleCreate}
          onUpdate={handleUpdate}
          isLoading={isCreating || isUpdating}
        />
      )}

      {toast && <Toast type={toast.type} message={toast.message} onDismiss={() => setToast(null)} />}
    </div>
  );
};

interface FormModalProps {
  item: AssetProductResponse | null;
  onClose: () => void;
  onCreate: (req: CreateAssetProductRequest) => void;
  onUpdate: (req: UpdateAssetProductRequest) => void;
  isLoading: boolean;
}

const AssetProductFormModal: React.FC<FormModalProps> = ({ item, onClose, onCreate, onUpdate, isLoading }) => {
  const [code, setCode] = useState(item?.code ?? '');
  const [name, setName] = useState(item?.name ?? '');
  const [categoryId, setCategoryId] = useState(String(item?.categoryId ?? ''));
  const [platformId, setPlatformId] = useState(String(item?.platformId ?? ''));
  const [minimumQuantity, setMinimumQuantity] = useState(String(item?.minimumQuantity ?? ''));
  const [unitName, setUnitName] = useState(item?.unitName ?? '');
  const [description, setDescription] = useState(item?.description ?? '');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const base = { code, name, categoryId: categoryId ? Number(categoryId) : null, platformId: platformId ? Number(platformId) : null, minimumQuantity: minimumQuantity ? Number(minimumQuantity) : null, unitName: unitName || null, description: description || null };
    if (item) onUpdate({ id: item.id, ...base });
    else onCreate(base);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md p-6 max-h-[90vh] overflow-y-auto">
        <div className="flex items-center justify-between mb-5">
          <h3 className="text-base font-semibold text-slate-900">{item ? 'Sửa sản phẩm' : 'Thêm sản phẩm'}</h3>
          <button onClick={onClose} className="p-1.5 rounded-lg hover:bg-slate-100 text-slate-400 hover:text-slate-700 transition-colors">
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" /></svg>
          </button>
        </div>
        <form onSubmit={handleSubmit} className="space-y-3">
          {[
            { label: 'Code *', value: code, set: setCode, type: 'text', required: true },
            { label: 'Tên *', value: name, set: setName, type: 'text', required: true },
            { label: 'Category ID', value: categoryId, set: setCategoryId, type: 'number', required: false },
            { label: 'Platform ID', value: platformId, set: setPlatformId, type: 'number', required: false },
            { label: 'Số lượng tối thiểu', value: minimumQuantity, set: setMinimumQuantity, type: 'number', required: false },
            { label: 'Đơn vị', value: unitName, set: setUnitName, type: 'text', required: false },
            { label: 'Mô tả', value: description, set: setDescription, type: 'text', required: false },
          ].map(({ label, value, set, type, required }) => (
            <div key={label}>
              <label className="block text-xs font-medium text-slate-700 mb-1">{label}</label>
              <input type={type} value={value} onChange={(e) => (set as React.Dispatch<React.SetStateAction<string>>)(e.target.value)} required={required}
                className="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500/30 focus:border-emerald-500" />
            </div>
          ))}
          <div className="flex gap-3 pt-2">
            <Button type="button" variant="secondary" className="w-full" onClick={onClose}>Huỷ</Button>
            <Button type="submit" variant="primary" className="w-full" disabled={isLoading}>{isLoading ? 'Đang lưu...' : (item ? 'Cập nhật' : 'Tạo mới')}</Button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AssetProductPage;
