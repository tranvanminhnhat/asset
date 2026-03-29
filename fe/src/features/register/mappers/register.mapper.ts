// map register response from backend to frontend model
import { RegisterResponse } from '../type/register.response';

export interface RegisterModel {
  id: string;
  email: string;
  status: string;
}

export const mapRegisterResponseToModel = (response: RegisterResponse): RegisterModel => {
  return {
    id: response.data.userId,
    email: response.data.email,
    status: response.data.status,
  };
};
