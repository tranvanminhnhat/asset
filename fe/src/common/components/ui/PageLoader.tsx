import React from 'react';
import Spinner from './Spinner';
import { useI18n } from '@/common/i18n/I18nProvider';

interface PageLoaderProps {
  text?: string;
}

const PageLoader: React.FC<PageLoaderProps> = ({ text }) => {
  const { pick } = useI18n();

  return (
    <div className="flex flex-col items-center justify-center py-24 gap-4 animate-fade-up">
      <Spinner size="lg" className="text-emerald-600" />
      <p className="text-sm text-slate-500 font-medium">
        {text ?? pick({ vi: 'Dang tai du lieu...', en: 'Loading data...', ja: 'データを読み込み中...' })}
      </p>
    </div>
  );
};

export default PageLoader;
