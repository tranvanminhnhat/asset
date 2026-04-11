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
    <div className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition">
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-3">
          <span className="text-2xl">{getTypeIcon(portfolio.type)}</span>
          <div>
            <h3 className="font-semibold text-slate-900">{getTypeLabel(portfolio.type)}</h3>
            <p className="text-xs text-slate-500">{portfolio.name}</p>
          </div>
        </div>
      </div>

      <div className="space-y-3 mb-4">
        <div className="flex justify-between items-center">
          <span className="text-xs text-slate-600">Giá mua</span>
          <span className="text-sm font-semibold text-slate-900">
            {formatCurrency(portfolio.purchaseValue)}
          </span>
        </div>
        <div className="flex justify-between items-center">
          <span className="text-xs text-slate-600">Giá hiện tại</span>
          <span className="text-sm font-semibold text-slate-900">
            {formatCurrency(portfolio.currentValue)}
          </span>
        </div>
      </div>

      <div className="border-t pt-3">
        <div className="flex justify-between items-center mb-2">
          <span className="text-xs text-slate-600">Lợi nhuận</span>
          <span className={`text-sm font-bold ${portfolio.isProfit ? 'text-green-600' : 'text-red-600'}`}>
            {portfolio.isProfit ? '+' : ''}{formatCurrency(portfolio.profit)}
          </span>
        </div>
        <div className="flex justify-between items-center">
          <span className="text-xs text-slate-600">Tỷ lệ</span>
          <span className={`text-sm font-bold ${portfolio.isProfit ? 'text-green-600' : 'text-red-600'}`}>
            {portfolio.isProfit ? '+' : ''}{formatPercentage(portfolio.profitPercentage)}
          </span>
        </div>
      </div>

      <button className="w-full mt-4 py-2 text-blue-600 hover:bg-blue-50 rounded text-sm font-medium transition">
        Xem chi tiết
      </button>
    </div>
  );
};

export default PortfolioCard;