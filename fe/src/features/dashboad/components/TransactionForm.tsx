'use client';

import React from 'react';
import { useForm, type Resolver } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { DashboardPortfolioItem, TransactionRequest } from '../type/dashboard.type';
import { transactionSchema, TransactionFormValues } from '../validation/transaction.schema';
import Button from '@/common/components/ui/Button';
import Input from '@/common/components/ui/Input';
import Select from '@/common/components/ui/Select';

interface TransactionFormProps {
  isSubmitting: boolean;
  portfolios: DashboardPortfolioItem[];
  selectedPortfolioId: number | null;
  onSelectPortfolio: (portfolioId: number) => void;
  onSubmit: (request: TransactionRequest) => Promise<void>;
}

const transactionTypes = ['BUY', 'SELL', 'DIVIDEND', 'ADJUSTMENT'] as const;
const assetTypes = ['STOCK', 'ETF', 'GOLD', 'EMERGENCY'] as const;

const TransactionForm: React.FC<TransactionFormProps> = ({
  isSubmitting,
  portfolios,
  selectedPortfolioId,
  onSelectPortfolio,
  onSubmit,
}) => {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<TransactionFormValues>({
    resolver: zodResolver(transactionSchema) as Resolver<TransactionFormValues>,
    defaultValues: {
      transactionType: 'BUY',
      assetType: 'STOCK',
      transactionDate: new Date().toISOString().slice(0, 10),
      quantity: 1,
      unitPrice: 0,
      referenceId: '',
      notes: '',
    },
  });

  const onFormSubmit = async (data: TransactionFormValues) => {
    if (selectedPortfolioId === null) return;

    await onSubmit({
      portfolioId: selectedPortfolioId,
      transactionType: data.transactionType,
      assetType: data.assetType,
      quantity: data.quantity,
      unitPrice: data.unitPrice,
      transactionDate: new Date(data.transactionDate).toISOString(),
      notes: data.notes?.trim() || undefined,
      referenceId: data.referenceId?.trim() || undefined,
    });

    reset();
  };

  return (
    <form
      onSubmit={handleSubmit(onFormSubmit)}
      className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden"
    >
      <div className="px-6 py-5 border-b border-slate-100">
        <h2 className="text-base font-semibold text-slate-900">Thêm giao dịch</h2>
        <p className="text-xs text-slate-500 mt-0.5">Ghi nhận giao dịch mới vào danh mục</p>
      </div>

      <div className="px-6 py-5 space-y-5">
        <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1.5">Danh mục</label>
            <select
              value={selectedPortfolioId ?? ''}
              onChange={(e) => onSelectPortfolio(Number(e.target.value))}
              className="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm text-slate-900 bg-white focus:outline-none focus:ring-2 focus:ring-emerald-500/30 focus:border-emerald-500 hover:border-slate-400 transition-all"
            >
              {portfolios.map((p) => (
                <option key={p.id} value={p.id}>
                  #{p.id} – {p.name}�€” {p.name}
                </option>
              ))}
            </select>
          </div>

          <Select
            label="Loại giao dịch"
            {...register('transactionType')}
            error={errors.transactionType?.message}
          >
            {transactionTypes.map((t) => (
              <option key={t} value={t}>{t}</option>
            ))}
          </Select>

          <Select
            label="Loại tài sản"
            {...register('assetType')}
            error={errors.assetType?.message}
          >
            {assetTypes.map((t) => (
              <option key={t} value={t}>{t}</option>
            ))}
          </Select>

          <Input
            label="Ngày giao dịch"
            type="date"
            {...register('transactionDate')}
            error={errors.transactionDate?.message}
          />

          <Input
            label="Số lượng"
            type="number"
            min="0"
            step="0.0001"
            {...register('quantity')}
            error={errors.quantity?.message}
          />

          <Input
            label="Đơn giá"
            type="number"
            min="0"
            step="0.01"
            {...register('unitPrice')}
            error={errors.unitPrice?.message}
          />

          <Input
            label="Reference ID"
            type="text"
            {...register('referenceId')}
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1.5">Ghi chú</label>
          <textarea
            {...register('notes')}
            rows={2}
            className="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm text-slate-900 focus:outline-none focus:ring-2 focus:ring-emerald-500/30 focus:border-emerald-500 transition-all resize-none hover:border-slate-400"
            placeholder="Mô tả ngắn cho giao dịch này..."
          />
        </div>
      </div>

      <div className="px-6 py-4 bg-slate-50/50 border-t border-slate-100 flex justify-end">
        <Button type="submit" loading={isSubmitting}>
          {isSubmitting ? 'Đang lưu...' : 'Lưu giao dịch'}
        </Button>
      </div>
    </form>
  );
};

export default TransactionForm;