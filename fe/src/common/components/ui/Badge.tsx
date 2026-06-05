import React from 'react';

export type BadgeVariant = 'success' | 'error' | 'warning' | 'info' | 'neutral' | 'purple';

interface BadgeProps {
  variant?: BadgeVariant;
  children: React.ReactNode;
  className?: string;
}

const variantClasses: Record<BadgeVariant, string> = {
  success: 'bg-emerald-100 text-emerald-700 ring-emerald-200',
  error: 'bg-red-100 text-red-700 ring-red-200',
  warning: 'bg-amber-100 text-amber-700 ring-amber-200',
  info: 'bg-blue-100 text-blue-700 ring-blue-200',
  neutral: 'bg-slate-100 text-slate-600 ring-slate-200',
  purple: 'bg-purple-100 text-purple-700 ring-purple-200',
};

const Badge: React.FC<BadgeProps> = ({ variant = 'neutral', children, className = '' }) => (
  <span
    className={[
      'inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium',
      'ring-1',
      variantClasses[variant],
      className,
    ].join(' ')}
  >
    {children}
  </span>
);

export default Badge;
