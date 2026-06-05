import React from 'react';
import { formatCurrency } from '@/lib/formatters';
import { FundAllocation } from '../type/investment-recommendations.type';

interface FundCardProps {
  fund: FundAllocation;
}

const typeConfig: Record<string, { color: string; bg: string; bar: string }> = {
  EMERGENCY: { color: 'text-amber-700', bg: 'bg-amber-50 border-amber-200', bar: 'bg-amber-500' },
  GOLD: { color: 'text-yellow-700', bg: 'bg-yellow-50 border-yellow-200', bar: 'bg-yellow-500' },
  ETF: { color: 'text-purple-700', bg: 'bg-purple-50 border-purple-200', bar: 'bg-purple-500' },
  STOCK: { color: 'text-blue-700', bg: 'bg-blue-50 border-blue-200', bar: 'bg-blue-500' },
};

const FundCard: React.FC<FundCardProps> = ({ fund }) => {
  const cfg = typeConfig[fund.type] ?? { color: 'text-slate-700', bg: 'bg-slate-50 border-slate-200', bar: 'bg-emerald-500' };

  return (
    <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm hover:shadow-md transition-all duration-200 hover:-translate-y-0.5">
      {/* Header */}
      <div className="flex items-center gap-3 mb-4">
        <div className={`px-2.5 py-1 rounded-lg border text-xs font-bold ${cfg.bg} ${cfg.color}`}>
          {fund.type}
        </div>
        <span className="text-xs font-bold text-slate-900 ml-auto">{fund.percentage}%</span>
      </div>

      <h4 className="text-sm font-semibold text-slate-900 mb-1 truncate">{fund.name}</h4>
      <p className="text-xs text-slate-500 mb-4 line-clamp-2">{fund.description}</p>

      {/* Progress bar */}
      <div className="w-full bg-slate-100 rounded-full h-1.5 mb-4">
        <div className={`h-1.5 rounded-full ${cfg.bar}`} style={{ width: `${fund.percentage}%` }} />
      </div>

      {/* Metrics */}
      <div className="space-y-2">
        {[
          { label: 'Tổng đầu tư', value: fund.totalAmount, cls: 'text-slate-900' },
          { label: 'Phiên này', value: fund.amount, cls: 'text-slate-900' },
          { label: 'Dư phiên trước', value: fund.carryOverAmount, cls: 'text-amber-600' },
          { label: 'Dư phiên này', value: fund.residualAmount, cls: 'text-rose-600' },
        ].map(({ label, value, cls }) => (
          <div key={label} className="flex justify-between">
            <span className="text-xs text-slate-500">{label}</span>
            <span className={`text-xs font-semibold ${cls}`}>{formatCurrency(value)}</span>
          </div>
        ))}
      </div>
    </div>
  );
};

export default FundCard;
