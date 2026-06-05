import { RegisterRequest } from '../type/register.request';
import { RegisterResponse } from '../type/register.response';
import { baseApi } from '@/store/api/baseApi';

export const registerApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    register: builder.mutation<RegisterResponse, RegisterRequest>({
      query: (body) => ({
        url: '/auth/register',
        method: 'POST',
        body,
      }),
    }),
  }),
});

export const { useRegisterMutation } = registerApi;
