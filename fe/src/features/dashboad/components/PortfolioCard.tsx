import React from 'react';
import { Portfolio } from '../type/dashboard.type';

interface PortfolioCardProps {
  portfolio: Portfolio;
}

const PortfolioCard: React.FC<PortfolioCardProps> = ({ portfolio }) => {
  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 2,
    }).format(value);
  };

  const formatPercentage = (value: number | null) => {
    if (value === null) return '0.00%';
    return `${value.toFixed(2)}%`;
  };

  const getTypeLabel = (type: string) => {
    switch (type) {
      case 'CRYPTO':
        return 'Quỹ khấn cấp';
      case 'STOCK':
        return 'Cổ phiếu';
      case 'GOLD':
        return 'Vàng';
      case 'ETF':
        return 'Quỹ ETF';
      default:
        return type;
    }
  };

  const getTypeIcon = (type: string) => {
    switch (type) {
      case 'CRYPTO':
        return '💰';
      case 'STOCK':
        return '📈';
      case 'GOLD':
        return '🏆';
      case 'ETF':
        return '📊';
      default:
        return '💼';
    }
  };

  return (
    <div className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm transition hover:shadow-md">
      {/* Header with icon and title */}
      <div className="flex items-center gap-3 mb-4">
        <span className="flex h-8 w-8 items-center justify-center rounded border border-blue-400 bg-blue-50 text-lg">
          {getTypeIcon(portfolio.type)}
        </span>
        <h3 className="text-base font-semibold text-slate-900 truncate" title={getTypeLabel(portfolio.type)}>
          {getTypeLabel(portfolio.type)}
        </h3>
      </div>

      {/* Price info */}
      <div className="mb-4 space-y-2">
        <div className="flex justify-between items-center">
          <span className="text-sm text-slate-600">Giá mua</span>
          <span className="text-sm font-semibold text-slate-900 truncate" title={formatCurrency(portfolio.purchaseValue)}>
            {formatCurrency(portfolio.purchaseValue)}
          </span>
        </div>
        <div className="flex justify-between items-center">
          <span className="text-sm text-slate-600">Giá hiện tại</span>
          <span className="text-sm font-semibold text-slate-900 truncate" title={formatCurrency(portfolio.currentValue)}>
            {formatCurrency(portfolio.currentValue)}
          </span>
        </div>
      </div>

      {/* Divider */}
      <div className="mb-4 border-t border-slate-200" />

      {/* Profit section */}
      <div className="mb-4 flex items-center justify-between">
        <div>
          <span className="text-sm text-slate-600">Lợi nhuận</span>
          <p className={`text-sm font-semibold ${portfolio.isProfit ? 'text-slate-900' : 'text-slate-900'}`}>
            {portfolio.isProfit ? '+' : ''}{formatCurrency(portfolio.profit)}
          </p>
        </div>
        <span className={`inline-block rounded-full px-2 py-1 text-xs font-semibold ${portfolio.isProfit ? 'bg-emerald-100 text-emerald-700' : 'bg-red-100 text-red-700'}`}>
          {portfolio.isProfit ? '+' : ''}{formatPercentage(portfolio.profitPercentage)}
        </span>
      </div>

      {/* Button */}
      <button className="w-full rounded border border-slate-300 bg-white py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50">
        Xem chi tiết
      </button>
    </div>
  );
};

export default PortfolioCard;