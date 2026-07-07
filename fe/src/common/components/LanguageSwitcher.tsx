'use client';

import React from 'react';
import { useI18n } from '@/common/i18n/I18nProvider';
import { AppLanguage, SUPPORTED_LANGUAGES } from '@/common/i18n/config';

interface LanguageSwitcherProps {
  className?: string;
}

const LanguageSwitcher: React.FC<LanguageSwitcherProps> = ({ className = '' }) => {
  const { language, setLanguage, t } = useI18n();

  return (
    <label className={`flex items-center gap-2 rounded-xl border border-slate-200 bg-white/95 px-3 py-2 shadow-sm backdrop-blur ${className}`}>
      <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 text-slate-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M3 5h12M9 3v2m1.048 9.5A18.022 18.022 0 016.412 9m6.088 5.5A16.947 16.947 0 0113 18m-6.588-9h11.176M3 19h12" />
      </svg>
      <span className="text-xs font-semibold text-slate-600">{t('language.option')}</span>
      <select
        value={language}
        onChange={(e) => setLanguage(e.target.value as AppLanguage)}
        className="rounded-md border border-slate-300 bg-white px-2 py-1 text-xs font-medium text-slate-700 outline-none focus:border-emerald-500"
        aria-label={t('language.option')}
      >
        {SUPPORTED_LANGUAGES.map((code) => (
          <option key={code} value={code}>
            {t(`language.${code}`)}
          </option>
        ))}
      </select>
    </label>
  );
};

export default LanguageSwitcher;
