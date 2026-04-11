import React from 'react';
import { RecommendedStock } from '../type/investment-recommendations.type';

interface StockRecommendationCardProps {
  stock: RecommendedStock;
}

const StockRecommendationCard: React.FC<StockRecommendationCardProps> = ({ stock }) => {
  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
      minimumFractionDigits: 0,
    }).format(value);
  };

  return (
    <div className="bg-white rounded-lg p-6 border border-slate-200 hover:shadow-lg transition">
      <div className="flex items-start justify-between mb-4">
        <div>
          <h4 className="text-lg font-semibold text-slate-900">{stock.code}</h4>
          <p className="text-sm text-slate-600">{stock.name}</p>
        </div>
        <span className="text-xs font-semibold px-3 py-1 rounded-full bg-slate-100 text-slate-700">
          {stock.sector}
        </span>
      </div>

      <div className="grid grid-cols-2 gap-4 mb-4 text-sm">
        <div>
          <p className="text-slate-600">Giá hiện tại</p>
          <p className="text-lg font-bold text-slate-900">{formatCurrency(stock.currentPrice)}</p>
        </div>
        <div>
          <p className="text-slate-600">Mục tiêu giá</p>
          <p className="text-lg font-bold text-slate-900">{formatCurrency(stock.estimatedPrice)}</p>
        </div>
      </div>

      <div className="mb-4 p-3 bg-blue-50 rounded-lg">
        <p className="text-xs text-slate-600 mb-1">Lợi nhuận tiềm tàng</p>
        <p className="text-xl font-bold text-blue-600">+{stock.potentialReturn.toFixed(2)}%</p>
      </div>

      <div className="space-y-3 text-sm">
        <div>
          <p className="font-semibold text-slate-900 mb-2">Ưu điểm:</p>
          <ul className="list-disc list-inside space-y-1 text-slate-600">
            {stock.advantages.map((adv, idx) => (
              <li key={idx}>{adv}</li>
            ))}
          </ul>
        </div>
        <div>
          <p className="font-semibold text-slate-900 mb-2">Rủi ro:</p>
          <ul className="list-disc list-inside space-y-1 text-slate-600">
            {stock.risks.map((risk, idx) => (
              <li key={idx}>{risk}</li>
            ))}
          </ul>
        </div>
      </div>

      <div className="mt-4 pt-4 border-t">
        <span className={`text-xs font-semibold px-3 py-1 rounded-full ${
          stock.riskLevel === 'LOW'
            ? 'bg-green-100 text-green-700'
            : stock.riskLevel === 'MEDIUM'
            ? 'bg-blue-100 text-blue-700'
            : 'bg-orange-100 text-orange-700'
        }`}>
          {stock.riskLevel === 'LOW' ? 'Rủi ro Thấp' : stock.riskLevel === 'MEDIUM' ? 'Rủi ro Vừa' : 'Rủi ro Cao'}
        </span>
      </div>
    </div>
  );
};

export default StockRecommendationCard;
