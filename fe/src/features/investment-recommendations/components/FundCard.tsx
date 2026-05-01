import React from 'react';
import { formatCurrency } from '@/lib/formatters';
import { FundAllocation } from '../type/investment-recommendations.type';

interface FundCardProps {
  fund: FundAllocation;
}

const FundCard: React.FC<FundCardProps> = ({ fund }) => {
  const getIconForType = (type: string) => {
    switch (type) {
      case 'EMERGENCY':
        return '🛟';
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
          <p className="text-sm text-slate-600">Tổng tiền đầu tư</p>
          <p className="text-2xl font-bold text-slate-900">{formatCurrency(fund.totalAmount)}</p>
          <p className="text-xs text-slate-500 mt-1">
            {fund.quantity} quantity x {formatCurrency(fund.unitAmount)}/quantity
          </p>
        </div>
        <div>
          <p className="text-sm text-slate-600">Tiền dư từ phiên trước</p>
          <p className="text-xl font-bold text-amber-600">{formatCurrency(fund.carryOverAmount)}</p>
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
