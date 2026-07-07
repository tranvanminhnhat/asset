import React from 'react';
import { formatCurrency, formatPercentage } from '@/lib/formatters';
import { DashboardSummary } from '../type/dashboard.type';
import { useI18n } from '@/common/i18n/I18nProvider';

interface SummaryBoxProps {
  summary: DashboardSummary;
}

const SummaryBox: React.FC<SummaryBoxProps> = ({ summary }) => {
  const { pick, locale } = useI18n();
  const isProfit = summary.totalProfit >= 0;

  return (
    <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden mb-8">
      {/* Header */}
      <div className="bg-slate-900 px-6 py-4 flex items-center justify-between">
        <div>
          <p className="text-xs font-semibold text-slate-400 uppercase tracking-widest mb-0.5">
            {pick({ vi: 'Tong quan tai san', en: 'Asset overview', ja: '資産概要' })}
          </p>
          <p className="text-2xl font-bold text-white tracking-tight">
            {formatCurrency(summary.totalCurrentValue, 'VND', locale)}
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
          <p className="text-xs text-slate-500 uppercase tracking-wider font-medium mb-1.5">{pick({ vi: 'Von dau tu', en: 'Invested capital', ja: '投資元本' })}</p>
          <p className="text-xl font-bold text-slate-900 tracking-tight">
            {formatCurrency(summary.totalInvestedCost, 'VND', locale)}
          </p>
        </div>

        <div className="px-6 py-5">
          <p className="text-xs text-slate-500 uppercase tracking-wider font-medium mb-1.5">{pick({ vi: 'Loi nhuan', en: 'Profit', ja: '利益' })}</p>
          <p className={`text-xl font-bold tracking-tight ${isProfit ? 'text-emerald-600' : 'text-red-600'}`}>
            {isProfit ? '+' : ''}{formatCurrency(Math.abs(summary.totalProfit), 'VND', locale)}
          </p>
          <p className={`text-xs font-semibold mt-0.5 ${isProfit ? 'text-emerald-500' : 'text-red-500'}`}>
            {isProfit ? '▲' : '▼'} {formatPercentage(Math.abs(summary.totalProfitRate))}
          </p>
        </div>

        <div className="px-6 py-5">
          <p className="text-xs text-slate-500 uppercase tracking-wider font-medium mb-1.5">{pick({ vi: 'Thong ke', en: 'Statistics', ja: '統計' })}</p>
          <div className="flex flex-wrap gap-2">
            <span className="inline-flex items-center rounded-full bg-slate-100 px-2.5 py-0.5 text-xs font-medium text-slate-600">
              {summary.totalAssetTypes ?? 0} {pick({ vi: 'loai tai san', en: 'asset types', ja: '資産種類' })}
            </span>
            <span className="inline-flex items-center rounded-full bg-slate-100 px-2.5 py-0.5 text-xs font-medium text-slate-600">
              {summary.totalTransactions ?? 0} {pick({ vi: 'giao dich', en: 'transactions', ja: '取引' })}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SummaryBox;