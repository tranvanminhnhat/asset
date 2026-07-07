'use client';

import React, { useEffect, useMemo, useState } from 'react';
import Button from '@/common/components/ui/Button';
import Input from '@/common/components/ui/Input';
import Select from '@/common/components/ui/Select';
import { useI18n } from '@/common/i18n/I18nProvider';
import { formatDateTime } from '@/lib/formatters';
import { useGetNotificationsQuery } from '@/features/notification/services/notification.api';
import { useGetProfileSettingsQuery, useUpdateProfileSettingsMutation } from '../services/profile.api';
import { InvestmentStyle, ProfileSettingsResponse } from '../type/profile.type';

const STRATEGY_OPTIONS: Array<{ value: InvestmentStyle; label: string; description: string }> = [
  { value: 'SAFE', label: 'An toàn', description: 'Ưu tiên ổn định và giảm biến động.' },
  { value: 'BALANCED', label: 'Cân bằng', description: 'Cân bằng giữa lợi nhuận và rủi ro.' },
  { value: 'GROWTH', label: 'Tăng trưởng', description: 'Tập trung vào tăng trưởng dài hạn.' },
];

const getInitials = (name: string) => (name.trim().slice(0, 2).toUpperCase() || 'US');

const toDateInputValue = (value: string | null) => (value ? value.slice(0, 10) : '');

const toDayOfMonth = (value: string) => {
  if (!value) return null;
  const parsed = new Date(value);
  return Number.isNaN(parsed.getTime()) ? null : parsed.getDate();
};

const ProfilePage: React.FC = () => {
  const { t, pick, locale } = useI18n();
  const { data: profile, isLoading, isError, refetch } = useGetProfileSettingsQuery();
  const { data: notifications = [] } = useGetNotificationsQuery();
  const [updateProfileSettings, { isLoading: isSaving }] = useUpdateProfileSettingsMutation();
  const [settings, setSettings] = useState<ProfileSettingsResponse | null>(null);
  const [saved, setSaved] = useState(false);

  useEffect(() => {
    if (profile) {
      setSettings(profile);
    }
  }, [profile]);

  const unreadCount = useMemo(() => notifications.filter((notification) => notification.status === 'NEW').length, [notifications]);

  const handleSave = async () => {
    if (!settings) {
      return;
    }

    await updateProfileSettings({
      fullName: settings.fullName?.trim() || null,
      avatarUrl: settings.avatarUrl?.trim() || null,
      autoInvestEnabled: settings.autoInvestEnabled,
      investmentStyle: settings.investmentStyle,
      autoInvestDayOfMonth: settings.autoInvestDayOfMonth,
    }).unwrap();

    await refetch();
    setSaved(true);
    window.setTimeout(() => setSaved(false), 2000);
  };

  if (isLoading && !settings) {
    return (
      <div className="min-h-screen bg-slate-50 px-4 py-10 flex items-center justify-center">
        <div className="rounded-2xl border border-slate-200 bg-white px-6 py-4 text-sm text-slate-500 shadow-sm">{t('common.loading')}</div>
      </div>
    );
  }

  if (isError || !settings) {
    return (
      <div className="min-h-screen bg-slate-50 px-4 py-10 flex items-center justify-center">
        <div className="rounded-2xl border border-red-200 bg-white px-6 py-4 text-sm text-red-700 shadow-sm">
          {pick({ vi: 'Khong tai duoc cai dat ho so.', en: 'Unable to load profile settings.', ja: 'プロフィール設定を読み込めません。' })}
        </div>
      </div>
    );
  }

  const previewAvatar = settings.avatarUrl?.trim() ?? '';
  const previewInitials = getInitials(settings.fullName || 'User');

  return (
    <div className="min-h-screen bg-slate-50 px-4 py-10 animate-fade-up">
      <div className="mx-auto max-w-5xl">
        <div className="mb-8">
          <div>
            <h1 className="text-3xl font-bold tracking-tight text-slate-900">{t('profile.pageTitle')}</h1>
            <p className="mt-2 max-w-2xl text-sm text-slate-500">{t('profile.pageSubtitle')}</p>
          </div>
        </div>

        <div className="grid gap-6 lg:grid-cols-[1.2fr_0.8fr]">
          <div className="space-y-6">
            <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm transition-all duration-150 ease-out hover:-translate-y-0.5 hover:shadow-md">
              <div className="mb-6 flex items-center justify-between">
                <div>
                  <p className="text-xs font-semibold uppercase tracking-[0.2em] text-emerald-600">{t('profile.preview')}</p>
                  <h2 className="mt-1 text-xl font-bold text-slate-900">{settings.fullName || t('toolbar.profile')}</h2>
                </div>
                <div className="rounded-2xl bg-slate-50 px-3 py-2 text-right text-xs text-slate-500">
                  <p>{t('toolbar.notifications')}</p>
                  <p className="mt-1 text-sm font-bold text-slate-900">{unreadCount}</p>
                </div>
              </div>

              <div className="flex flex-col gap-4 rounded-2xl bg-slate-900 p-5 text-white transition-transform duration-150 ease-out sm:flex-row sm:items-center hover:translate-y-[-1px]">
                <div className="flex h-20 w-20 shrink-0 items-center justify-center overflow-hidden rounded-2xl bg-white/10 text-2xl font-bold">
                  {previewAvatar ? (
                    <img src={previewAvatar} alt={settings.fullName || 'Avatar'} className="h-full w-full object-cover" />
                  ) : (
                    previewInitials
                  )}
                </div>
                <div className="flex-1">
                  <p className="text-sm text-white/60">{settings.autoInvestEnabled ? t('profile.autoMode') : t('profile.manualMode')}</p>
                  <p className="mt-1 text-2xl font-bold">{settings.fullName || 'Unnamed user'}</p>
                  <p className="mt-2 text-sm text-white/75">
                    {settings.nextAutoInvestAt ? `${t('profile.autoDate')}: ${formatDateTime(settings.nextAutoInvestAt, locale)}` : t('profile.reminderSubtitle')}
                  </p>
                </div>
              </div>
            </div>

            <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm transition-all duration-150 ease-out hover:-translate-y-0.5 hover:shadow-md">
              <div className="mb-5 flex items-center justify-between">
                <div>
                  <h2 className="text-lg font-bold text-slate-900">{t('profile.pageTitle')}</h2>
                  <p className="text-sm text-slate-500">{t('profile.reminderSubtitle')}</p>
                </div>
                <span className={`rounded-full px-3 py-1 text-xs font-semibold ${settings.autoInvestEnabled ? 'bg-emerald-50 text-emerald-700' : 'bg-slate-100 text-slate-600'}`}>
                  {settings.autoInvestEnabled ? t('profile.autoMode') : t('profile.manualMode')}
                </span>
              </div>

              <div className="grid gap-5">
                <Input
                  label={t('profile.name')}
                  value={settings.fullName ?? ''}
                  onChange={(event) => setSettings((current) => (current ? { ...current, fullName: event.target.value } : current))}
                  placeholder={pick({ vi: 'Nhap ten hien thi', en: 'Enter a display name', ja: '表示名を入力' })}
                />

                <Input
                  label={t('profile.avatar')}
                  value={settings.avatarUrl ?? ''}
                  onChange={(event) => setSettings((current) => (current ? { ...current, avatarUrl: event.target.value } : current))}
                  placeholder="https://..."
                  hint={pick({ vi: 'De trong de dung avatar chu cai dau.', en: 'Leave empty to use initials.', ja: '空欄ならイニシャルを表示します。' })}
                />

                <div className="grid gap-2">
                  <label className="text-sm font-medium text-slate-700">{t('profile.autoInvest')}</label>
                  <button
                    type="button"
                    onClick={() => setSettings((current) => (current ? { ...current, autoInvestEnabled: !current.autoInvestEnabled } : current))}
                    className={`relative inline-flex h-12 w-full items-center rounded-2xl border px-4 transition-all duration-150 ease-out hover:-translate-y-0.5 hover:shadow-sm active:translate-y-0 active:scale-[0.99] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-emerald-500/30 ${settings.autoInvestEnabled ? 'border-emerald-200 bg-emerald-50' : 'border-slate-200 bg-slate-50'}`}
                  >
                    <span className={`inline-flex h-7 w-12 items-center rounded-full p-1 transition-colors duration-150 ${settings.autoInvestEnabled ? 'bg-emerald-600' : 'bg-slate-300'}`}>
                      <span className={`h-5 w-5 rounded-full bg-white shadow-sm transition-transform duration-150 ${settings.autoInvestEnabled ? 'translate-x-5' : 'translate-x-0'}`} />
                    </span>
                    <span className="ml-3 text-left text-sm font-semibold text-slate-900">
                      {settings.autoInvestEnabled ? t('profile.autoMode') : t('profile.manualMode')}
                    </span>
                  </button>
                </div>

                <Select
                  label={t('profile.strategy')}
                  value={settings.investmentStyle ?? 'BALANCED'}
                  onChange={(event) => setSettings((current) => (current ? { ...current, investmentStyle: event.target.value as InvestmentStyle } : current))}
                >
                  {STRATEGY_OPTIONS.map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </Select>

                <div className="grid gap-2">
                  <label className="text-sm font-medium text-slate-700">{t('profile.autoDate')}</label>
                  <input
                    type="date"
                    value={toDateInputValue(settings.nextAutoInvestAt)}
                    onChange={(event) =>
                      setSettings((current) =>
                        current
                          ? {
                              ...current,
                              autoInvestDayOfMonth: toDayOfMonth(event.target.value),
                              nextAutoInvestAt: event.target.value || null,
                            }
                          : current,
                      )
                    }
                    className="h-11 rounded-lg border border-slate-300 bg-white px-3 text-sm text-slate-900 outline-none transition-all duration-150 ease-out hover:border-slate-400 focus:border-emerald-500 focus:ring-2 focus:ring-emerald-500/30"
                  />
                </div>

                <div className="flex items-center justify-between gap-3 pt-2">
                  <div className="text-xs text-slate-500">
                    {settings.autoInvestEnabled ? t('profile.autoMode') : t('profile.manualMode')}
                    <br />
                    {settings.nextAutoInvestAt ? `${t('profile.autoDate')}: ${formatDateTime(settings.nextAutoInvestAt, locale)}` : t('profile.reminderSubtitle')}
                  </div>
                  <Button onClick={handleSave} loading={isSaving} leftIcon={null} rightIcon={null}>
                    {t('profile.save')}
                  </Button>
                </div>

                {saved && (
                  <div className="rounded-2xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">
                    {t('profile.saved')}
                  </div>
                )}
              </div>
            </div>
          </div>

          <div className="space-y-6">
            <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm transition-all duration-150 ease-out hover:-translate-y-0.5 hover:shadow-md">
              <h3 className="text-base font-bold text-slate-900">{t('toolbar.notifications')}</h3>
              <p className="mt-1 text-sm text-slate-500">{t('profile.reminderSubtitle')}</p>

              <div className="mt-4 space-y-3">
                {notifications.slice(0, 3).map((notification) => (
                  <div key={notification.id} className={`rounded-2xl border px-4 py-3 transition-all duration-150 ease-out hover:-translate-y-0.5 hover:shadow-sm ${notification.status === 'NEW' ? 'border-emerald-200 bg-emerald-50/50' : 'border-slate-200 bg-slate-50'}`}>
                    <div className="flex items-start justify-between gap-2">
                      <div>
                        <p className="text-sm font-semibold text-slate-900">{notification.title}</p>
                        <p className="mt-1 text-xs text-slate-600">{notification.message}</p>
                      </div>
                      <span className="text-[11px] text-slate-400">{notification.dueDate ? formatDateTime(notification.dueDate, locale) : ''}</span>
                    </div>
                  </div>
                ))}

                {notifications.length === 0 && (
                  <div className="rounded-2xl border border-dashed border-slate-200 px-4 py-6 text-center text-sm text-slate-500">
                    {t('toolbar.emptyNotifications')}
                  </div>
                )}
              </div>
            </div>

            <div className="rounded-3xl border border-slate-200 bg-slate-900 p-6 text-white shadow-sm transition-all duration-150 ease-out hover:-translate-y-0.5 hover:shadow-md">
              <h3 className="text-base font-bold">{pick({ vi: 'Tom tat nhanh', en: 'Quick summary', ja: 'クイック概要' })}</h3>
              <div className="mt-4 grid gap-3 text-sm text-white/80">
                {STRATEGY_OPTIONS.map((option) => (
                  <div key={option.value} className={`rounded-2xl border px-4 py-3 transition-all duration-150 ease-out hover:-translate-y-0.5 ${settings.investmentStyle === option.value ? 'border-emerald-400 bg-emerald-500/10' : 'border-white/10 bg-white/5'}`}>
                    <p className="font-semibold text-white">{option.label}</p>
                    <p className="mt-1 text-xs text-white/70">{option.description}</p>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;