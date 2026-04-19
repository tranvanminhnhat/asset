import { useRouter } from 'next/navigation';
import { useLogoutMutation } from '../services/logout.api';

const CLEAR_KEYS = ['accessToken', 'investmentAllocations', 'investmentCompleteResult'];

const clearSession = () => {
  CLEAR_KEYS.forEach((key) => localStorage.removeItem(key));
  document.cookie = 'accessToken=; path=/; max-age=0; samesite=lax';
  document.cookie = 'accessToken=; path=/; max-age=0; samesite=lax; secure';
  document.cookie = 'isLoggedOut=1; path=/; max-age=86400; samesite=lax';
};

export const useLogout = () => {
  const router = useRouter();
  const [logout, { isLoading }] = useLogoutMutation();

  const handleLogout = async () => {
    try {
      await logout().unwrap();
    } catch {
      // Ignore API error — session is cleared client-side regardless
    } finally {
      clearSession();
      router.push('/login');
    }
  };

  return { handleLogout, isLoading };
};
