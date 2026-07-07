export const SUPPORTED_LANGUAGES = ['vi', 'en', 'ja'] as const;

export type AppLanguage = (typeof SUPPORTED_LANGUAGES)[number];

export const DEFAULT_LANGUAGE: AppLanguage = 'vi';

export const LANGUAGE_STORAGE_KEY = 'appLanguage';

export const LOCALE_BY_LANGUAGE: Record<AppLanguage, string> = {
  vi: 'vi-VN',
  en: 'en-US',
  ja: 'ja-JP',
};

export const normalizeLanguage = (value?: string | null): AppLanguage | null => {
  if (!value) {
    return null;
  }

  const normalized = value.toLowerCase();

  if (normalized.startsWith('vi')) return 'vi';
  if (normalized.startsWith('en')) return 'en';
  if (normalized.startsWith('ja')) return 'ja';

  return null;
};
