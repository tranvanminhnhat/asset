import React from 'react';
import { formatCurrency, formatPercentage } from '@/lib/formatters';
import { DashboardPortfolioItem } from '../type/dashboard.type';

interface PortfolioCardProps {
  portfolio: DashboardPortfolioItem;
}

const PortfolioCard: React.FC<PortfolioCardProps> = ({ portfolio }) => {
  const getTypeLabel = (type: string) => {
    switch (type) {
      case 'EMERGENCY':
        return 'Quỹ khẩn cấp';
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
      case 'EMERGENCY':
        return '🛟';
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
          {getTypeIcon(portfolio.assetType)}
        </span>
        <div>
          <h3 className="text-base font-semibold text-slate-900 truncate" title={getTypeLabel(portfolio.assetType)}>
            {getTypeLabel(portfolio.assetType)}
          </h3>
          <p className="text-xs text-slate-500">{portfolio.assetType}</p>
        </div>
      </div>

      {/* Price info */}
      <div className="mb-4 space-y-2">
        <div className="flex justify-between items-center">
          <span className="text-sm text-slate-600">Loại tài sản</span>
          <span className="text-sm font-semibold text-slate-900">{portfolio.assetType}</span>
        </div>
        <div className="flex justify-between items-center">
          <span className="text-sm text-slate-600">Số lượng</span>
          <span className="text-sm font-semibold text-slate-900">{portfolio.quantity}</span>
        </div>
        <div className="flex justify-between items-center">
          <span className="text-sm text-slate-600">Vốn đầu tư</span>
          <span className="text-sm font-semibold text-slate-900 truncate" title={formatCurrency(portfolio.investedCost)}>
            {formatCurrency(portfolio.investedCost)}
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
          <span className="text-sm text-slate-600">PnL</span>
          <p className={`text-sm font-semibold ${portfolio.isProfit ? 'text-emerald-700' : 'text-red-700'}`}>
            {portfolio.isProfit ? '+' : ''}{formatCurrency(portfolio.profit)}
          </p>
        </div>
        <span className={`inline-block rounded-full px-2 py-1 text-xs font-semibold ${portfolio.isProfit ? 'bg-emerald-100 text-emerald-700' : 'bg-red-100 text-red-700'}`}>
          {portfolio.isProfit ? '+' : ''}{formatPercentage(portfolio.profitRate)}
        </span>
      </div>
    </div>
  );
};

export default PortfolioCard;