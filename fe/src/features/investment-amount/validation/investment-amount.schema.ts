import { z } from 'zod';

export const investmentAmountSchema = z.object({
  amount: z
    .string()
    .min(1, 'Vui lòng nhập số tiền đầu tư.')
    .refine((val) => Number(val.replace(/[^\d]/g, '')) > 0, 'Số tiền phải lớn hơn 0.'),
  type: z.enum(['SAFE', 'BALANCED', 'GROWTH']),
});

export type InvestmentAmountFormValues = z.infer<typeof investmentAmountSchema>;
