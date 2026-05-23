'use client';
import React, { useState } from 'react';
import { AllocationMasterResponse } from '../type/allocation-master.type';
import Badge from '@/common/components/ui/Badge';

interface Props {
  data: AllocationMasterResponse[];
  onEdit: (item: AllocationMasterResponse) => void;
  onDelete: (item: AllocationMasterResponse) => void;
}

const AllocationMasterTable: React.FC<Props> = ({ data, onEdit, onDelete }) => {
  const [expandedId, setExpandedId] = useState<number | null>(null);

  const toggleExpand = (id: number) => {
    setExpandedId((prev) => (prev === id ? null : id));
  };

  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
      <div className="overflow-x-auto">
        <table className="min-w-full">
          <thead>
            <tr className="bg-slate-900">
              <th className="px-5 py-3.5 text-left text-[11px] font-semibold text-slate-400 uppercase tracking-wider">ID</th>
              <th className="px-5 py-3.5 text-left text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Code</th>
              <th className="px-5 py-3.5 text-left text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Name</th>
              <th className="px-5 py-3.5 text-left text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Ngày tạo</th>
              <th className="px-5 py-3.5 text-left text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Cập nhật</th>
              <th className="px-5 py-3.5 text-left text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Chi tiết</th>
              <th className="px-5 py-3.5 text-center text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Thao tác</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {data.map((item) => (
              <React.Fragment key={item.id}>
                <tr className="hover:bg-slate-50/60 transition-colors duration-100">
                  <td className="px-5 py-3.5 text-sm text-slate-500 font-mono">#{item.id}</td>
                  <td className="px-5 py-3.5">
                    <span className="inline-flex items-center px-2.5 py-1 rounded-md bg-slate-100 text-slate-700 text-xs font-semibold font-mono tracking-wide">
                      {item.code}
                    </span>
                  </td>
                  <td className="px-5 py-3.5 text-sm font-medium text-slate-900">{item.name || '—'}</td>
                  <td className="px-5 py-3.5 text-sm text-slate-500 whitespace-nowrap">
                    {new Date(item.createdAt).toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' })}
                  </td>
                  <td className="px-5 py-3.5 text-sm text-slate-500 whitespace-nowrap">
                    {new Date(item.updatedAt).toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' })}
                  </td>
                  <td className="px-5 py-3.5 text-sm">
                    <button
                      onClick={() => toggleExpand(item.id)}
                      className="inline-flex items-center gap-1.5 text-xs font-medium text-emerald-600 hover:text-emerald-800 transition-colors"
                    >
                      {expandedId === item.id ? (
                        <>
                          <svg xmlns="http://www.w3.org/2000/svg" className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                            <path strokeLinecap="round" strokeLinejoin="round" d="M5 15l7-7 7 7" />
                          </svg>
                          Ẩn
                        </>
                      ) : (
                        <>
                          <svg xmlns="http://www.w3.org/2000/svg" className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                            <path strokeLinecap="round" strokeLinejoin="round" d="M19 9l-7 7-7-7" />
                          </svg>
                          Xem ({item.details.length})
                        </>
                      )}
                    </button>
                  </td>
                  <td className="px-5 py-3.5">
                    <div className="flex items-center justify-center gap-1.5">
                      <button
                        onClick={() => onEdit(item)}
                        className="inline-flex items-center gap-1 px-3 py-1.5 text-xs rounded-lg font-medium bg-blue-50 text-blue-700 hover:bg-blue-100 border border-blue-200/80 transition-colors"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                          <path strokeLinecap="round" strokeLinejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                        </svg>
                        Sửa
                      </button>
                      <button
                        onClick={() => onDelete(item)}
                        className="inline-flex items-center gap-1 px-3 py-1.5 text-xs rounded-lg font-medium bg-red-50 text-red-700 hover:bg-red-100 border border-red-200/80 transition-colors"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                          <path strokeLinecap="round" strokeLinejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                        </svg>
                        Xoá
                      </button>
                    </div>
                  </td>
                </tr>

                {/* Expandable detail row */}
                {expandedId === item.id && item.details.length > 0 && (
                  <tr>
                    <td colSpan={7} className="p-0">
                      <div className="bg-slate-50 border-t border-b border-slate-200 px-5 py-4">
                        <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider mb-3">
                          Chi tiết phân bổ — {item.details.length} dòng
                        </p>
                        <div className="rounded-lg overflow-hidden border border-slate-200">
                          <table className="min-w-full">
                            <thead className="bg-slate-100">
                              <tr>
                                {['ID', 'Strategy ID', 'Loại tài sản', 'Tỷ lệ (%)', 'Lot tối thiểu'].map((h) => (
                                  <th key={h} className="px-4 py-2.5 text-left text-[11px] font-semibold text-slate-600 uppercase tracking-wide">
                                    {h}
                                  </th>
                                ))}
                              </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-slate-100">
                              {item.details.map((detail) => (
                                <tr key={detail.id} className="hover:bg-slate-50 transition-colors">
                                  <td className="px-4 py-2.5 text-xs text-slate-500 font-mono">#{detail.id}</td>
                                  <td className="px-4 py-2.5 text-xs text-slate-600">{detail.strategyId}</td>
                                  <td className="px-4 py-2.5">
                                    <Badge variant="info">{detail.assetType}</Badge>
                                  </td>
                                  <td className="px-4 py-2.5 text-xs font-semibold text-emerald-700">
                                    {detail.percentage}%
                                  </td>
                                  <td className="px-4 py-2.5 text-xs text-slate-600">
                                    {Number(detail.minimumLotSizeAmount).toLocaleString('vi-VN')}
                                  </td>
                                </tr>
                              ))}
                            </tbody>
                          </table>
                        </div>
                      </div>
                    </td>
                  </tr>
                )}
              </React.Fragment>
            ))}

            {data.length === 0 && (
              <tr>
                <td colSpan={7} className="px-5 py-16 text-center">
                  <div className="flex flex-col items-center gap-3 text-slate-400">
                    <svg xmlns="http://www.w3.org/2000/svg" className="w-10 h-10" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1}>
                      <path strokeLinecap="round" strokeLinejoin="round" d="M9 17V7m0 10a2 2 0 01-2 2H5a2 2 0 01-2-2V7a2 2 0 012-2h2a2 2 0 012 2m0 10a2 2 0 002 2h2a2 2 0 002-2M9 7a2 2 0 012-2h2a2 2 0 012 2m0 10V7m0 10a2 2 0 002 2h2a2 2 0 002-2V7a2 2 0 00-2-2h-2a2 2 0 00-2 2" />
                    </svg>
                    <div>
                      <p className="text-sm font-medium text-slate-600">Chưa có dữ liệu</p>
                      <p className="text-xs text-slate-400 mt-0.5">Tạo mới Allocation Master để bắt đầu</p>
                    </div>
                  </div>
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Table footer */}
      {data.length > 0 && (
        <div className="px-5 py-3 border-t border-slate-100 bg-slate-50/50">
          <p className="text-xs text-slate-500">
            Tổng cộng <span className="font-semibold text-slate-700">{data.length}</span> bản ghi
          </p>
        </div>
      )}
    </div>
  );
};

export default AllocationMasterTable;
