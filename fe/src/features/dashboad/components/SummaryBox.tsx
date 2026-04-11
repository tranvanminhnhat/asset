import React from 'react';
import { DashboardSummary } from '../type/dashboard.type';

interface SummaryBoxProps {
  summary: DashboardSummary;
}

const SummaryBox: React.FC<SummaryBoxProps> = ({ summary }) => {
  const formatCurrency = (value: number, currency: string) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: currency === 'USD' ? 'USD' : 'VND',
      minimumFractionDigits: 2,
    }).format(value);
  };

  const formatPercentage = (value: number) => {
    return `${value.toFixed(2)}%`;
  };

  return (
    <div className="bg-white rounded-lg shadow-md p-8 mb-8">
      <div className="flex items-center justify-between mb-6">
        <h3 className="text-2xl font-semibold text-slate-900">Tổng tài sản</h3>
        <span className="bg-blue-100 text-blue-700 text-xs font-semibold px-3 py-1 rounded-full">
          Cập nhật
        </span>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
        <div>
          <p className="text-sm text-slate-600 mb-2">Tổng giá trị</p>
          <p className="text-3xl font-bold text-emerald-600">
            {formatCurrency(summary.totalCurrentValue, summary.currency)}
          </p>
        </div>
        <div>
          <p className="text-sm text-slate-600 mb-2">Tổng giá mua</p>
          <p className="text-3xl font-bold text-slate-800">
            {formatCurrency(summary.totalPurchaseValue, summary.currency)}
          </p>
        </div>
        <div>
          <p className="text-sm text-slate-600 mb-2">Lãi suất lợi nhuận</p>
          <div className="flex items-baseline gap-2">
            <p className={`text-3xl font-bold ${summary.totalProfit >= 0 ? 'text-green-600' : 'text-red-600'}`}>
              {summary.totalProfit >= 0 ? '+' : ''}{formatCurrency(summary.totalProfit, summary.currency)}
            </p>
            <p className={`text-lg font-semibold ${summary.profitPercentage >= 0 ? 'text-green-600' : 'text-red-600'}`}>
              {summary.profitPercentage >= 0 ? '+' : ''}{formatPercentage(summary.profitPercentage)}
            </p>
          </div>
        </div>
      </div>

      <button className="w-full bg-slate-900 hover:bg-slate-800 text-white font-semibold py-3 px-6 rounded-lg transition">
        Xem báo cáo chi tiết
      </button>
    </div>
  );
};

export default SummaryBox;