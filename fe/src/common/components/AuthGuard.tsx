'use client';

import { usePathname, useRouter } from 'next/navigation';
import { useEffect } from 'react';

const PUBLIC_ROUTES = ['/login', '/register'];

const hasCookie = (name: string) => {
  if (typeof document === 'undefined') {
    return false;
  }

  return document.cookie
    .split(';')
    .map((cookie) => cookie.trim())
    .some((cookie) => cookie.startsWith(`${name}=`));
};

const isPublicRoute = (pathname: string) => {
  return PUBLIC_ROUTES.some((route) => pathname === route || pathname.startsWith(`${route}/`));
};

const AuthGuard = ({ children }: { children: React.ReactNode }) => {
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    const token = hasCookie('accessToken') || Boolean(localStorage.getItem('accessToken'));
    const loggedOut = hasCookie('isLoggedOut');
    const authenticated = token && !loggedOut;
    const currentIsPublic = isPublicRoute(pathname);

    if (!authenticated && !currentIsPublic) {
      router.replace(`/login?from=${encodeURIComponent(pathname)}`);
      return;
    }

    if (authenticated && currentIsPublic) {
      router.replace('/dashboard');
    }
  }, [pathname, router]);

  return <>{children}</>;
};

export default AuthGuard;
