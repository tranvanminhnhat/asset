import React from 'react';
import { formatCurrency, formatPercentage } from '@/lib/formatters';
import { DashboardSummary } from '../type/dashboard.type';

interface SummaryBoxProps {
  summary: DashboardSummary;
}

const SummaryBox: React.FC<SummaryBoxProps> = ({ summary }) => {
  return (
    <div className="mx-auto w-2/3 rounded-2xl bg-white p-6 mb-8 shadow-lg border border-gray-100">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <h3 className="text-xl font-bold text-gray-900">Tổng tài sản</h3>
        <button className="p-2 bg-blue-50 hover:bg-blue-100 rounded-lg transition duration-200" title="Cập nhật">
          <svg className="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
          </svg>
        </button>
      </div>

      {/* Main Total Value */}
      <div className="text-center mb-8">
        <p className="text-sm text-gray-500 mb-2">Tổng giá trị tài sản</p>
        <p className="text-5xl font-bold text-gray-900 tracking-tight">
          {formatCurrency(summary.totalCurrentValue, summary.currency)}
        </p>
      </div>

      {/* KPI Grid */}
      <div className="grid grid-cols-3 gap-4 mb-6">
        {/* Purchase Value */}
        <div className="text-center p-4 bg-gray-50 rounded-xl">
          <p className="text-xs text-gray-500 uppercase tracking-wide mb-2">Giá mua</p>
          <p className="text-lg font-semibold text-gray-900">
            {formatCurrency(summary.totalPurchaseValue, summary.currency)}
          </p>
        </div>

        {/* Profit Group */}
        <div className="text-center p-4 bg-green-50 rounded-xl border border-green-200">
          <p className="text-xs text-gray-500 uppercase tracking-wide mb-2">Lợi nhuận</p>
          <p className={`text-lg font-semibold ${summary.totalProfit >= 0 ? 'text-green-700' : 'text-red-700'}`}>
            {summary.totalProfit >= 0 ? '+' : ''}{formatCurrency(Math.abs(summary.totalProfit), summary.currency)}
          </p>
          <p className={`text-sm font-bold mt-1 ${summary.profitPercentage >= 0 ? 'text-green-600' : 'text-red-600'}`}>
            {summary.profitPercentage >= 0 ? '+' : ''}{formatPercentage(summary.profitPercentage)}
          </p>
        </div>

        {/* ROI */}
        <div className="text-center p-4 bg-blue-50 rounded-xl">
          <p className="text-xs text-gray-500 uppercase tracking-wide mb-2">ROI</p>
          <p className={`text-lg font-semibold ${summary.profitPercentage >= 0 ? 'text-blue-700' : 'text-red-700'}`}>
            {summary.profitPercentage >= 0 ? '+' : ''}{formatPercentage(summary.profitPercentage)}
          </p>
        </div>
      </div>

      {/* CTA Button */}
      <div className="mb-4 flex flex-wrap items-center justify-center gap-2 text-xs font-medium text-slate-600">
        <span className="rounded-full bg-slate-100 px-3 py-1">
          Loại tài sản: {summary.totalAssetTypes ?? 0}
        </span>
        <span className="rounded-full bg-slate-100 px-3 py-1">
          Tổng giao dịch: {summary.totalTransactions ?? 0}
        </span>
      </div>

      <button className="w-full bg-linear-to-r from-blue-600 to-blue-700 hover:from-blue-700 hover:to-blue-800 text-white font-semibold py-3 rounded-xl transition duration-200 shadow-md hover:shadow-lg">
        Xem báo cáo chi tiết
      </button>
    </div>
  );
};

export default SummaryBox;