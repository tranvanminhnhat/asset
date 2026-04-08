import { z } from 'zod';

export const loginSchema = z.object({
  email: z.string().min(5).max(255).email(),
  password: z.string().min(12).max(64),
});

export type LoginForm = z.infer<typeof loginSchema>;
