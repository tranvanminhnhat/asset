import React from 'react';

interface EmptyStateProps {
  icon?: React.ReactNode;
  title: string;
  description?: string;
  action?: React.ReactNode;
}

const EmptyState: React.FC<EmptyStateProps> = ({ icon, title, description, action }) => (
  <div className="flex flex-col items-center justify-center py-16 px-6 text-center animate-fade-up">
    {icon && (
      <div className="mb-4 p-4 rounded-2xl bg-slate-100 text-slate-400">
        {icon}
      </div>
    )}
    <h3 className="text-sm font-semibold text-slate-900 mb-1">{title}</h3>
    {description && (
      <p className="text-sm text-slate-500 max-w-xs leading-relaxed">{description}</p>
    )}
    {action && <div className="mt-5">{action}</div>}
  </div>
);

export default EmptyState;
