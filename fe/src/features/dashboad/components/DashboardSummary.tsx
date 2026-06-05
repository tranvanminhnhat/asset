import React from 'react';
import { formatCurrency, formatPercentage } from '@/lib/formatters';
import { DashboardSummary } from '../type/dashboard.type';

interface DashboardSummaryProps {
  summary: DashboardSummary;
}

const DashboardSummaryComponent: React.FC<DashboardSummaryProps> = ({ summary }) => {
  return (
    <div className="bg-white rounded-lg shadow-md p-6 mb-8">
      <h2 className="text-2xl font-semibold text-emerald-950 mb-6">Tổng quan</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div className="text-center">
          <p className="text-sm text-slate-600 mb-2">Giá trị hiện tại</p>
          <p className="text-xl font-bold text-emerald-600">
            {formatCurrency(summary.totalCurrentValue, summary.currency)}
          </p>
        </div>
        <div className="text-center">
          <p className="text-sm text-slate-600 mb-2">Giá trị mua</p>
          <p className="text-xl font-bold text-slate-800">
            {formatCurrency(summary.totalPurchaseValue, summary.currency)}
          </p>
        </div>
        <div className="text-center">
          <p className="text-sm text-slate-600 mb-2">Lợi nhuận</p>
          <p className={`text-xl font-bold ${summary.totalProfit >= 0 ? 'text-green-600' : 'text-red-600'}`}>
            {formatCurrency(summary.totalProfit, summary.currency)}
          </p>
        </div>
        <div className="text-center">
          <p className="text-sm text-slate-600 mb-2">Tỷ lệ lợi nhuận</p>
          <p className={`text-xl font-bold ${summary.profitPercentage >= 0 ? 'text-green-600' : 'text-red-600'}`}>
            {formatPercentage(summary.profitPercentage)}
          </p>
        </div>
      </div>
    </div>
  );
};

export default DashboardSummaryComponent;