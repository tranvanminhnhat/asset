import React from 'react';
import { PortfolioOption } from '../type/portfolio-selection.type';

interface PortfolioSelectorProps {
  options: PortfolioOption[];
  selectedId: string | null;
  onSelect: (portfolioId: string) => void;
}

const riskConfig: Record<string, { label: string; color: string; bg: string; bar: string; badge: string }> = {
  LOW: { label: 'An Toàn', color: 'text-emerald-700', bg: 'bg-emerald-50 border-emerald-300', bar: 'bg-emerald-500', badge: 'bg-emerald-100 text-emerald-700' },
  MEDIUM: { label: 'Cân Bằng', color: 'text-blue-700', bg: 'bg-blue-50 border-blue-300', bar: 'bg-blue-500', badge: 'bg-blue-100 text-blue-700' },
  HIGH: { label: 'Tăng Trưởng', color: 'text-orange-700', bg: 'bg-orange-50 border-orange-300', bar: 'bg-orange-500', badge: 'bg-orange-100 text-orange-700' },
};

const PortfolioSelector: React.FC<PortfolioSelectorProps> = ({ options, selectedId, onSelect }) => {
  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
      {options.map((option) => {
        const cfg = riskConfig[option.riskLevel] ?? { label: option.riskLevel, color: 'text-slate-700', bg: 'bg-slate-50 border-slate-200', bar: 'bg-slate-500', badge: 'bg-slate-100 text-slate-700' };
        const isSelected = selectedId === option.id;

        return (
          <button
            key={option.id}
            type="button"
            onClick={() => onSelect(option.id)}
            className={[
              'w-full text-left p-5 rounded-xl border-2 transition-all duration-150',
              isSelected ? `${cfg.bg} border-2 shadow-sm` : 'bg-white border-slate-200 hover:border-slate-300 hover:bg-slate-50',
            ].join(' ')}
          >
            <div className="flex items-center justify-between mb-3">
              <span className={`text-xs font-bold px-2.5 py-1 rounded-full ${cfg.badge}`}>
                {cfg.label}
              </span>
              <span
                className={[
                  'w-4 h-4 rounded-full border-2 flex items-center justify-center',
                  isSelected ? `border-emerald-500 bg-emerald-500` : 'border-slate-300 bg-white',
                ].join(' ')}
              >
                {isSelected && <span className="w-1.5 h-1.5 rounded-full bg-white" />}
              </span>
            </div>

            <h3 className="text-base font-bold text-slate-900 mb-1">{option.name}</h3>
            <p className="text-xs text-slate-500 mb-4">{option.description}</p>

            <div className="space-y-1.5">
              <div className="flex justify-between text-xs">
                <span className="text-slate-500">Lợi nhuận kỳ vọng</span>
                <span className={`font-bold ${cfg.color}`}>{option.expectedReturn.toFixed(2)}%/năm</span>
              </div>
              <div className="w-full bg-slate-100 rounded-full h-1.5">
                <div
                  className={`h-1.5 rounded-full ${cfg.bar}`}
                  style={{ width: `${Math.min(option.expectedReturn, 100)}%` }}
                />
              </div>
            </div>
          </button>
        );
      })}
    </div>
  );
};

export default PortfolioSelector;
