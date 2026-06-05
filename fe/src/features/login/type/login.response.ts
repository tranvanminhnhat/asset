export interface LoginResponse {
  data: {
    accessToken: string;
    refreshToken: string;
    email: string;
    status: string;
    userId: string;
  };
  message: {
    code: string;
    message: string;
    service: string;
  };
}
