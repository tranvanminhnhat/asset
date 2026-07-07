export type InvestmentStyle = 'SAFE' | 'BALANCED' | 'GROWTH';

export interface ProfileSettingsResponse {
  userId: string;
  email: string;
  fullName: string | null;
  avatarUrl: string | null;
  autoInvestEnabled: boolean;
  investmentStyle: string | null;
  autoInvestDayOfMonth: number | null;
  flowMode: 'AUTO' | 'MANUAL';
  nextAutoInvestAt: string | null;
  autoInvestDueToday: boolean;
}

export interface UpdateProfileSettingsRequest {
  fullName?: string | null;
  avatarUrl?: string | null;
  autoInvestEnabled?: boolean;
  investmentStyle?: string | null;
  autoInvestDayOfMonth?: number | null;
}
