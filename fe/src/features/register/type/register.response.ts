export interface RegisterResponse {
  data: {
    userId: string;
    email: string;
    status: string;
  };
  message: {
    code: string;
    service: string;
    message: string;
  };
}
