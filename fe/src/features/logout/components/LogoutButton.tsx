'use client';

import React from 'react';
import { useLogout } from '@/features/logout/hooks/useLogout';

interface LogoutButtonProps {
  className?: string;
}

const LogoutButton: React.FC<LogoutButtonProps> = ({ className }) => {
  const { handleLogout, isLoading } = useLogout();

  return (
    <button
      onClick={handleLogout}
      disabled={isLoading}
      className={
        className ??
        'flex items-center gap-2 px-4 py-2 border border-slate-300 text-slate-700 font-semibold rounded-lg hover:bg-slate-50 transition disabled:opacity-50'
      }
    >
      {isLoading ? 'Đang đăng xuất...' : 'Đăng xuất'}
    </button>
  );
};

export default LogoutButton;
