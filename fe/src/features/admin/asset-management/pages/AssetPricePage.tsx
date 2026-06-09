'use client';
import React, { useState } from 'react';
import { formatDateTime } from '@/lib/formatters';
import PageHeader from '@/common/components/ui/PageHeader';
import Button from '@/common/components/ui/Button';
import Toast, { ToastType } from '@/common/components/ui/Toast';
import {
  AssetPriceAdminResponse,
  CreateAssetPriceRequest,
  UpdateAssetPriceRequest,
} from '../type/asset-management.type';
import {
  useLazyGetAssetPriceByIdQuery,
  useCreateAssetPriceMutation,
  useUpdateAssetPriceMutation,
  useDeleteAssetPriceMutation,
} from '../services/asset-management.api';

interface ToastState { type: ToastType; message: string; }

const AssetPricePage: React.FC = () => {
  const [searchId, setSearchId] = useState('');
  const [items, setItems] = useState<AssetPriceAdminResponse[]>([]);
  const [formOpen, setFormOpen] = useState(false);
  const [editItem, setEditItem] = useState<AssetPriceAdminResponse | null>(null);
  const [toast, setToast] = useState<ToastState | null>(null);

  const [fetchById, { isFetching }] = useLazyGetAssetPriceByIdQuery();
  const [createPrice, { isLoading: isCreating }] = useCreateAssetPriceMutation();
  const [updatePrice, { isLoading: isUpdating }] = useUpdateAssetPriceMutation();
  const [deletePrice, { isLoading: isDeleting }] = useDeleteAssetPriceMutation();

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
        showToast('success', 'Tìm thấy giá tài sản');
      }
    } catch {
      showToast('error', 'Không tìm thấy ID này');
    }
  };

  const handleCreate = async (form: CreateAssetPriceRequest) => {
    try {
      const res = await createPrice(form).unwrap();
      if (res.data) {
        setItems((prev) => [res.data, ...prev]);
        showToast('success', 'Tạo giá tài sản thành công!');
        setFormOpen(false);
      }
    } catch {
      showToast('error', 'Tạo thất bại. Vui lòng thử lại.');
    }
  };

  const handleUpdate = async (form: UpdateAssetPriceRequest) => {
    try {
      const res = await updatePrice(form).unwrap();
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
      await deletePrice(id).unwrap();
      setItems((prev) => prev.filter((i) => i.id !== id));
      showToast('success', 'Xoá thành công!');
    } catch {
      showToast('error', 'Xoá thất bại. Vui lòng thử lại.');
    }
  };

  const handleEdit = (item: AssetPriceAdminResponse) => {
    setEditItem(item);
    setFormOpen(true);
  };

  return (
    <div className="p-6 animate-fade-up">
      <PageHeader
        title="Quản lý Giá Tài sản"
        description="CRUD giá mua/bán tài sản (POST /api/v1/admin/price)"
        actions={
          <Button variant="primary" size="sm" onClick={() => { setEditItem(null); setFormOpen(true); }}>
            + Thêm giá
          </Button>
        }
      />

      {/* Search by ID */}
      <div className="flex gap-2 mb-6">
        <input
          type="number"
          value={searchId}
          onChange={(e) => setSearchId(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
          placeholder="Tìm theo ID..."
          className="border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500/30 focus:border-emerald-500 w-48"
        />
        <Button variant="secondary" size="sm" onClick={handleSearch} disabled={isFetching}>
          {isFetching ? 'Đang tìm...' : 'Tìm kiếm'}
        </Button>
      </div>

      {/* Table */}
      <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden mb-6">
        <div className="overflow-x-auto">
          <table className="min-w-full">
            <thead>
              <tr className="bg-slate-900">
                {['ID', 'Product ID', 'Giá mua', 'Giá bán', 'Ngày giá', 'Active', 'Thao tác'].map((h) => (
                  <th key={h} className="px-4 py-3.5 text-left text-[11px] font-semibold text-slate-400 uppercase tracking-wider whitespace-nowrap">
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {items.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-4 py-8 text-center text-sm text-slate-400">
                    Chưa có dữ liệu. Tìm theo ID hoặc tạo mới.
                  </td>
                </tr>
              ) : items.map((item) => (
                <tr key={item.id} className="hover:bg-slate-50/60 transition-colors">
                  <td className="px-4 py-3 text-sm text-slate-500 font-mono">#{item.id}</td>
                  <td className="px-4 py-3 text-sm text-slate-700">#{item.productId}</td>
                  <td className="px-4 py-3 text-sm font-semibold text-emerald-700">{item.buyPrice?.toLocaleString('vi-VN')} ₫</td>
                  <td className="px-4 py-3 text-sm font-semibold text-rose-600">{item.sellPrice?.toLocaleString('vi-VN')} ₫</td>
                  <td className="px-4 py-3 text-sm text-slate-500">{item.priceDate ? formatDateTime(item.priceDate) : '—'}</td>
                  <td className="px-4 py-3">
                    <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${item.isActive ? 'bg-emerald-100 text-emerald-700' : 'bg-slate-100 text-slate-500'}`}>
                      {item.isActive ? 'Hoạt động' : 'Tắt'}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex items-center gap-1.5">
                      <button onClick={() => handleEdit(item)} className="px-2.5 py-1.5 text-xs rounded-lg font-medium bg-blue-50 text-blue-700 hover:bg-blue-100 border border-blue-200/80 transition-colors">Sửa</button>
                      <button onClick={() => handleDelete(item.id)} disabled={isDeleting} className="px-2.5 py-1.5 text-xs rounded-lg font-medium bg-red-50 text-red-700 hover:bg-red-100 border border-red-200/80 transition-colors">Xoá</button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Form Modal */}
      {formOpen && (
        <AssetPriceFormModal
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
  item: AssetPriceAdminResponse | null;
  onClose: () => void;
  onCreate: (req: CreateAssetPriceRequest) => void;
  onUpdate: (req: UpdateAssetPriceRequest) => void;
  isLoading: boolean;
}

const AssetPriceFormModal: React.FC<FormModalProps> = ({ item, onClose, onCreate, onUpdate, isLoading }) => {
  const [productId, setProductId] = useState(String(item?.productId ?? ''));
  const [buyPrice, setBuyPrice] = useState(String(item?.buyPrice ?? ''));
  const [sellPrice, setSellPrice] = useState(String(item?.sellPrice ?? ''));
  const [priceDate, setPriceDate] = useState(item?.priceDate?.slice(0, 16) ?? '');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (item) {
      onUpdate({ id: item.id, productId: Number(productId), buyPrice: Number(buyPrice), sellPrice: Number(sellPrice), priceDate: priceDate || null });
    } else {
      onCreate({ productId: Number(productId), buyPrice: Number(buyPrice), sellPrice: Number(sellPrice), priceDate: priceDate || null });
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md p-6">
        <div className="flex items-center justify-between mb-5">
          <h3 className="text-base font-semibold text-slate-900">{item ? 'Sửa giá tài sản' : 'Thêm giá tài sản'}</h3>
          <button onClick={onClose} className="p-1.5 rounded-lg hover:bg-slate-100 text-slate-400 hover:text-slate-700 transition-colors">
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-medium text-slate-700 mb-1">Product ID *</label>
            <input type="number" value={productId} onChange={(e) => setProductId(e.target.value)} required className="w-full border border-slate-300 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500/30 focus:border-emerald-500" />
          </div>
          <div>
            <label className="block text-xs font-medium text-slate-700 mb-1">Giá mua (VND) *</label>
            <input type="number" value={buyPrice} onChange={(e) => setBuyPrice(e.target.value)} required className="w-full border border-slate-300 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500/30 focus:border-emerald-500" />
          </div>
          <div>
            <label className="block text-xs font-medium text-slate-700 mb-1">Giá bán (VND) *</label>
            <input type="number" value={sellPrice} onChange={(e) => setSellPrice(e.target.value)} required className="w-full border border-slate-300 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500/30 focus:border-emerald-500" />
          </div>
          <div>
            <label className="block text-xs font-medium text-slate-700 mb-1">Ngày giá</label>
            <input type="datetime-local" value={priceDate} onChange={(e) => setPriceDate(e.target.value)} className="w-full border border-slate-300 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500/30 focus:border-emerald-500" />
          </div>
          <div className="flex gap-3 pt-2">
            <Button type="button" variant="secondary" className="w-full" onClick={onClose}>Huỷ</Button>
            <Button type="submit" variant="primary" className="w-full" disabled={isLoading}>
              {isLoading ? 'Đang lưu...' : (item ? 'Cập nhật' : 'Tạo mới')}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AssetPricePage;
