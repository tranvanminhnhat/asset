'use client';

import React from 'react';
import { useI18n } from '@/common/i18n/I18nProvider';
import { NotificationResponse } from '@/features/notification/type/notification.type';
import { formatDateTime } from '@/lib/formatters';

interface NotificationDropdownProps {
  open: boolean;
  notifications: NotificationResponse[];
  onClose: () => void;
  onMarkAllRead: () => void;
  onSelectNotification: (notification: NotificationResponse) => void;
}

const NotificationDropdown: React.FC<NotificationDropdownProps> = ({
  open,
  notifications,
  onClose,
  onMarkAllRead,
  onSelectNotification,
}) => {
  const { locale } = useI18n();

  if (!open) {
    return null;
  }

  const unreadCount = notifications.filter((notification) => notification.status === 'NEW').length;

  return (
    <div className="absolute right-0 top-12 w-[22rem] overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-xl ring-1 ring-black/5">
      <div className="flex items-center justify-between border-b border-slate-100 px-4 py-3">
        <div>
          <p className="text-sm font-semibold text-slate-900">Notifications</p>
          <p className="text-xs text-slate-500">{unreadCount} unread</p>
        </div>
        <button
          onClick={onMarkAllRead}
          className="rounded-full px-2 py-1 text-xs font-semibold text-emerald-700 transition-all duration-150 ease-out hover:bg-emerald-50 hover:text-emerald-800 active:scale-[0.98] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-emerald-500/30"
        >
          Mark all read
        </button>
      </div>

      <div className="max-h-96 overflow-y-auto">
        {notifications.length === 0 ? (
          <div className="px-4 py-8 text-center text-sm text-slate-500">No new notifications.</div>
        ) : (
          notifications.map((notification) => (
            <button
              key={notification.id}
              type="button"
              onClick={() => onSelectNotification(notification)}
              className={`group w-full border-b border-slate-100 px-4 py-3 text-left transition-all duration-150 ease-out hover:-translate-y-[1px] hover:bg-slate-50 active:translate-y-0 active:scale-[0.998] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-emerald-500/20 ${notification.status === 'NEW' ? 'bg-emerald-50/40' : 'bg-white'}`}
            >
              <div className="flex items-start gap-3">
                <span
                  className={`mt-1 h-2.5 w-2.5 rounded-full transition-colors duration-150 ${notification.status === 'NEW' ? 'bg-emerald-500 group-hover:bg-emerald-600' : 'bg-slate-300 group-hover:bg-slate-400'}`}
                />
                <div className="min-w-0 flex-1">
                  <div className="flex items-center justify-between gap-2">
                    <p className="truncate text-sm font-semibold text-slate-900">{notification.title}</p>
                    <span className="shrink-0 text-[11px] text-slate-400">{notification.createdAt ? formatDateTime(notification.createdAt, locale) : ''}</span>
                  </div>
                  <p className="mt-1 text-xs leading-5 text-slate-600">{notification.message}</p>
                  <div className="mt-2 flex items-center gap-2 text-[11px] font-semibold text-emerald-700">
                    <span>{notification.targetFlow === 'AUTO_INVEST' ? 'Auto invest' : 'Manual flow'}</span>
                    <span className="text-slate-300">•</span>
                    <span>{notification.mode}</span>
                  </div>
                </div>
              </div>
            </button>
          ))
        )}
      </div>

      <div className="flex items-center justify-between border-t border-slate-100 px-4 py-3 text-xs text-slate-500">
        <button
          onClick={onClose}
          className="rounded-full px-2 py-1 font-semibold text-slate-600 transition-all duration-150 ease-out hover:bg-slate-100 hover:text-slate-900 active:scale-[0.98] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-slate-400/30"
        >
          Close
        </button>
        <span>{notifications.length} items</span>
      </div>
    </div>
  );
};

export default NotificationDropdown;
