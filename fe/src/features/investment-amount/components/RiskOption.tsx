import React from 'react';

interface RiskOptionProps {
  label: string;
  description: string;
  isSelected: boolean;
  onClick: () => void;
}

const RiskOption: React.FC<RiskOptionProps> = ({ label, description, isSelected, onClick }) => {
  return (
    <div
      onClick={onClick}
      className={`p-4 rounded-lg border-2 cursor-pointer transition ${
        isSelected
          ? 'border-blue-500 bg-blue-50'
          : 'border-slate-200 hover:border-slate-300 bg-white'
      }`}
    >
      <div className="flex items-start gap-3">
        <input
          type="radio"
          checked={isSelected}
          onChange={onClick}
          className="w-4 h-4 mt-1 accent-blue-600"
        />
        <div>
          <p className="font-semibold text-slate-900">{label}</p>
          <p className="text-sm text-slate-600">{description}</p>
        </div>
      </div>
    </div>
  );
};

export default RiskOption;
