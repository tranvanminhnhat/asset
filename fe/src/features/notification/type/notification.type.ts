export interface NotificationResponse {
  id: number;
  type: string;
  title: string;
  message: string;
  mode: string;
  targetFlow: 'AUTO_INVEST' | 'MANUAL_INVESTMENT' | string;
  status: string;
  payload: string | null;
  dueDate: string | null;
  readAt: string | null;
  actionedAt: string | null;
  doneAt: string | null;
  createdAt: string | null;
}