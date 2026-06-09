'use client';
import React, { useState } from 'react';
import PageHeader from '@/common/components/ui/PageHeader';
import Button from '@/common/components/ui/Button';
import Toast, { ToastType } from '@/common/components/ui/Toast';
import {
  AssetCategoryResponse, AssetPlatformResponse,
  CreateAssetCategoryRequest, UpdateAssetCategoryRequest,
  CreateAssetPlatformRequest, UpdateAssetPlatformRequest,
} from '../type/asset-management.type';
import {
  useLazyGetAssetCategoryByIdQuery, useCreateAssetCategoryMutation,
  useUpdateAssetCategoryMutation, useDeleteAssetCategoryMutation,
  useLazyGetAssetPlatformByIdQuery, useCreateAssetPlatformMutation,
  useUpdateAssetPlatformMutation, useDeleteAssetPlatformMutation,
} from '../services/asset-management.api';

interface ToastState { type: ToastType; message: string; }

// ─── Generic CRUD table ──────────────────────────────────────────────────────

interface SimpleEntity { id: number; code: string; name: string; description?: string | null; isActive?: boolean | null; createdAt?: string | null; }

interface SimpleTableProps {
  title: string;
  subtitle: string;
  items: SimpleEntity[];
  isFetching: boolean;
  searchId: string;
  onSearchIdChange: (v: string) => void;
  onSearch: () => void;
  onAdd: () => void;
  onEdit: (item: SimpleEntity) => void;
  onDelete: (id: number) => void;
  isDeleting: boolean;
}

const SimpleTable: React.FC<SimpleTableProps> = ({ title, subtitle, items, isFetching, searchId, onSearchIdChange, onSearch, onAdd, onEdit, onDelete, isDeleting }) => (
  <div className="p-6 animate-fade-up">
    <PageHeader title={title} description={subtitle} actions={<Button variant="primary" size="sm" onClick={onAdd}>+ Thêm</Button>} />
    <div className="flex gap-2 mb-6">
      <input type="number" value={searchId} onChange={(e) => onSearchIdChange(e.target.value)} onKeyDown={(e) => e.key === 'Enter' && onSearch()} placeholder="Tìm theo ID..."
        className="border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500/30 focus:border-emerald-500 w-48" />
      <Button variant="secondary" size="sm" onClick={onSearch} disabled={isFetching}>{isFetching ? 'Đang tìm...' : 'Tìm kiếm'}</Button>
    </div>
    <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden mb-6">
      <div className="overflow-x-auto">
        <table className="min-w-full">
          <thead>
            <tr className="bg-slate-900">
              {['ID', 'Code', 'Tên', 'Mô tả', 'Active', 'Thao tác'].map((h) => (
                <th key={h} className="px-4 py-3.5 text-left text-[11px] font-semibold text-slate-400 uppercase tracking-wider whitespace-nowrap">{h}</th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {items.length === 0 ? (
              <tr><td colSpan={6} className="px-4 py-8 text-center text-sm text-slate-400">Chưa có dữ liệu. Tìm theo ID hoặc tạo mới.</td></tr>
            ) : items.map((item) => (
              <tr key={item.id} className="hover:bg-slate-50/60 transition-colors">
                <td className="px-4 py-3 text-sm text-slate-500 font-mono">#{item.id}</td>
                <td className="px-4 py-3"><span className="inline-flex items-center px-2.5 py-1 rounded-md bg-slate-100 text-slate-700 text-xs font-semibold font-mono">{item.code}</span></td>
                <td className="px-4 py-3 text-sm font-medium text-slate-900">{item.name}</td>
                <td className="px-4 py-3 text-sm text-slate-500 max-w-xs truncate">{item.description ?? '—'}</td>
                <td className="px-4 py-3">
                  <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${item.isActive ? 'bg-emerald-100 text-emerald-700' : 'bg-slate-100 text-slate-500'}`}>
                    {item.isActive ? 'Hoạt động' : 'Tắt'}
                  </span>
                </td>
                <td className="px-4 py-3">
                  <div className="flex items-center gap-1.5">
                    <button onClick={() => onEdit(item)} className="px-2.5 py-1.5 text-xs rounded-lg font-medium bg-blue-50 text-blue-700 hover:bg-blue-100 border border-blue-200/80 transition-colors">Sửa</button>
                    <button onClick={() => onDelete(item.id)} disabled={isDeleting} className="px-2.5 py-1.5 text-xs rounded-lg font-medium bg-red-50 text-red-700 hover:bg-red-100 border border-red-200/80 transition-colors">Xoá</button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  </div>
);

// ─── Generic form modal ──────────────────────────────────────────────────────

interface GenericFormModalProps {
  title: string;
  item: SimpleEntity | null;
  onClose: () => void;
  onSubmit: (data: { id?: number; code: string; name: string; description: string | null }) => void;
  isLoading: boolean;
}

const GenericFormModal: React.FC<GenericFormModalProps> = ({ title, item, onClose, onSubmit, isLoading }) => {
  const [code, setCode] = useState(item?.code ?? '');
  const [name, setName] = useState(item?.name ?? '');
  const [description, setDescription] = useState(item?.description ?? '');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit({ ...(item ? { id: item.id } : {}), code, name, description: description || null });
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md p-6">
        <div className="flex items-center justify-between mb-5">
          <h3 className="text-base font-semibold text-slate-900">{title}</h3>
          <button onClick={onClose} className="p-1.5 rounded-lg hover:bg-slate-100 text-slate-400 hover:text-slate-700 transition-colors">
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" /></svg>
          </button>
        </div>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-medium text-slate-700 mb-1">Code *</label>
            <input type="text" value={code} onChange={(e) => setCode(e.target.value)} required className="w-full border border-slate-300 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500/30 focus:border-emerald-500" />
          </div>
          <div>
            <label className="block text-xs font-medium text-slate-700 mb-1">Tên *</label>
            <input type="text" value={name} onChange={(e) => setName(e.target.value)} required className="w-full border border-slate-300 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500/30 focus:border-emerald-500" />
          </div>
          <div>
            <label className="block text-xs font-medium text-slate-700 mb-1">Mô tả</label>
            <textarea value={description} onChange={(e) => setDescription(e.target.value)} rows={3} className="w-full border border-slate-300 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500/30 focus:border-emerald-500 resize-none" />
          </div>
          <div className="flex gap-3 pt-2">
            <Button type="button" variant="secondary" className="w-full" onClick={onClose}>Huỷ</Button>
            <Button type="submit" variant="primary" className="w-full" disabled={isLoading}>{isLoading ? 'Đang lưu...' : (item ? 'Cập nhật' : 'Tạo mới')}</Button>
          </div>
        </form>
      </div>
    </div>
  );
};

// ─── Asset Category Page ─────────────────────────────────────────────────────

export const AssetCategoryPage: React.FC = () => {
  const [searchId, setSearchId] = useState('');
  const [items, setItems] = useState<AssetCategoryResponse[]>([]);
  const [formOpen, setFormOpen] = useState(false);
  const [editItem, setEditItem] = useState<AssetCategoryResponse | null>(null);
  const [toast, setToast] = useState<ToastState | null>(null);

  const [fetchById, { isFetching }] = useLazyGetAssetCategoryByIdQuery();
  const [create, { isLoading: isCreating }] = useCreateAssetCategoryMutation();
  const [update, { isLoading: isUpdating }] = useUpdateAssetCategoryMutation();
  const [remove, { isLoading: isDeleting }] = useDeleteAssetCategoryMutation();

  const showToast = (type: ToastType, message: string) => setToast({ type, message });

  const handleSearch = async () => {
    const id = Number(searchId);
    if (!id) return;
    try {
      const res = await fetchById(id).unwrap();
      if (res.data) {
        setItems((prev) => { const ex = prev.find((i) => i.id === res.data.id); if (ex) return prev.map((i) => i.id === res.data.id ? res.data : i); return [...prev, res.data]; });
        showToast('success', 'Tìm thấy danh mục');
      }
    } catch { showToast('error', 'Không tìm thấy ID này'); }
  };

  const handleSubmit = async (data: { id?: number; code: string; name: string; description: string | null }) => {
    try {
      if (editItem) {
        const req: UpdateAssetCategoryRequest = { id: editItem.id, code: data.code, name: data.name, description: data.description };
        const res = await update(req).unwrap();
        if (res.data) { setItems((prev) => prev.map((i) => i.id === res.data.id ? res.data : i)); showToast('success', 'Cập nhật thành công!'); }
      } else {
        const req: CreateAssetCategoryRequest = { code: data.code, name: data.name, description: data.description };
        const res = await create(req).unwrap();
        if (res.data) { setItems((prev) => [res.data, ...prev]); showToast('success', 'Tạo thành công!'); }
      }
      setFormOpen(false); setEditItem(null);
    } catch { showToast('error', 'Thao tác thất bại. Vui lòng thử lại.'); }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Bạn có chắc chắn muốn xoá?')) return;
    try { await remove(id).unwrap(); setItems((prev) => prev.filter((i) => i.id !== id)); showToast('success', 'Xoá thành công!'); }
    catch { showToast('error', 'Xoá thất bại.'); }
  };

  return (
    <>
      <SimpleTable
        title="Quản lý Danh mục" subtitle="CRUD danh mục tài sản (POST /api/v1/admin/category)"
        items={items} isFetching={isFetching} searchId={searchId} onSearchIdChange={setSearchId} onSearch={handleSearch}
        onAdd={() => { setEditItem(null); setFormOpen(true); }}
        onEdit={(i) => { setEditItem(i as AssetCategoryResponse); setFormOpen(true); }}
        onDelete={handleDelete} isDeleting={isDeleting}
      />
      {formOpen && (
        <GenericFormModal title={editItem ? 'Sửa danh mục' : 'Thêm danh mục'} item={editItem} onClose={() => { setFormOpen(false); setEditItem(null); }} onSubmit={handleSubmit} isLoading={isCreating || isUpdating} />
      )}
      {toast && <Toast type={toast.type} message={toast.message} onDismiss={() => setToast(null)} />}
    </>
  );
};

// ─── Asset Platform Page ─────────────────────────────────────────────────────

export const AssetPlatformPage: React.FC = () => {
  const [searchId, setSearchId] = useState('');
  const [items, setItems] = useState<AssetPlatformResponse[]>([]);
  const [formOpen, setFormOpen] = useState(false);
  const [editItem, setEditItem] = useState<AssetPlatformResponse | null>(null);
  const [toast, setToast] = useState<ToastState | null>(null);

  const [fetchById, { isFetching }] = useLazyGetAssetPlatformByIdQuery();
  const [create, { isLoading: isCreating }] = useCreateAssetPlatformMutation();
  const [update, { isLoading: isUpdating }] = useUpdateAssetPlatformMutation();
  const [remove, { isLoading: isDeleting }] = useDeleteAssetPlatformMutation();

  const showToast = (type: ToastType, message: string) => setToast({ type, message });

  const handleSearch = async () => {
    const id = Number(searchId);
    if (!id) return;
    try {
      const res = await fetchById(id).unwrap();
      if (res.data) {
        setItems((prev) => { const ex = prev.find((i) => i.id === res.data.id); if (ex) return prev.map((i) => i.id === res.data.id ? res.data : i); return [...prev, res.data]; });
        showToast('success', 'Tìm thấy sàn giao dịch');
      }
    } catch { showToast('error', 'Không tìm thấy ID này'); }
  };

  const handleSubmit = async (data: { id?: number; code: string; name: string; description: string | null }) => {
    try {
      if (editItem) {
        const req: UpdateAssetPlatformRequest = { id: editItem.id, code: data.code, name: data.name, description: data.description };
        const res = await update(req).unwrap();
        if (res.data) { setItems((prev) => prev.map((i) => i.id === res.data.id ? res.data : i)); showToast('success', 'Cập nhật thành công!'); }
      } else {
        const req: CreateAssetPlatformRequest = { code: data.code, name: data.name, description: data.description };
        const res = await create(req).unwrap();
        if (res.data) { setItems((prev) => [res.data, ...prev]); showToast('success', 'Tạo thành công!'); }
      }
      setFormOpen(false); setEditItem(null);
    } catch { showToast('error', 'Thao tác thất bại. Vui lòng thử lại.'); }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Bạn có chắc chắn muốn xoá?')) return;
    try { await remove(id).unwrap(); setItems((prev) => prev.filter((i) => i.id !== id)); showToast('success', 'Xoá thành công!'); }
    catch { showToast('error', 'Xoá thất bại.'); }
  };

  return (
    <>
      <SimpleTable
        title="Quản lý Sàn giao dịch" subtitle="CRUD platform/sàn giao dịch (POST /api/v1/admin/platform)"
        items={items} isFetching={isFetching} searchId={searchId} onSearchIdChange={setSearchId} onSearch={handleSearch}
        onAdd={() => { setEditItem(null); setFormOpen(true); }}
        onEdit={(i) => { setEditItem(i as AssetPlatformResponse); setFormOpen(true); }}
        onDelete={handleDelete} isDeleting={isDeleting}
      />
      {formOpen && (
        <GenericFormModal title={editItem ? 'Sửa sàn giao dịch' : 'Thêm sàn giao dịch'} item={editItem} onClose={() => { setFormOpen(false); setEditItem(null); }} onSubmit={handleSubmit} isLoading={isCreating || isUpdating} />
      )}
      {toast && <Toast type={toast.type} message={toast.message} onDismiss={() => setToast(null)} />}
    </>
  );
};

export default AssetCategoryPage;
