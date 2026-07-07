import React from 'react';
import { formatCurrency, formatPercentage } from '@/lib/formatters';
import { DashboardSummary } from '../type/dashboard.type';
import { useI18n } from '@/common/i18n/I18nProvider';

interface DashboardSummaryProps {
  summary: DashboardSummary;
}

const DashboardSummaryComponent: React.FC<DashboardSummaryProps> = ({ summary }) => {
  const { pick, locale } = useI18n();

  return (
    <div className="bg-white rounded-lg shadow-md p-6 mb-8">
      <h2 className="text-2xl font-semibold text-emerald-950 mb-6">{pick({ vi: 'Tong quan', en: 'Overview', ja: '概要' })}</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div className="text-center">
          <p className="text-sm text-slate-600 mb-2">{pick({ vi: 'Gia tri hien tai', en: 'Current value', ja: '現在価値' })}</p>
          <p className="text-xl font-bold text-emerald-600">
            {formatCurrency(summary.totalCurrentValue, 'VND', locale)}
          </p>
        </div>
        <div className="text-center">
          <p className="text-sm text-slate-600 mb-2">{pick({ vi: 'Gia tri mua', en: 'Purchase value', ja: '取得価値' })}</p>
          <p className="text-xl font-bold text-slate-800">
            {formatCurrency(summary.totalPurchaseValue, 'VND', locale)}
          </p>
        </div>
        <div className="text-center">
          <p className="text-sm text-slate-600 mb-2">{pick({ vi: 'Loi nhuan', en: 'Profit', ja: '利益' })}</p>
          <p className={`text-xl font-bold ${summary.totalProfit >= 0 ? 'text-green-600' : 'text-red-600'}`}>
            {formatCurrency(summary.totalProfit, 'VND', locale)}
          </p>
        </div>
        <div className="text-center">
          <p className="text-sm text-slate-600 mb-2">{pick({ vi: 'Ty le loi nhuan', en: 'Profit ratio', ja: '利益率' })}</p>
          <p className={`text-xl font-bold ${summary.profitPercentage >= 0 ? 'text-green-600' : 'text-red-600'}`}>
            {formatPercentage(summary.profitPercentage)}
          </p>
        </div>
      </div>
    </div>
  );
};

export default DashboardSummaryComponent;