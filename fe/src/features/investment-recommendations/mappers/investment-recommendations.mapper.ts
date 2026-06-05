import { Allocation } from '../../investment-amount/type/investment-amount.type';
import { FundAllocation } from '../type/investment-recommendations.type';

const getFundName = (type: string) => {
  switch (type) {
    case 'EMERGENCY': return 'Quỹ Khẩn cấp';
    case 'GOLD': return 'Quỹ Vàng';
    case 'ETF': return 'Quỹ ETF';
    case 'STOCK': return 'Quỹ Cổ Phiếu';
    default: return type;
  }
};

const getFundDescription = (type: string) => {
  switch (type) {
    case 'EMERGENCY': return 'Đầu tư vào quỹ khẩn cấp với tỷ suất lợi nhuận ổn định.';
    case 'GOLD': return 'Quỹ đầu tư vào vàng với tỷ suất lợi nhuận 8% năm.';
    case 'ETF': return 'Quỹ ETF theo dõi chỉ số chính với lợi nhuận 13% năm.';
    case 'STOCK': return 'Đầu tư lựa chọn các cổ phiếu tốt nhất với lợi nhuận 20% năm.';
    default: return '';
  }
};

export const mapAllocationsToFunds = (allocations: Allocation[]): FundAllocation[] => {
  return allocations.map((alloc, index) => {
    return {
      id: (index + 1).toString(),
      name: getFundName(alloc.type),
      type: alloc.type,
      amount: alloc.amount,
      investableAmount: alloc.investableAmount,
      residualAmount: alloc.residualAmount,
      unitAmount: alloc.amount,
      carryOverAmount: alloc.carryOverAmount,
      quantity: 1,
      totalAmount: alloc.investableAmount,
      currency: 'VND',
      percentage: alloc.percent * 100,
      description: getFundDescription(alloc.type),
    };
  });
};
