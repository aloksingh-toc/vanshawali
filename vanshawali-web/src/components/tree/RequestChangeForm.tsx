import { useState } from "react";
import { X, Send, Loader2, CheckCircle2 } from "lucide-react";
import { submitChangeRequest, type RequestType } from "../../lib/api";
import type { SelectedPerson } from "./FamilyTree";

interface Props {
  person: SelectedPerson | null;
  onClose: () => void;
}

const TYPE_OPTIONS: { value: RequestType; label: string }[] = [
  { value: "RENAME", label: "नाम सुधार" },
  { value: "ADD_CHILD", label: "संतान जोड़ें" },
  { value: "MARK_ISSUELESS", label: "निःसंतान चिह्नित करें" },
  { value: "CONFIRM_NAME", label: "नाम की पुष्टि करें" },
  { value: "ADD_NOTE", label: "टिप्पणी जोड़ें" },
  { value: "DELETE", label: "हटाने का अनुरोध" },
  { value: "OTHER", label: "अन्य" },
];

const NAME_FIELD_TYPES: RequestType[] = ["RENAME", "ADD_CHILD"];
const NOTE_REQUIRED_TYPES: RequestType[] = ["ADD_NOTE", "OTHER"];

export default function RequestChangeForm({ person, onClose }: Props) {
  const open = !!person;
  const personId = person?.node.id ?? null;

  const [requestType, setRequestType] = useState<RequestType>("RENAME");
  const [name, setName] = useState("");
  const [note, setNote] = useState("");
  const [requesterName, setRequesterName] = useState("");
  const [requesterContact, setRequesterContact] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [done, setDone] = useState(false);

  function reset() {
    setRequestType("RENAME");
    setName("");
    setNote("");
    setRequesterName("");
    setRequesterContact("");
    setError(null);
    setDone(false);
  }

  function handleClose() {
    reset();
    onClose();
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!personId) return;
    setError(null);

    if (NAME_FIELD_TYPES.includes(requestType) && !name.trim()) {
      setError("कृपया नाम दर्ज करें");
      return;
    }
    if (NOTE_REQUIRED_TYPES.includes(requestType) && !note.trim()) {
      setError("कृपया टिप्पणी दर्ज करें");
      return;
    }
    if (!requesterName.trim()) {
      setError("कृपया अपना नाम दर्ज करें");
      return;
    }

    const proposedData: Record<string, unknown> = {};
    if (NAME_FIELD_TYPES.includes(requestType)) proposedData.name = name.trim();
    if (note.trim()) proposedData.note = note.trim();

    setSubmitting(true);
    try {
      await submitChangeRequest({
        targetPersonId: personId,
        requestType,
        proposedData,
        requesterName: requesterName.trim(),
        requesterContact: requesterContact.trim() || null,
      });
      setDone(true);
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <>
      {open && (
        <div className="absolute inset-0 z-10 bg-ink/10" onClick={handleClose} aria-hidden="true" />
      )}
      <div
        className={`absolute inset-x-0 bottom-0 z-20 max-h-[85%] overflow-y-auto rounded-t-2xl border-t-[3px] border-indigo bg-box px-4 pb-5 pt-3 shadow-panel transition-transform duration-300 ${
          open ? "translate-y-0" : "translate-y-full"
        }`}
        role="dialog"
        aria-modal="true"
      >
        <div className="mx-auto mb-2 h-1 w-10 rounded-full bg-ink/15" />
        <div className="mb-3 flex items-center justify-between">
          <h3 className="font-display text-lg font-semibold text-indigo">
            {person?.node.n ?? "—"} के लिए बदलाव अनुरोध
          </h3>
          <button
            onClick={handleClose}
            aria-label="बंद करें"
            className="rounded-full p-1.5 text-ink-soft transition active:bg-ink/10"
          >
            <X size={18} />
          </button>
        </div>

        {done ? (
          <div className="flex flex-col items-center gap-2 py-6 text-center">
            <CheckCircle2 size={32} className="text-indigo" />
            <p className="text-[13px] text-ink">
              आपका निवेदन भेज दिया गया है। Admin इसकी समीक्षा करेंगे।
            </p>
            <button
              onClick={handleClose}
              className="mt-2 rounded-lg bg-indigo px-4 py-2 text-[12px] font-medium text-cream transition active:scale-95"
            >
              ठीक है
            </button>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-3">
            <Field label="अनुरोध का प्रकार">
              <select
                value={requestType}
                onChange={(e) => setRequestType(e.target.value as RequestType)}
                className="w-full rounded-lg border border-ink/20 bg-white px-3 py-2 text-[13px] text-ink outline-none focus:border-indigo"
              >
                {TYPE_OPTIONS.map((opt) => (
                  <option key={opt.value} value={opt.value}>
                    {opt.label}
                  </option>
                ))}
              </select>
            </Field>

            {NAME_FIELD_TYPES.includes(requestType) && (
              <Field label={requestType === "ADD_CHILD" ? "संतान का नाम" : "नया नाम"}>
                <input
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  className="w-full rounded-lg border border-ink/20 bg-white px-3 py-2 text-[13px] text-ink outline-none focus:border-indigo"
                />
              </Field>
            )}

            <Field label={NOTE_REQUIRED_TYPES.includes(requestType) ? "टिप्पणी" : "टिप्पणी / कारण (वैकल्पिक)"}>
              <textarea
                value={note}
                onChange={(e) => setNote(e.target.value)}
                rows={3}
                className="w-full rounded-lg border border-ink/20 bg-white px-3 py-2 text-[13px] text-ink outline-none focus:border-indigo"
              />
            </Field>

            <Field label="आपका नाम">
              <input
                value={requesterName}
                onChange={(e) => setRequesterName(e.target.value)}
                className="w-full rounded-lg border border-ink/20 bg-white px-3 py-2 text-[13px] text-ink outline-none focus:border-indigo"
              />
            </Field>

            <Field label="संपर्क (फ़ोन/ईमेल, वैकल्पिक)">
              <input
                value={requesterContact}
                onChange={(e) => setRequesterContact(e.target.value)}
                className="w-full rounded-lg border border-ink/20 bg-white px-3 py-2 text-[13px] text-ink outline-none focus:border-indigo"
              />
            </Field>

            {error && <p className="text-[12px] text-sindoor">{error}</p>}

            <button
              type="submit"
              disabled={submitting}
              className="flex w-full items-center justify-center gap-1.5 rounded-lg bg-indigo py-2.5 text-[12px] font-medium text-cream transition active:scale-[0.98] disabled:opacity-60"
            >
              {submitting ? <Loader2 size={14} className="animate-spin" /> : <Send size={14} />}
              निवेदन भेजें
            </button>
          </form>
        )}
      </div>
    </>
  );
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <label className="block">
      <span className="mb-1 block text-[11px] text-ink-soft">{label}</span>
      {children}
    </label>
  );
}
