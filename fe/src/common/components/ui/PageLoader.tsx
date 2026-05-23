import React from 'react';
import Spinner from './Spinner';

interface PageLoaderProps {
  text?: string;
}

const PageLoader: React.FC<PageLoaderProps> = ({ text = 'Đang tải dữ liệu...' }) => (
  <div className="flex flex-col items-center justify-center py-24 gap-4 animate-fade-up">
    <Spinner size="lg" className="text-emerald-600" />
    <p className="text-sm text-slate-500 font-medium">{text}</p>
  </div>
);

export default PageLoader;
