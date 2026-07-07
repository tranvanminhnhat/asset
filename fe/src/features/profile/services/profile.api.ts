import { baseApi } from '@/store/api/baseApi';
import { ProfileSettingsResponse, UpdateProfileSettingsRequest } from '../type/profile.type';

interface ProfileApiResponse {
  data?: ProfileSettingsResponse;
}

export const profileApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getProfileSettings: builder.query<ProfileSettingsResponse, void>({
      query: () => ({ url: '/api/v1/profile/settings', method: 'GET' }),
      transformResponse: (response: ProfileApiResponse) => response.data as ProfileSettingsResponse,
      providesTags: ['ProfileSettings'],
    }),
    updateProfileSettings: builder.mutation<ProfileSettingsResponse, UpdateProfileSettingsRequest>({
      query: (body) => ({ url: '/api/v1/profile/settings', method: 'PUT', body }),
      transformResponse: (response: ProfileApiResponse) => response.data as ProfileSettingsResponse,
      invalidatesTags: ['ProfileSettings', 'Notifications'],
    }),
  }),
});

export const { useGetProfileSettingsQuery, useUpdateProfileSettingsMutation } = profileApi;