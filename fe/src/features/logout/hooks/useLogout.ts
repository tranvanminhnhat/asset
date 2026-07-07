import { useRouter } from 'next/navigation';
import { useLogoutMutation } from '../services/logout.api';
import { clearAuthTokens, setLoggedOutFlag } from '@/common/utils/authSession';

const CLEAR_KEYS = ['investmentAllocations', 'investmentCompleteResult', 'profileSettings', 'appNotifications', 'investmentAutoInvestResult'];

const clearSession = () => {
  CLEAR_KEYS.forEach((key) => localStorage.removeItem(key));
  clearAuthTokens();
  setLoggedOutFlag();
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
