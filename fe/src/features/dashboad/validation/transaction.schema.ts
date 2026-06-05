import { z } from 'zod';

const transactionTypes = ['BUY', 'SELL', 'DIVIDEND', 'ADJUSTMENT'] as const;
const assetTypes = ['STOCK', 'ETF', 'GOLD', 'EMERGENCY'] as const;

export const transactionSchema = z.object({
  transactionType: z.enum(transactionTypes),
  assetType: z.enum(assetTypes),
  transactionDate: z.string().min(1, 'Vui lòng chọn ngày giao dịch.'),
  quantity: z.coerce.number().positive('Số lượng phải lớn hơn 0.'),
  unitPrice: z.coerce.number().min(0, 'Đơn giá không được âm.'),
  referenceId: z.string().optional(),
  notes: z.string().optional(),
});

export type TransactionFormValues = z.infer<typeof transactionSchema>;
