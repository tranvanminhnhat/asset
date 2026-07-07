'use client';

import React, { useEffect, useMemo, useRef, useState } from 'react';
import { usePathname, useRouter } from 'next/navigation';
import LanguageSwitcher from '@/common/components/LanguageSwitcher';
import NotificationDropdown from '@/common/components/NotificationDropdown';
import AutoInvestModal from '@/common/components/AutoInvestModal';
import { useI18n } from '@/common/i18n/I18nProvider';
import { hasAuthToken, isLoggedOut } from '@/common/utils/authSession';
import { useGetProfileSettingsQuery } from '@/features/profile/services/profile.api';
import {
  useGetNotificationsQuery,
  useMarkNotificationActionedMutation,
  useMarkNotificationDoneMutation,
  useMarkNotificationReadMutation,
} from '@/features/notification/services/notification.api';
import { NotificationResponse } from '@/features/notification/type/notification.type';
import { useExecuteAutoInvestMutation } from '@/features/investment-auto/services/investment-auto.api';

const PUBLIC_ROUTES = ['/login', '/register'];

const isPublicRoute = (pathname: string) => PUBLIC_ROUTES.some((route) => pathname === route || pathname.startsWith(`${route}/`));

const getAvatarInitial = (name: string) => (name.trim().slice(0, 1).toUpperCase() || 'U');

const AppTopRightToolbar: React.FC = () => {
  const router = useRouter();
  const pathname = usePathname();
  const { t } = useI18n();
  const containerRef = useRef<HTMLDivElement | null>(null);
  const authenticated = hasAuthToken() && !isLoggedOut();
  const shouldShowProtectedShell = authenticated && !isPublicRoute(pathname);

  const { data: profile } = useGetProfileSettingsQuery(undefined, { skip: !shouldShowProtectedShell });
  const { data: notifications = [], refetch: refetchNotifications } = useGetNotificationsQuery(undefined, {
    skip: !shouldShowProtectedShell,
    pollingInterval: shouldShowProtectedShell ? 60000 : undefined,
  });
  const [markNotificationRead] = useMarkNotificationReadMutation();
  const [markNotificationActioned] = useMarkNotificationActionedMutation();
  const [markNotificationDone] = useMarkNotificationDoneMutation();
  const [executeAutoInvest] = useExecuteAutoInvestMutation();

  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [autoInvestOpen, setAutoInvestOpen] = useState(false);
  const [selectedNotification, setSelectedNotification] = useState<NotificationResponse | null>(null);

  const unreadCount = useMemo(
    () => notifications.filter((notification) => notification.status === 'NEW').length,
    [notifications],
  );
  const avatarInitial = getAvatarInitial(profile?.fullName || 'User');

  useEffect(() => {
    const handleDocumentClick = (event: MouseEvent) => {
      if (!containerRef.current) {
        return;
      }

      if (!containerRef.current.contains(event.target as Node)) {
        setDropdownOpen(false);
      }
    };

    document.addEventListener('mousedown', handleDocumentClick);

    return () => {
      document.removeEventListener('mousedown', handleDocumentClick);
    };
  }, []);

  const handleSelectNotification = async (notification: NotificationResponse) => {
    setSelectedNotification(notification);
    setDropdownOpen(false);

    await markNotificationRead(notification.id);
    await refetchNotifications();

    if (notification.targetFlow === 'AUTO_INVEST') {
      await markNotificationActioned(notification.id);
      setAutoInvestOpen(true);
      return;
    }

    await markNotificationActioned(notification.id);
    router.push('/investment-amount');
  };

  const handleSubmitAutoInvest = async (amount: number): Promise<void> => {
    await executeAutoInvest({ amount }).unwrap();

    if (selectedNotification) {
      await markNotificationDone(selectedNotification.id);
      setSelectedNotification(null);
    }

    await refetchNotifications();
    setAutoInvestOpen(false);
    setDropdownOpen(false);
    router.push('/investment-complete');
  };

  if (!shouldShowProtectedShell) {
    return (
      <div className="fixed right-3 top-3 z-50">
        <LanguageSwitcher />
      </div>
    );
  }

  return (
    <>
      <button
        type="button"
        onClick={() => router.push('/dashboard')}
        className="fixed left-3 top-3 z-50 inline-flex h-10 items-center gap-2 rounded-xl border border-slate-200 bg-white/95 px-3 shadow-sm backdrop-blur transition-all duration-150 ease-out hover:-translate-y-0.5 hover:bg-slate-50 hover:shadow-md active:translate-y-0 active:scale-[0.98] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-emerald-500/40"
        aria-label="Go to dashboard"
      >
        <span className="inline-flex h-6 w-6 items-center justify-center rounded-lg bg-slate-900 text-white">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" className="h-3.5 w-3.5" stroke="currentColor" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round" d="M4 19V9m8 10V5m8 14v-7" />
          </svg>
        </span>
        <span className="hidden text-xs font-semibold text-slate-700 sm:inline">Asset</span>
      </button>

      <div ref={containerRef} className="fixed right-3 top-3 z-50 flex items-start gap-2">
        <LanguageSwitcher />

        <div className="relative">
          <button
            type="button"
            onClick={() => setDropdownOpen((open) => !open)}
            className="relative inline-flex h-10 w-10 items-center justify-center rounded-xl border border-slate-200 bg-white/95 shadow-sm backdrop-blur transition-all duration-150 ease-out hover:-translate-y-0.5 hover:bg-slate-50 hover:shadow-md active:translate-y-0 active:scale-[0.98] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-emerald-500/40"
            aria-label={t('toolbar.notifications')}
          >
            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-slate-700" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C8.67 6.165 7 8.388 7 11v3.159c0 .538-.214 1.055-.595 1.436L5 17h5m5 0a3 3 0 11-6 0h6z" />
            </svg>
            {unreadCount > 0 && (
              <span className="absolute -right-1 -top-1 inline-flex min-w-5 items-center justify-center rounded-full bg-rose-500 px-1.5 py-0.5 text-[10px] font-bold text-white">
                {unreadCount}
              </span>
            )}
          </button>

          <NotificationDropdown
            open={dropdownOpen}
            notifications={notifications}
            onClose={() => setDropdownOpen(false)}
            onMarkAllRead={async () => {
              await Promise.all(notifications.filter((notification) => notification.status === 'NEW').map((notification) => markNotificationRead(notification.id)));
              await refetchNotifications();
            }}
            onSelectNotification={handleSelectNotification}
          />
        </div>

        <button
          type="button"
          onClick={() => router.push('/profile')}
          className="inline-flex h-10 items-center gap-2 rounded-xl border border-slate-200 bg-white/95 px-3 shadow-sm backdrop-blur transition-all duration-150 ease-out hover:-translate-y-0.5 hover:bg-slate-50 hover:shadow-md active:translate-y-0 active:scale-[0.98] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-emerald-500/40"
          aria-label={t('toolbar.profile')}
        >
          <span className="flex h-7 w-7 items-center justify-center overflow-hidden rounded-full bg-slate-900 text-xs font-bold text-white">
            {profile?.avatarUrl ? (
              <img src={profile.avatarUrl} alt={profile.fullName || 'Profile'} className="h-7 w-7 rounded-full object-cover" />
            ) : (
              avatarInitial
            )}
          </span>
          <span className="hidden text-xs font-semibold text-slate-600 sm:inline">{profile?.fullName || t('toolbar.profile')}</span>
        </button>

        <AutoInvestModal
          open={autoInvestOpen}
          profile={profile ?? null}
          onClose={() => setAutoInvestOpen(false)}
          onSubmit={handleSubmitAutoInvest}
        />
      </div>
    </>
  );
};

export default AppTopRightToolbar;
