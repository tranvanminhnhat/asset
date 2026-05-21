'use client';

import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { DashboardPortfolioItem, TransactionRequest } from '../type/dashboard.type';
import { transactionSchema, TransactionFormValues } from '../validation/transaction.schema';

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
    resolver: zodResolver(transactionSchema),
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
    if (selectedPortfolioId === null) {
      return;
    }

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
    <form onSubmit={handleSubmit(onFormSubmit)} className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-semibold text-slate-900">Thêm giao dịch</h2>
          <p className="text-sm text-slate-600">Gửi đúng contract của BE dưới endpoint portfolio transactions.</p>
        </div>
      </div>

      <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
        <label className="block">
          <span className="mb-2 block text-sm font-medium text-slate-700">Danh mục</span>
          <select
            value={selectedPortfolioId ?? ''}
            onChange={(event) => onSelectPortfolio(Number(event.target.value))}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 focus:border-emerald-500 focus:outline-none"
          >
            {portfolios.map((portfolio) => (
              <option key={portfolio.id} value={portfolio.id}>
                #{portfolio.id} - {portfolio.name}
              </option>
            ))}
          </select>
        </label>

        <label className="block">
          <span className="mb-2 block text-sm font-medium text-slate-700">Loại giao dịch</span>
          <select
            {...register('transactionType')}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 focus:border-emerald-500 focus:outline-none"
          >
            {transactionTypes.map((option) => (
              <option key={option} value={option}>
                {option}
              </option>
            ))}
          </select>
          {errors.transactionType && (
            <p className="mt-1 text-sm text-red-600">{errors.transactionType.message}</p>
          )}
        </label>

        <label className="block">
          <span className="mb-2 block text-sm font-medium text-slate-700">Asset type</span>
          <select
            {...register('assetType')}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 focus:border-emerald-500 focus:outline-none"
          >
            {assetTypes.map((option) => (
              <option key={option} value={option}>
                {option}
              </option>
            ))}
          </select>
          {errors.assetType && (
            <p className="mt-1 text-sm text-red-600">{errors.assetType.message}</p>
          )}
        </label>

        <label className="block">
          <span className="mb-2 block text-sm font-medium text-slate-700">Ngày giao dịch</span>
          <input
            type="date"
            {...register('transactionDate')}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 focus:border-emerald-500 focus:outline-none"
          />
          {errors.transactionDate && (
            <p className="mt-1 text-sm text-red-600">{errors.transactionDate.message}</p>
          )}
        </label>

        <label className="block">
          <span className="mb-2 block text-sm font-medium text-slate-700">Số lượng</span>
          <input
            type="number"
            min="0"
            step="0.0001"
            {...register('quantity')}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 focus:border-emerald-500 focus:outline-none"
          />
          {errors.quantity && (
            <p className="mt-1 text-sm text-red-600">{errors.quantity.message}</p>
          )}
        </label>

        <label className="block">
          <span className="mb-2 block text-sm font-medium text-slate-700">Đơn giá</span>
          <input
            type="number"
            min="0"
            step="0.01"
            {...register('unitPrice')}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 focus:border-emerald-500 focus:outline-none"
          />
          {errors.unitPrice && (
            <p className="mt-1 text-sm text-red-600">{errors.unitPrice.message}</p>
          )}
        </label>

        <label className="block">
          <span className="mb-2 block text-sm font-medium text-slate-700">Reference ID</span>
          <input
            type="text"
            {...register('referenceId')}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 focus:border-emerald-500 focus:outline-none"
          />
        </label>
      </div>

      <label className="mt-4 block">
        <span className="mb-2 block text-sm font-medium text-slate-700">Ghi chú</span>
        <textarea
          {...register('notes')}
          rows={3}
          className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 focus:border-emerald-500 focus:outline-none"
          placeholder="Mô tả ngắn cho giao dịch này"
        />
      </label>

      <div className="mt-6 flex justify-end">
        <button
          type="submit"
          disabled={isSubmitting}
          className="rounded-lg bg-emerald-600 px-5 py-3 font-semibold text-white transition hover:bg-emerald-700 disabled:opacity-50"
        >
          {isSubmitting ? 'Đang lưu...' : 'Lưu giao dịch'}
        </button>
      </div>
    </form>
  );
};

export default TransactionForm;
