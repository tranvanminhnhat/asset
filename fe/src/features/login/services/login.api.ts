import { baseApi } from '@/store/api/baseApi';
import { LoginRequest } from '../type/login.request';
import { LoginResponse } from '../type/login.response';

export const loginApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    login: builder.mutation<LoginResponse, LoginRequest>({
      query: (body) => ({
        url: '/auth/login',
        method: 'POST',
        body,
      }),
    }),
  }),
});

export const { useLoginMutation } = loginApi;
