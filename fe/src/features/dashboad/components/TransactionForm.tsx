'use client';

import React, { useState } from 'react';
import { DashboardPortfolioItem, TransactionRequest, TransactionType } from '../type/dashboard.type';

interface TransactionFormProps {
  isSubmitting: boolean;
  portfolios: DashboardPortfolioItem[];
  selectedPortfolioId: number | null;
  onSelectPortfolio: (portfolioId: number) => void;
  onSubmit: (request: TransactionRequest) => Promise<void>;
}

const transactionTypes: TransactionType[] = ['BUY', 'SELL', 'DIVIDEND', 'ADJUSTMENT'];
const assetTypes = ['STOCK', 'ETF', 'GOLD', 'EMERGENCY'];

const TransactionForm: React.FC<TransactionFormProps> = ({
  isSubmitting,
  portfolios,
  selectedPortfolioId,
  onSelectPortfolio,
  onSubmit,
}) => {
  const [assetType, setAssetType] = useState('STOCK');
  const [transactionType, setTransactionType] = useState<TransactionType>('BUY');
  const [quantity, setQuantity] = useState('1');
  const [unitPrice, setUnitPrice] = useState('0');
  const [transactionDate, setTransactionDate] = useState(() => new Date().toISOString().slice(0, 10));
  const [notes, setNotes] = useState('');
  const [referenceId, setReferenceId] = useState('');

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (selectedPortfolioId === null) {
      return;
    }

    const parsedQuantity = Number(quantity);
    const parsedUnitPrice = Number(unitPrice);

    await onSubmit({
      portfolioId: selectedPortfolioId,
      transactionType,
      assetType,
      quantity: Number.isNaN(parsedQuantity) ? 0 : parsedQuantity,
      unitPrice: Number.isNaN(parsedUnitPrice) ? 0 : parsedUnitPrice,
      transactionDate: new Date(transactionDate).toISOString(),
      notes: notes.trim() || undefined,
      referenceId: referenceId.trim() || undefined,
    });

    setQuantity('1');
    setUnitPrice('0');
    setNotes('');
    setReferenceId('');
  };

  return (
    <form onSubmit={handleSubmit} className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
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
            value={transactionType}
            onChange={(event) => setTransactionType(event.target.value as TransactionType)}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 focus:border-emerald-500 focus:outline-none"
          >
            {transactionTypes.map((option) => (
              <option key={option} value={option}>
                {option}
              </option>
            ))}
          </select>
        </label>

        <label className="block">
          <span className="mb-2 block text-sm font-medium text-slate-700">Asset type</span>
          <select
            value={assetType}
            onChange={(event) => setAssetType(event.target.value)}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 focus:border-emerald-500 focus:outline-none"
          >
            {assetTypes.map((option) => (
              <option key={option} value={option}>
                {option}
              </option>
            ))}
          </select>
        </label>

        <label className="block">
          <span className="mb-2 block text-sm font-medium text-slate-700">Ngày giao dịch</span>
          <input
            type="date"
            value={transactionDate}
            onChange={(event) => setTransactionDate(event.target.value)}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 focus:border-emerald-500 focus:outline-none"
          />
        </label>

        <label className="block">
          <span className="mb-2 block text-sm font-medium text-slate-700">Số lượng</span>
          <input
            type="number"
            min="0"
            step="0.0001"
            value={quantity}
            onChange={(event) => setQuantity(event.target.value)}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 focus:border-emerald-500 focus:outline-none"
          />
        </label>

        <label className="block">
          <span className="mb-2 block text-sm font-medium text-slate-700">Đơn giá</span>
          <input
            type="number"
            min="0"
            step="0.01"
            value={unitPrice}
            onChange={(event) => setUnitPrice(event.target.value)}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 focus:border-emerald-500 focus:outline-none"
          />
        </label>

        <label className="block">
          <span className="mb-2 block text-sm font-medium text-slate-700">Reference ID</span>
          <input
            type="text"
            value={referenceId}
            onChange={(event) => setReferenceId(event.target.value)}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 focus:border-emerald-500 focus:outline-none"
          />
        </label>
      </div>

      <label className="mt-4 block">
        <span className="mb-2 block text-sm font-medium text-slate-700">Ghi chú</span>
        <textarea
          value={notes}
          onChange={(event) => setNotes(event.target.value)}
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