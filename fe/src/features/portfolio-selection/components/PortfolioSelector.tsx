import React from 'react';
import { PortfolioOption } from '../type/portfolio-selection.type';

interface PortfolioSelectorProps {
  options: PortfolioOption[];
  selectedId: string | null;
  onSelect: (portfolioId: string) => void;
}

const PortfolioSelector: React.FC<PortfolioSelectorProps> = ({ options, selectedId, onSelect }) => {
  const getRiskColor = (riskLevel: string) => {
    switch (riskLevel) {
      case 'LOW':
        return 'bg-green-50 border-green-200';
      case 'MEDIUM':
        return 'bg-blue-50 border-blue-200';
      case 'HIGH':
        return 'bg-orange-50 border-orange-200';
      default:
        return 'bg-slate-50 border-slate-200';
    }
  };

  const getRiskLabel = (riskLevel: string) => {
    switch (riskLevel) {
      case 'LOW':
        return 'An Toàn';
      case 'MEDIUM':
        return 'Cân Bằng';
      case 'HIGH':
        return 'Tăng Trưởng';
      default:
        return riskLevel;
    }
  };

  return (
    <div className="space-y-4">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {options.map((option) => (
          <div
            key={option.id}
            onClick={() => onSelect(option.id)}
            className={`p-6 rounded-lg border-2 cursor-pointer transition ${
              selectedId === option.id
                ? 'border-blue-500 bg-blue-50'
                : getRiskColor(option.riskLevel)
            }`}
          >
            <div className="flex items-start justify-between mb-3">
              <div>
                <h3 className="text-lg font-semibold text-slate-900">{getRiskLabel(option.riskLevel)}</h3>
                <p className="text-sm text-slate-600">{option.description}</p>
              </div>
              <input
                type="radio"
                name="portfolio"
                value={option.id}
                checked={selectedId === option.id}
                onChange={(e) => onSelect(e.target.value)}
                className="w-4 h-4 accent-blue-600"
              />
            </div>
            <div className="text-sm text-slate-600">
              <p>📈 {option.expectedReturn.toFixed(2)}% tổng trưởng</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default PortfolioSelector;
