'use client';
import React, { useEffect } from 'react';

export type ToastType = 'success' | 'error' | 'info';

interface ToastProps {
  type: ToastType;
  message: string;
  onDismiss: () => void;
}

const icons: Record<ToastType, React.ReactNode> = {
  success: (
    <svg className="w-4 h-4 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
      <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
    </svg>
  ),
  error: (
    <svg className="w-4 h-4 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
      <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
    </svg>
  ),
  info: (
    <svg className="w-4 h-4 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
      <path strokeLinecap="round" strokeLinejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
    </svg>
  ),
};

const bgClasses: Record<ToastType, string> = {
  success: 'bg-emerald-600',
  error: 'bg-red-600',
  info: 'bg-blue-600',
};

const Toast: React.FC<ToastProps> = ({ type, message, onDismiss }) => {
  useEffect(() => {
    const t = setTimeout(onDismiss, 4000);
    return () => clearTimeout(t);
  }, [onDismiss]);

  return (
    <div
      className={[
        'fixed top-5 right-5 z-[9999] flex items-center gap-3 px-4 py-3',
        'rounded-xl shadow-xl text-sm font-medium text-white max-w-sm w-full',
        'animate-toast-in',
        bgClasses[type],
      ].join(' ')}
    >
      <span className="shrink-0 p-0.5 rounded-full bg-white/20">{icons[type]}</span>
      <span className="flex-1 leading-snug">{message}</span>
      <button
        onClick={onDismiss}
        className="shrink-0 opacity-70 hover:opacity-100 transition-opacity"
        aria-label="Dismiss"
      >
        <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
          <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </div>
  );
};

export default Toast;
