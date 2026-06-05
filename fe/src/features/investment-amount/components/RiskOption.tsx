import React from 'react';

interface RiskOptionProps {
  label: string;
  description: string;
  isSelected: boolean;
  onClick: () => void;
}

const RiskOption: React.FC<RiskOptionProps> = ({ label, description, isSelected, onClick }) => {
  return (
    <button
      type="button"
      onClick={onClick}
      className={[
        'w-full text-left p-4 rounded-xl border-2 transition-all duration-150',
        isSelected
          ? 'border-emerald-500 bg-emerald-50 shadow-sm'
          : 'border-slate-200 bg-white hover:border-slate-300 hover:bg-slate-50',
      ].join(' ')}
    >
      <div className="flex items-center gap-3">
        <span
          className={[
            'w-4 h-4 rounded-full border-2 flex items-center justify-center shrink-0',
            isSelected ? 'border-emerald-500 bg-emerald-500' : 'border-slate-300 bg-white',
          ].join(' ')}
        >
          {isSelected && <span className="w-1.5 h-1.5 rounded-full bg-white" />}
        </span>
        <div>
          <p className={`text-sm font-semibold ${isSelected ? 'text-emerald-900' : 'text-slate-900'}`}>
            {label}
          </p>
          <p className="text-xs text-slate-500 mt-0.5">{description}</p>
        </div>
      </div>
    </button>
  );
};

export default RiskOption;

