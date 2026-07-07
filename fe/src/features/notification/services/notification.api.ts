import { baseApi } from '@/store/api/baseApi';
import { NotificationResponse } from '../type/notification.type';

export const notificationApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getNotifications: builder.query<NotificationResponse[], void>({
      query: () => ({ url: '/api/v1/notifications', method: 'GET' }),
      transformResponse: (response: { data?: NotificationResponse[] }) => response.data ?? [],
      providesTags: ['Notifications'],
    }),
    getUnreadNotifications: builder.query<NotificationResponse[], void>({
      query: () => ({ url: '/api/v1/notifications/unread', method: 'GET' }),
      transformResponse: (response: { data?: NotificationResponse[] }) => response.data ?? [],
      providesTags: ['Notifications'],
    }),
    markNotificationRead: builder.mutation<NotificationResponse, number>({
      query: (id) => ({ url: `/api/v1/notifications/${id}/read`, method: 'POST' }),
      invalidatesTags: ['Notifications'],
    }),
    markNotificationActioned: builder.mutation<NotificationResponse, number>({
      query: (id) => ({ url: `/api/v1/notifications/${id}/action`, method: 'POST' }),
      invalidatesTags: ['Notifications'],
    }),
    markNotificationDone: builder.mutation<NotificationResponse, number>({
      query: (id) => ({ url: `/api/v1/notifications/${id}/done`, method: 'POST' }),
      invalidatesTags: ['Notifications'],
    }),
  }),
});

export const {
  useGetNotificationsQuery,
  useGetUnreadNotificationsQuery,
  useMarkNotificationReadMutation,
  useMarkNotificationActionedMutation,
  useMarkNotificationDoneMutation,
} = notificationApi;