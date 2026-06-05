import React from 'react';

interface SpinnerProps {
  size?: 'sm' | 'md' | 'lg';
  className?: string;
}

const sizeMap = {
  sm: 'w-4 h-4',
  md: 'w-8 h-8',
  lg: 'w-12 h-12',
};

const Spinner: React.FC<SpinnerProps> = ({ size = 'md', className = '' }) => (
  <svg
    className={['animate-spin', sizeMap[size], className].join(' ')}
    fill="none"
    viewBox="0 0 24 24"
    aria-label="Loading"
  >
    <circle
      className="opacity-20"
      cx="12"
      cy="12"
      r="10"
      stroke="currentColor"
      strokeWidth="4"
    />
    <path
      className="opacity-80"
      fill="currentColor"
      d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"
    />
  </svg>
);

export default Spinner;
