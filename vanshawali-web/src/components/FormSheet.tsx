import type { FormEvent, ReactNode } from "react";
import { Loader2, X } from "lucide-react";

export function FormSheet({
  title,
  onClose,
  onSubmit,
  submitting,
  submitLabel,
  error,
  children,
}: {
  title: string;
  onClose: () => void;
  onSubmit: (e: FormEvent) => void;
  submitting: boolean;
  submitLabel: string;
  error: string | null;
  children: ReactNode;
}) {
  return (
    <div
      role="dialog"
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4"
      onClick={onClose}
    >
      <form
        onClick={(e) => e.stopPropagation()}
        onSubmit={onSubmit}
        className="w-full max-w-sm space-y-3 rounded-card bg-box p-4 shadow-panel"
      >
        <div className="flex items-center justify-between">
          <h3 className="font-display text-base font-semibold text-indigo">{title}</h3>
          <button type="button" onClick={onClose} className="text-ink-faint">
            <X size={18} />
          </button>
        </div>

        {children}

        {error && <p className="text-[12px] text-sindoor">{error}</p>}

        <button
          type="submit"
          disabled={submitting}
          className="flex w-full items-center justify-center gap-2 rounded-lg bg-indigo py-2.5 text-[13px] font-semibold text-cream transition active:scale-[0.98] disabled:opacity-60"
        >
          {submitting && <Loader2 size={14} className="animate-spin" />}
          {submitLabel}
        </button>
      </form>
    </div>
  );
}

export function Field({ label, children }: { label: string; children: ReactNode }) {
  return (
    <div>
      <label className="mb-1 block text-[11px] text-ink-soft">{label}</label>
      {children}
    </div>
  );
}

export const fieldInputClass =
  "w-full rounded-lg border border-ink/20 bg-white px-2.5 py-2 text-[13px] text-ink outline-none focus:border-indigo";
