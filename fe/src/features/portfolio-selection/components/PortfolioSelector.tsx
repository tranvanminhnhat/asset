import React from 'react';
import { PortfolioOption } from '../type/portfolio-selection.type';
import { useI18n } from '@/common/i18n/I18nProvider';

interface PortfolioSelectorProps {
  options: PortfolioOption[];
  selectedId: string | null;
  onSelect: (portfolioId: string) => void;
}

const riskConfig: Record<string, { key: string; color: string; bg: string; bar: string; badge: string }> = {
  LOW: { key: 'LOW', color: 'text-emerald-700', bg: 'bg-emerald-50 border-emerald-300', bar: 'bg-emerald-500', badge: 'bg-emerald-100 text-emerald-700' },
  MEDIUM: { key: 'MEDIUM', color: 'text-blue-700', bg: 'bg-blue-50 border-blue-300', bar: 'bg-blue-500', badge: 'bg-blue-100 text-blue-700' },
  HIGH: { key: 'HIGH', color: 'text-orange-700', bg: 'bg-orange-50 border-orange-300', bar: 'bg-orange-500', badge: 'bg-orange-100 text-orange-700' },
};

const PortfolioSelector: React.FC<PortfolioSelectorProps> = ({ options, selectedId, onSelect }) => {
  const { pick } = useI18n();

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
      {options.map((option) => {
        const cfg = riskConfig[option.riskLevel] ?? { key: option.riskLevel, color: 'text-slate-700', bg: 'bg-slate-50 border-slate-200', bar: 'bg-slate-500', badge: 'bg-slate-100 text-slate-700' };
        const riskLabel = {
          LOW: pick({ vi: 'An toan', en: 'Safe', ja: '低リスク' }),
          MEDIUM: pick({ vi: 'Can bang', en: 'Balanced', ja: '中リスク' }),
          HIGH: pick({ vi: 'Tang truong', en: 'Growth', ja: '高リスク' }),
        }[cfg.key] ?? cfg.key;
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
                {riskLabel}
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
                <span className="text-slate-500">{pick({ vi: 'Loi nhuan ky vong', en: 'Expected return', ja: '期待収益' })}</span>
                <span className={`font-bold ${cfg.color}`}>{option.expectedReturn.toFixed(2)}%/year</span>
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
