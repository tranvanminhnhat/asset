import React from 'react';
import { formatCurrency, formatPercentage } from '@/lib/formatters';
import { DashboardPortfolioItem } from '../type/dashboard.type';

interface PortfolioCardProps {
  portfolio: DashboardPortfolioItem;
}

const assetConfig: Record<string, { label: string; color: string; bg: string; icon: React.ReactNode }> = {
  EMERGENCY: {
    label: 'Quỹ Khẩn Cấp',
    color: 'text-amber-700',
    bg: 'bg-amber-50 border-amber-200',
    icon: (
      <svg xmlns="http://www.w3.org/2000/svg" className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.8}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
      </svg>
    ),
  },
  STOCK: {
    label: 'Cổ Phiếu',
    color: 'text-blue-700',
    bg: 'bg-blue-50 border-blue-200',
    icon: (
      <svg xmlns="http://www.w3.org/2000/svg" className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.8}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
      </svg>
    ),
  },
  GOLD: {
    label: 'Vàng',
    color: 'text-yellow-700',
    bg: 'bg-yellow-50 border-yellow-200',
    icon: (
      <svg xmlns="http://www.w3.org/2000/svg" className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.8}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
    ),
  },
  ETF: {
    label: 'Quỹ ETF',
    color: 'text-purple-700',
    bg: 'bg-purple-50 border-purple-200',
    icon: (
      <svg xmlns="http://www.w3.org/2000/svg" className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.8}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
      </svg>
    ),
  },
};

const PortfolioCard: React.FC<PortfolioCardProps> = ({ portfolio }) => {
  const cfg = assetConfig[portfolio.assetType] ?? {
    label: portfolio.assetType,
    color: 'text-slate-700',
    bg: 'bg-slate-50 border-slate-200',
    icon: (
      <svg xmlns="http://www.w3.org/2000/svg" className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.8}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
      </svg>
    ),
  };

  const isProfit = portfolio.isProfit;

  return (
    <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm hover:shadow-md transition-all duration-200 hover:-translate-y-0.5">
      {/* Header */}
      <div className="flex items-center gap-3 mb-4">
        <div className={`p-2.5 rounded-xl border ${cfg.bg} ${cfg.color} shrink-0`}>
          {cfg.icon}
        </div>
        <div className="min-w-0">
          <h3 className="text-sm font-semibold text-slate-900 truncate">{cfg.label}</h3>
          <p className="text-xs text-slate-500 font-mono">{portfolio.assetType}</p>
        </div>
        <span
          className={[
            'ml-auto shrink-0 inline-flex items-center rounded-full px-2 py-0.5 text-xs font-semibold',
            isProfit ? 'bg-emerald-100 text-emerald-700' : 'bg-red-100 text-red-700',
          ].join(' ')}
        >
          {isProfit ? '+' : ''}{formatPercentage(portfolio.profitRate)}
        </span>
      </div>

      {/* Metrics */}
      <div className="space-y-2.5">
        <div className="flex justify-between text-xs">
          <span className="text-slate-500">Số lượng</span>
          <span className="font-semibold text-slate-900">{portfolio.quantity}</span>
        </div>
        <div className="flex justify-between text-xs">
          <span className="text-slate-500">Vốn đầu tư</span>
          <span className="font-semibold text-slate-900">{formatCurrency(portfolio.investedCost)}</span>
        </div>
        <div className="flex justify-between text-xs">
          <span className="text-slate-500">Giá trị hiện tại</span>
          <span className="font-semibold text-slate-900">{formatCurrency(portfolio.currentValue)}</span>
        </div>
      </div>

      {/* Divider */}
      <div className="my-3 border-t border-slate-100" />

      {/* PnL */}
      <div className="flex items-center justify-between">
        <span className="text-xs text-slate-500">Lợi nhuận (PnL)</span>
        <span className={`text-sm font-bold ${isProfit ? 'text-emerald-600' : 'text-red-600'}`}>
          {isProfit ? '+' : ''}{formatCurrency(portfolio.profit)}
        </span>
      </div>
    </div>
  );
};

export default PortfolioCard;