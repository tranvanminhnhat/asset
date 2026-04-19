export interface LogoutResponse {
  data: {
    message: string;
  };
  message: {
    code: string;
    message: string;
    service: string;
  };
}
