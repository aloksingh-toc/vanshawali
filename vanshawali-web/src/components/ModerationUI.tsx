import type { ReactNode } from "react";
import { Check, Loader2, X } from "lucide-react";

export function Pill({ cls, children }: { cls: string; children: ReactNode }) {
  return <span className={`shrink-0 rounded-full px-2 py-0.5 text-[10px] ${cls}`}>{children}</span>;
}

export const STATUS_LABELS: Record<string, { label: string; cls: string }> = {
  PENDING: { label: "बाकी", cls: "border-haldi/40 bg-haldi-bg text-[#633806]" },
  APPROVED: { label: "स्वीकृत", cls: "border-indigo/30 bg-indigo/5 text-indigo" },
  REJECTED: { label: "अस्वीकृत", cls: "border-sindoor/40 bg-[#FDE8E8] text-[#7A2020]" },
};

export function StatusBadge({ status }: { status: string }) {
  const s = STATUS_LABELS[status];
  if (!s) return null;
  return <Pill cls={`border ${s.cls}`}>{s.label}</Pill>;
}

export function ApproveRejectButtons({
  busy,
  onApprove,
  onReject,
}: {
  busy: boolean;
  onApprove: () => void;
  onReject: () => void;
}) {
  return (
    <div className="mt-2 flex gap-2">
      <button
        onClick={onApprove}
        disabled={busy}
        className="flex flex-1 items-center justify-center gap-1.5 rounded-lg bg-indigo py-2 text-[12px] font-medium text-cream transition active:scale-[0.98] disabled:opacity-60"
      >
        {busy ? <Loader2 size={13} className="animate-spin" /> : <Check size={13} />}
        स्वीकृत
      </button>
      <button
        onClick={onReject}
        disabled={busy}
        className="flex flex-1 items-center justify-center gap-1.5 rounded-lg border border-sindoor/40 bg-[#FDE8E8] py-2 text-[12px] font-medium text-[#7A2020] transition active:scale-[0.98] disabled:opacity-60"
      >
        <X size={13} />
        अस्वीकृत
      </button>
    </div>
  );
}
