import React from 'react';
import { FundAllocation } from '../type/investment-recommendations.type';

interface FundCardProps {
  fund: FundAllocation;
}

const FundCard: React.FC<FundCardProps> = ({ fund }) => {
  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
      minimumFractionDigits: 0,
    }).format(value);
  };

  const getIconForType = (type: string) => {
    switch (type) {
      case 'CRYPTO':
        return '💰';
      case 'GOLD':
        return '🏆';
      case 'ETF':
        return '📊';
      case 'STOCK':
        return '📈';
      default:
        return '💼';
    }
  };

  return (
    <div className="bg-white rounded-lg p-6 border border-slate-200 hover:shadow-lg transition">
      <div className="flex items-start justify-between mb-4">
        <div className="flex items-center gap-3">
          <span className="text-3xl">{getIconForType(fund.type)}</span>
          <div>
            <h4 className="font-semibold text-slate-900">{fund.name}</h4>
            <p className="text-xs text-slate-500">{fund.type}</p>
          </div>
        </div>
      </div>

      <div className="space-y-3 mb-4">
        <div>
          <p className="text-sm text-slate-600">Số tiền đầu tư</p>
          <p className="text-2xl font-bold text-slate-900">{formatCurrency(fund.amount)}</p>
        </div>
        <div>
          <p className="text-sm text-slate-600">Tỷ lệ phân bổ</p>
          <div className="flex items-center gap-2">
            <div className="flex-1 bg-slate-200 rounded-full h-2">
              <div
                className="bg-blue-600 h-2 rounded-full"
                style={{ width: `${fund.percentage}%` }}
              />
            </div>
            <span className="text-lg font-bold text-slate-900">{fund.percentage}%</span>
          </div>
        </div>
      </div>

      <p className="text-sm text-slate-600">{fund.description}</p>
    </div>
  );
};

export default FundCard;
