'use client';

import { usePathname, useRouter } from 'next/navigation';
import { useEffect } from 'react';
import { hasAuthToken, isLoggedOut } from '@/common/utils/authSession';

const PUBLIC_ROUTES = ['/login', '/register'];

const isPublicRoute = (pathname: string) => {
  return PUBLIC_ROUTES.some((route) => pathname === route || pathname.startsWith(`${route}/`));
};

const AuthGuard = ({ children }: { children: React.ReactNode }) => {
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    const token = hasAuthToken();
    const loggedOut = isLoggedOut();
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
