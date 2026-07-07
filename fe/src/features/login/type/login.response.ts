export interface LoginResponse {
  data: {
    accessToken: string;
    refreshToken: string;
    email: string;
    fullName?: string | null;
    avatarUrl?: string | null;
    status: string;
    userId: string;
    autoInvestEnabled?: boolean | null;
    investmentStyle?: string | null;
    autoInvestDayOfMonth?: number | null;
    flowMode?: 'AUTO' | 'MANUAL' | string | null;
  };
  message: {
    code: string;
    message: string;
    service: string;
  };
}
