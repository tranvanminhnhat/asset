'use client';
import React from 'react';
import { AllocationMasterResponse } from '../type/allocation-master.type';
import Modal from '@/common/components/ui/Modal';
import Button from '@/common/components/ui/Button';

interface Props {
  open: boolean;
  target: AllocationMasterResponse | null;
  onClose: () => void;
  onConfirm: (id: number) => void;
  isDeleting?: boolean;
}

const AllocationMasterDeleteModal: React.FC<Props> = ({
  open,
  target,
  onClose,
  onConfirm,
  isDeleting = false,
}) => {
  if (!open || !target) return null;

  return (
    <Modal open={open} onClose={onClose} title="XÃ¡c nháº­n xoÃ¡" maxWidth="max-w-md">
      <div className="px-6 py-5">
        {/* Warning icon */}
        <div className="flex items-center gap-4 mb-5">
          <div className="shrink-0 w-12 h-12 rounded-full bg-red-100 flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" className="w-6 h-6 text-red-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
          </div>
          <div>
            <p className="text-sm font-semibold text-slate-900">HÃ nh Ä‘á»™ng khÃ´ng thá»ƒ hoÃ n tÃ¡c</p>
            <p className="text-sm text-slate-500 mt-0.5">
              Báº¡n sáº¯p xoÃ¡{' '}
              <span className="font-semibold text-slate-800">
                [{target.code}] {target.name}
              </span>
            </p>
          </div>
        </div>

        <div className="rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">
          Táº¥t cáº£ dá»¯ liá»‡u chi tiáº¿t liÃªn quan sáº½ bá»‹ xoÃ¡ vÄ©nh viá»…n.
        </div>
      </div>

      <div className="flex justify-end gap-2.5 px-6 py-4 border-t border-slate-100 bg-slate-50/50 rounded-b-2xl">
        <Button variant="outline" onClick={onClose} disabled={isDeleting}>
          Huá»·
        </Button>
        <Button variant="danger" loading={isDeleting} onClick={() => onConfirm(target.id)}>
          XÃ¡c nháº­n xoÃ¡
        </Button>
      </div>
    </Modal>
  );
};

export default AllocationMasterDeleteModal;
