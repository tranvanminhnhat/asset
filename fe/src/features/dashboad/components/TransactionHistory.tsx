import React from 'react';
import { formatCurrency, formatDateTime } from '@/lib/formatters';
import { TransactionResponse } from '../type/dashboard.type';

interface TransactionHistoryProps {
  transactions: TransactionResponse[];
}

const TransactionHistory: React.FC<TransactionHistoryProps> = ({ transactions }) => {
  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-semibold text-slate-900">Lịch sử giao dịch</h2>
          <p className="text-sm text-slate-600">Danh sách giao dịch mới nhất từ backend.</p>
        </div>
      </div>

      {transactions.length === 0 ? (
        <div className="rounded-xl border border-dashed border-slate-200 bg-slate-50 p-6 text-sm text-slate-600">
          Chưa có giao dịch nào để hiển thị.
        </div>
      ) : (
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-slate-200">
            <thead>
              <tr className="text-left text-sm text-slate-500">
                <th className="pb-3 pr-4 font-medium">Ngày</th>
                <th className="pb-3 pr-4 font-medium">Loại</th>
                <th className="pb-3 pr-4 font-medium">Asset</th>
                <th className="pb-3 pr-4 font-medium">Số lượng</th>
                <th className="pb-3 pr-4 font-medium">Đơn giá</th>
                <th className="pb-3 pr-4 font-medium">Số tiền</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-sm text-slate-700">
              {transactions.map((transaction) => (
                <tr key={transaction.transactionId}>
                  <td className="py-3 pr-4">{formatDateTime(transaction.transactionDate)}</td>
                  <td className="py-3 pr-4 font-semibold text-slate-900">{transaction.transactionType}</td>
                  <td className="py-3 pr-4">{transaction.assetType}</td>
                  <td className="py-3 pr-4">{transaction.quantity}</td>
                  <td className="py-3 pr-4">{formatCurrency(transaction.unitPrice)}</td>
                  <td className="py-3 pr-4 font-semibold text-slate-900">{formatCurrency(transaction.totalAmount)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default TransactionHistory;