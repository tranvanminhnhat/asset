'use client';
import React, { useEffect, useState } from 'react';
import {
  AllocationMasterResponse,
  AllocationMasterRequest,
  AllocationMasterDetailRequest,
} from '../type/allocation-master.type';
import Modal from '@/common/components/ui/Modal';
import Button from '@/common/components/ui/Button';
import Input from '@/common/components/ui/Input';

interface Props {
  open: boolean;
  initialData?: AllocationMasterResponse | null;
  onClose: () => void;
  onSubmit: (data: AllocationMasterRequest) => void;
  isSubmitting?: boolean;
}

const emptyDetail = (): AllocationMasterDetailRequest => ({
  assetType: '',
  percentage: 0,
  minimumLotSizeAmount: 0,
});

const AllocationMasterFormModal: React.FC<Props> = ({
  open,
  initialData,
  onClose,
  onSubmit,
  isSubmitting = false,
}) => {
  const isEdit = !!initialData;

  const [code, setCode] = useState('');
  const [name, setName] = useState('');
  const [details, setDetails] = useState<AllocationMasterDetailRequest[]>([emptyDetail()]);

  useEffect(() => {
    if (open) {
      if (initialData) {
        setCode(initialData.code);
        setName(initialData.name);
        setDetails(
          initialData.details.length > 0
            ? initialData.details.map((d) => ({
                assetType: d.assetType,
                percentage: d.percentage,
                minimumLotSizeAmount: d.minimumLotSizeAmount,
              }))
            : [emptyDetail()],
        );
      } else {
        setCode('');
        setName('');
        setDetails([emptyDetail()]);
      }
    }
  }, [open, initialData]);

  const handleDetailChange = <K extends keyof AllocationMasterDetailRequest>(
    index: number,
    field: K,
    value: AllocationMasterDetailRequest[K],
  ) => {
    setDetails((prev) => prev.map((d, i) => (i === index ? { ...d, [field]: value } : d)));
  };

  const addDetail = () => setDetails((prev) => [...prev, emptyDetail()]);

  const removeDetail = (index: number) => {
    setDetails((prev) => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit({ code, name, details });
  };

  return (
    <Modal
      open={open}
      onClose={onClose}
      title={isEdit ? 'Chỉnh sửa Allocation Master' : 'Tạo mới Allocation Master'}
      maxWidth="max-w-2xl"
    >
      <form onSubmit={handleSubmit}>
        <div className="px-6 py-5 space-y-5 max-h-[65vh] overflow-y-auto">
          {/* Basic info */}
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Code"
              value={code}
              onChange={(e) => setCode(e.target.value)}
              required
              placeholder="VD: SAFE_001"
            />
            <Input
              label="Name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="Tên phân bổ (tuỳ chọn)"
            />
          </div>

          {/* Details section */}
          <div>
            <div className="flex items-center justify-between mb-3">
              <div>
                <p className="text-sm font-medium text-slate-700">Chi tiết phân bổ</p>
                <p className="text-xs text-slate-500 mt-0.5">{details.length} dòng</p>
              </div>
              <Button
                type="button"
                variant="outline"
                size="sm"
                onClick={addDetail}
                leftIcon={
                  <svg xmlns="http://www.w3.org/2000/svg" className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M12 4v16m8-8H4" />
                  </svg>
                }
              >
                Thêm dòng
              </Button>
            </div>

            <div className="space-y-3">
              {details.map((detail, index) => (
                <div
                  key={index}
                  className="border border-slate-200 rounded-xl p-4 bg-slate-50/50 hover:bg-slate-50 transition-colors"
                >
                  <div className="flex justify-between items-center mb-3">
                    <span className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
                      Detail #{index + 1}
                    </span>
                    {details.length > 1 && (
                      <button
                        type="button"
                        onClick={() => removeDetail(index)}
                        className="text-xs text-red-500 hover:text-red-700 font-medium flex items-center gap-1 transition-colors"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                          <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
                        </svg>
                        Xoá
                      </button>
                    )}
                  </div>
                  <div className="grid grid-cols-3 gap-3">
                    <Input
                      label="Loại tài sản"
                      value={detail.assetType}
                      onChange={(e) => handleDetailChange(index, 'assetType', e.target.value)}
                      required
                      placeholder="STOCK"
                    />
                    <Input
                      label="Tỷ lệ (%)"
                      type="number"
                      step="0.01"
                      min="0"
                      max="100"
                      value={detail.percentage}
                      onChange={(e) => handleDetailChange(index, 'percentage', Number(e.target.value))}
                      required
                    />
                    <Input
                      label="Lot tối thiểu"
                      type="number"
                      step="0.01"
                      min="0"
                      value={detail.minimumLotSizeAmount}
                      onChange={(e) => handleDetailChange(index, 'minimumLotSizeAmount', Number(e.target.value))}
                      required
                    />
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="flex justify-end gap-2.5 px-6 py-4 border-t border-slate-100 bg-slate-50/50 rounded-b-2xl">
          <Button type="button" variant="outline" onClick={onClose}>
            Huỷ
          </Button>
          <Button type="submit" loading={isSubmitting}>
            {isEdit ? 'Cập nhật' : 'Tạo mới'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};

export default AllocationMasterFormModal;
