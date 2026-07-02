import React from 'react';
import { formatUnitPrice } from '@/lib/formatters';
import { AssetPrice } from '../type/investment-processing.type';

interface AssetOptionProps {
  asset: AssetPrice;
  isSelected: boolean;
  onClick: () => void;
}

const AssetOption: React.FC<AssetOptionProps> = ({ asset, isSelected, onClick }) => {
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
        <div className="flex-1 min-w-0">
          <div className="flex items-center justify-between gap-2">
            <p className={`text-sm font-semibold truncate ${isSelected ? 'text-emerald-900' : 'text-slate-900'}`}>
              {asset.assetCode ?? `ID-${asset.productId}`}
            </p>
            <p className="text-sm font-bold text-slate-900 shrink-0">
              {formatUnitPrice(asset.currentPrice)}
            </p>
          </div>
          <p className="text-xs text-slate-500 mt-0.5 truncate">{asset.assetName ?? `Product #${asset.productId}`}</p>
        </div>
      </div>
    </button>
  );
};

export default AssetOption;
