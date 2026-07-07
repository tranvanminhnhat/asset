'use client';

import React, { createContext, useContext, useEffect, useMemo, useState } from 'react';
import {
  AppLanguage,
  DEFAULT_LANGUAGE,
  LANGUAGE_STORAGE_KEY,
  LOCALE_BY_LANGUAGE,
  normalizeLanguage,
} from './config';
import { MESSAGES } from './messages';

type TranslateParams = Record<string, string | number>;

interface TranslateOptions {
  params?: TranslateParams;
  fallback?: string;
}

interface I18nContextValue {
  language: AppLanguage;
  locale: string;
  setLanguage: (language: AppLanguage) => void;
  t: (key: string, options?: TranslateOptions) => string;
  pick: (values: Record<AppLanguage, string>) => string;
}

const I18nContext = createContext<I18nContextValue | undefined>(undefined);

const applyParams = (template: string, params?: TranslateParams): string => {
  if (!params) return template;

  return Object.entries(params).reduce((result, [name, value]) => {
    return result.replace(new RegExp(`\\{\\{${name}\\}\\}`, 'g'), String(value));
  }, template);
};

export const I18nProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [language, setLanguageState] = useState<AppLanguage>(DEFAULT_LANGUAGE);

  useEffect(() => {
    const persisted = normalizeLanguage(localStorage.getItem(LANGUAGE_STORAGE_KEY));
    if (persisted) {
      setLanguageState(persisted);
      return;
    }

    const fromBrowser = normalizeLanguage(window.navigator.language);
    if (fromBrowser) {
      setLanguageState(fromBrowser);
    }
  }, []);

  useEffect(() => {
    localStorage.setItem(LANGUAGE_STORAGE_KEY, language);
    document.documentElement.lang = language;
  }, [language]);

  const value = useMemo<I18nContextValue>(() => {
    const t = (key: string, options?: TranslateOptions) => {
      const currentMessages = MESSAGES[language];
      const defaultMessages = MESSAGES[DEFAULT_LANGUAGE];

      const template = currentMessages[key] ?? defaultMessages[key] ?? options?.fallback ?? key;
      return applyParams(template, options?.params);
    };

    return {
      language,
      locale: LOCALE_BY_LANGUAGE[language],
      setLanguage: setLanguageState,
      t,
      pick: (values: Record<AppLanguage, string>) => values[language] ?? values[DEFAULT_LANGUAGE],
    };
  }, [language]);

  return <I18nContext.Provider value={value}>{children}</I18nContext.Provider>;
};

export const useI18n = (): I18nContextValue => {
  const context = useContext(I18nContext);

  if (!context) {
    throw new Error('useI18n must be used within I18nProvider');
  }

  return context;
};
