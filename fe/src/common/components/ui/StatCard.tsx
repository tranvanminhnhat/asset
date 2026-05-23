import React from 'react';

interface StatCardProps {
  label: string;
  value: string;
  change?: string;
  positive?: boolean;
  icon?: React.ReactNode;
  iconBg?: string;
}

const StatCard: React.FC<StatCardProps> = ({
  label,
  value,
  change,
  positive,
  icon,
  iconBg = 'bg-emerald-50 text-emerald-600',
}) => (
  <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm hover:shadow-md transition-shadow duration-200">
    <div className="flex items-start justify-between mb-3">
      <p className="text-xs font-medium text-slate-500 uppercase tracking-wider leading-4">{label}</p>
      {icon && (
        <div className={['p-2 rounded-lg', iconBg].join(' ')}>
          {icon}
        </div>
      )}
    </div>
    <p className="text-2xl font-bold text-slate-900 tracking-tight">{value}</p>
    {change !== undefined && (
      <p
        className={[
          'mt-1.5 text-xs font-medium flex items-center gap-1',
          positive ? 'text-emerald-600' : 'text-red-500',
        ].join(' ')}
      >
        {positive ? (
          <svg className="w-3 h-3" fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M5.293 9.707a1 1 0 010-1.414l4-4a1 1 0 011.414 0l4 4a1 1 0 01-1.414 1.414L11 7.414V15a1 1 0 11-2 0V7.414L6.707 9.707a1 1 0 01-1.414 0z" clipRule="evenodd" />
          </svg>
        ) : (
          <svg className="w-3 h-3" fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M14.707 10.293a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 111.414-1.414L9 12.586V5a1 1 0 012 0v7.586l2.293-2.293a1 1 0 011.414 0z" clipRule="evenodd" />
          </svg>
        )}
        {change}
      </p>
    )}
  </div>
);

export default StatCard;
