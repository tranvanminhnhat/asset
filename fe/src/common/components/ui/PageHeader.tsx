import React from 'react';

interface PageHeaderProps {
  title: string;
  description?: string;
  actions?: React.ReactNode;
}

const PageHeader: React.FC<PageHeaderProps> = ({ title, description, actions }) => (
  <div className="flex items-start justify-between mb-6">
    <div>
      <h1 className="text-xl font-semibold tracking-tight text-slate-900">{title}</h1>
      {description && (
        <p className="text-sm text-slate-500 mt-0.5">{description}</p>
      )}
    </div>
    {actions && (
      <div className="flex items-center gap-2 shrink-0 ml-4">{actions}</div>
    )}
  </div>
);

export default PageHeader;
