import { baseApi } from '@/store/api/baseApi';
import { LogoutResponse } from '../type/logout.type';

export const logoutApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    logout: builder.mutation<LogoutResponse, void>({
      query: () => ({
        url: '/auth/logout',
        method: 'POST',
      }),
    }),
  }),
});

export const { useLogoutMutation } = logoutApi;
