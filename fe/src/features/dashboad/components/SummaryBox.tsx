import React from 'react';
import { formatCurrency, formatPercentage } from '@/lib/formatters';
import { DashboardSummary } from '../type/dashboard.type';

interface SummaryBoxProps {
  summary: DashboardSummary;
}

const SummaryBox: React.FC<SummaryBoxProps> = ({ summary }) => {
  const isProfit = summary.totalProfit >= 0;

  return (
    <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden mb-8">
      {/* Header */}
      <div className="bg-slate-900 px-6 py-4 flex items-center justify-between">
        <div>
          <p className="text-xs font-semibold text-slate-400 uppercase tracking-widest mb-0.5">
            Tổng quan tài sản
          </p>
          <p className="text-2xl font-bold text-white tracking-tight">
            {formatCurrency(summary.totalCurrentValue)}
          </p>
        </div>
        <div className="text-right">
          <p className="text-xs text-slate-400 mb-0.5">ROI</p>
          <p className={`text-lg font-bold ${isProfit ? 'text-emerald-400' : 'text-red-400'}`}>
            {isProfit ? '+' : ''}{formatPercentage(summary.totalProfitRate)}
          </p>
        </div>
      </div>

      {/* KPIs */}
      <div className="grid grid-cols-3 divide-x divide-slate-100">
        <div className="px-6 py-5">
          <p className="text-xs text-slate-500 uppercase tracking-wider font-medium mb-1.5">Vốn đầu tư</p>
          <p className="text-xl font-bold text-slate-900 tracking-tight">
            {formatCurrency(summary.totalInvestedCost)}
          </p>
        </div>

        <div className="px-6 py-5">
          <p className="text-xs text-slate-500 uppercase tracking-wider font-medium mb-1.5">Lợi nhuận</p>
          <p className={`text-xl font-bold tracking-tight ${isProfit ? 'text-emerald-600' : 'text-red-600'}`}>
            {isProfit ? '+' : ''}{formatCurrency(Math.abs(summary.totalProfit))}
          </p>
          <p className={`text-xs font-semibold mt-0.5 ${isProfit ? 'text-emerald-500' : 'text-red-500'}`}>
            {isProfit ? '▲' : '▼'} {formatPercentage(Math.abs(summary.totalProfitRate))}
          </p>
        </div>

        <div className="px-6 py-5">
          <p className="text-xs text-slate-500 uppercase tracking-wider font-medium mb-1.5">Thống kê</p>
          <div className="flex flex-wrap gap-2">
            <span className="inline-flex items-center rounded-full bg-slate-100 px-2.5 py-0.5 text-xs font-medium text-slate-600">
              {summary.totalAssetTypes ?? 0} loại tài sản
            </span>
            <span className="inline-flex items-center rounded-full bg-slate-100 px-2.5 py-0.5 text-xs font-medium text-slate-600">
              {summary.totalTransactions ?? 0} giao dịch
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SummaryBox;