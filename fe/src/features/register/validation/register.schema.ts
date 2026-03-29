import { z } from 'zod';

export const registerSchema = z.object({
  email: z.string().min(5).max(255).email(),
  password: z.string().min(12).max(64),
  confirmPassword: z.string().min(12).max(64),
}).refine((data) => data.password === data.confirmPassword, {
  message: "Passwords don't match",
  path: ["confirmPassword"],
});

export type RegisterForm = z.infer<typeof registerSchema>;
