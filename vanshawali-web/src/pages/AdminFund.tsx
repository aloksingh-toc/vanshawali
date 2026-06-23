import { useCallback, useEffect, useState } from "react";
import { Coins, Loader2, Pencil, Plus, Trash2 } from "lucide-react";
import { Field, FormSheet, fieldInputClass } from "../components/FormSheet";
import { useAuth } from "../lib/auth";
import { formatINR } from "../lib/format";
import {
  fetchEvents,
  type CommunityEventDetail,
  type FundEntryDetail,
  type FundEntryType,
  type FundEntryWriteInput,
  type FundMode,
} from "../lib/api";

const MODE_OPTIONS: { value: FundMode; label: string }[] = [
  { value: "CASH", label: "नकद" },
  { value: "UPI", label: "UPI" },
  { value: "BANK_TRANSFER", label: "बैंक ट्रांसफर" },
  { value: "CHEQUE", label: "चेक" },
  { value: "OTHER", label: "अन्य" },
];

const MODE_LABEL: Record<FundMode, string> = Object.fromEntries(
  MODE_OPTIONS.map((o) => [o.value, o.label])
) as Record<FundMode, string>;

export default function AdminFund() {
  const { isAdmin, authFetch } = useAuth();
  const [entries, setEntries] = useState<FundEntryDetail[] | null>(null);
  const [events, setEvents] = useState<CommunityEventDetail[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [formTarget, setFormTarget] = useState<FundEntryDetail | "new" | null>(null);
  const [busyId, setBusyId] = useState<number | null>(null);

  const load = useCallback(async () => {
    setError(null);
    try {
      const res = await authFetch("/api/fund/entries");
      if (!res.ok) throw new Error(`सूची लोड नहीं हो सकी (${res.status})`);
      setEntries(await res.json());
    } catch (err) {
      setError((err as Error).message);
    }
  }, [authFetch]);

  useEffect(() => {
    if (isAdmin) {
      load();
      fetchEvents()
        .then(setEvents)
        .catch(() => setEvents([]));
    }
  }, [isAdmin, load]);

  async function handleDelete(id: number) {
    if (!window.confirm("क्या आप इस प्रविष्टि को मिटाना चाहते हैं?")) return;
    setBusyId(id);
    setError(null);
    try {
      const res = await authFetch(`/api/fund/entries/${id}`, { method: "DELETE" });
      if (!res.ok) throw new Error(`मिटाया नहीं जा सका (${res.status})`);
      await load();
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setBusyId(null);
    }
  }


  return (
    <div className="flex h-full flex-col">
      <div className="flex shrink-0 items-center gap-2 border-b border-ink/10 bg-box/85 px-4 py-3 backdrop-blur-sm">
        <Coins size={17} className="text-indigo" />
        <h2 className="flex-1 font-display text-base font-semibold text-indigo">चंदा — प्रबंधन</h2>
        <button
          onClick={() => setFormTarget("new")}
          className="flex items-center gap-1.5 rounded-lg bg-indigo px-2.5 py-1.5 text-[11px] font-medium text-cream transition active:scale-95"
        >
          <Plus size={13} />
          नया
        </button>
      </div>

      <div className="flex-1 overflow-y-auto px-3 py-3">
        {error && <p className="mb-2 text-[12px] text-sindoor">{error}</p>}
        {!entries && !error && (
          <p className="py-6 text-center text-[12px] text-ink-soft">लोड हो रहा है…</p>
        )}
        {entries && entries.length === 0 && (
          <p className="py-6 text-center text-[12px] text-ink-soft">अभी कोई प्रविष्टि नहीं है।</p>
        )}

        <div className="space-y-2.5">
          {entries?.map((entry) => {
            const isExpense = entry.entryType === "EXPENSE";
            return (
              <div
                key={entry.id}
                className="rounded-card border border-ink/10 bg-box/90 p-3 shadow-panel"
              >
                <div className="flex items-start justify-between gap-2">
                  <div className="min-w-0 flex-1">
                    <div className="text-[11px] font-medium text-indigo">
                      {entry.entryDate} · {MODE_LABEL[entry.mode]}
                    </div>
                    <div className="text-[13px] font-medium text-ink">{entry.name}</div>
                    {entry.note && <p className="mt-0.5 text-[12px] text-ink-soft">{entry.note}</p>}
                    {entry.relatedEventTitle && (
                      <p className="mt-1 text-[11px] text-ink-faint">{entry.relatedEventTitle}</p>
                    )}
                  </div>
                  <div className="flex shrink-0 flex-col items-end gap-1.5">
                    <span
                      className={`text-[13px] font-semibold ${
                        isExpense ? "text-[#7A2020]" : "text-[#27500A]"
                      }`}
                    >
                      {isExpense ? "−" : "+"}
                      {formatINR(entry.amount)}
                    </span>
                    <div className="flex gap-1.5">
                      <button
                        onClick={() => setFormTarget(entry)}
                        className="rounded-lg border border-ink/20 bg-white p-1.5 text-ink transition active:scale-95"
                        aria-label="संपादित करें"
                      >
                        <Pencil size={14} />
                      </button>
                      <button
                        onClick={() => handleDelete(entry.id)}
                        disabled={busyId === entry.id}
                        className="rounded-lg border border-sindoor/40 bg-[#FDE8E8] p-1.5 text-[#7A2020] transition active:scale-95 disabled:opacity-60"
                        aria-label="मिटाएं"
                      >
                        {busyId === entry.id ? (
                          <Loader2 size={14} className="animate-spin" />
                        ) : (
                          <Trash2 size={14} />
                        )}
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {formTarget && (
        <FundEntryForm
          target={formTarget === "new" ? null : formTarget}
          events={events}
          onClose={() => setFormTarget(null)}
          onSaved={() => {
            setFormTarget(null);
            load();
          }}
        />
      )}
    </div>
  );
}

function FundEntryForm({
  target,
  events,
  onClose,
  onSaved,
}: {
  target: FundEntryDetail | null;
  events: CommunityEventDetail[];
  onClose: () => void;
  onSaved: () => void;
}) {
  const { authFetch } = useAuth();
  const [name, setName] = useState(target?.name ?? "");
  const [amount, setAmount] = useState(target ? String(target.amount) : "");
  const [entryDate, setEntryDate] = useState(target?.entryDate ?? "");
  const [mode, setMode] = useState<FundMode>(target?.mode ?? "CASH");
  const [entryType, setEntryType] = useState<FundEntryType>(target?.entryType ?? "CONTRIBUTION");
  const [note, setNote] = useState(target?.note ?? "");
  const [relatedEventId, setRelatedEventId] = useState(
    target?.relatedEventId ? String(target.relatedEventId) : ""
  );
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    const amountNum = Number(amount);
    if (!name.trim() || !entryDate || !amountNum || amountNum <= 0) {
      setError("नाम, राशि और तारीख़ आवश्यक हैं");
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      const input: FundEntryWriteInput = {
        name: name.trim(),
        amount: amountNum,
        entryDate,
        mode,
        note: note.trim() || null,
        entryType,
        relatedEventId: relatedEventId ? Number(relatedEventId) : null,
      };
      const res = await authFetch(
        target ? `/api/fund/entries/${target.id}` : "/api/fund/entries",
        {
          method: target ? "PUT" : "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(input),
        }
      );
      if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body.message ?? `सहेजा नहीं जा सका (${res.status})`);
      }
      onSaved();
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <FormSheet
      title={target ? "प्रविष्टि संपादित करें" : "नई प्रविष्टि"}
      onClose={onClose}
      onSubmit={handleSubmit}
      submitting={submitting}
      submitLabel="सहेजें"
      error={error}
    >
      <div className="flex gap-2">
        <button
          type="button"
          onClick={() => setEntryType("CONTRIBUTION")}
          className={`flex-1 rounded-lg border py-1.5 text-[12px] font-medium transition ${
            entryType === "CONTRIBUTION"
              ? "border-[#27500A]/40 bg-[#EAF3DE] text-[#27500A]"
              : "border-ink/20 bg-white text-ink-soft"
          }`}
        >
          चंदा
        </button>
        <button
          type="button"
          onClick={() => setEntryType("EXPENSE")}
          className={`flex-1 rounded-lg border py-1.5 text-[12px] font-medium transition ${
            entryType === "EXPENSE"
              ? "border-sindoor/40 bg-[#FDE8E8] text-[#7A2020]"
              : "border-ink/20 bg-white text-ink-soft"
          }`}
        >
          खर्च
        </button>
      </div>

      <Field label="नाम">
        <input
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="जैसे: राजेश सिंह"
          className={fieldInputClass}
        />
      </Field>

      <div className="flex gap-2">
        <div className="flex-1">
          <Field label="राशि (₹)">
            <input
              type="number"
              min="1"
              step="0.01"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              className={fieldInputClass}
            />
          </Field>
        </div>
        <div className="flex-1">
          <Field label="तारीख़">
            <input
              type="date"
              value={entryDate}
              onChange={(e) => setEntryDate(e.target.value)}
              className={fieldInputClass}
            />
          </Field>
        </div>
      </div>

      <div className="flex gap-2">
        <div className="flex-1">
          <Field label="माध्यम">
            <select
              value={mode}
              onChange={(e) => setMode(e.target.value as FundMode)}
              className={fieldInputClass}
            >
              {MODE_OPTIONS.map((o) => (
                <option key={o.value} value={o.value}>
                  {o.label}
                </option>
              ))}
            </select>
          </Field>
        </div>
        <div className="flex-1">
          <Field label="कार्यक्रम (वैकल्पिक)">
            <select
              value={relatedEventId}
              onChange={(e) => setRelatedEventId(e.target.value)}
              className={fieldInputClass}
            >
              <option value="">कोई नहीं</option>
              {events.map((ev) => (
                <option key={ev.id} value={ev.id}>
                  {ev.title}
                </option>
              ))}
            </select>
          </Field>
        </div>
      </div>

      <Field label="टिप्पणी (वैकल्पिक)">
        <textarea
          value={note}
          onChange={(e) => setNote(e.target.value)}
          rows={2}
          className={`resize-none ${fieldInputClass}`}
        />
      </Field>
    </FormSheet>
  );
}
