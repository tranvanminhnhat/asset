import { z } from 'zod';

export const portfolioSelectionSchema = z.object({
  selectedPortfolio: z.string().min(1, 'Vui lòng chọn danh mục đầu tư.'),
});

export type PortfolioSelectionFormValues = z.infer<typeof portfolioSelectionSchema>;
