import { useEffect, useState } from "react";
import { X, Save, Trash2, UserPlus, Loader2 } from "lucide-react";
import { useAuth } from "../../lib/auth";
import { fetchPerson, type PersonDetail, type PersonWriteInput } from "../../lib/api";
import FileUploadField from "../FileUploadField";
import type { SelectedPerson } from "./FamilyTree";

interface Props {
  person: SelectedPerson | null;
  onClose: () => void;
  onChanged: () => void;
}

type FormState = PersonWriteInput;

function detailToForm(d: PersonDetail): FormState {
  return {
    parentId: d.parentId ?? 0,
    name: d.name,
    aliasNote: d.aliasNote ?? "",
    directLine: d.directLine,
    issueless: d.issueless,
    unconfirmed: d.unconfirmed,
    pending: d.pending,
    birthDate: d.birthDate ?? "",
    deathDate: d.deathDate ?? "",
    dateIsApproximate: d.dateIsApproximate,
    photoUrl: d.photoUrl ?? "",
  };
}

const BLANK_CHILD: FormState = {
  parentId: 0,
  name: "",
  aliasNote: "",
  directLine: false,
  issueless: false,
  unconfirmed: true,
  pending: true,
  birthDate: "",
  deathDate: "",
  dateIsApproximate: false,
  photoUrl: "",
};

export default function AdminEditPanel({ person, onClose, onChanged }: Props) {
  const { authFetch } = useAuth();
  const open = !!person;
  const personId = person?.node.id ?? null;

  const [mode, setMode] = useState<"edit" | "create">("edit");
  const [form, setForm] = useState<FormState | null>(null);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!personId) {
      setForm(null);
      return;
    }
    setMode("edit");
    setError(null);
    setLoading(true);
    fetchPerson(personId)
      .then((d) => setForm(detailToForm(d)))
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [personId]);

  function startAddChild() {
    if (!personId) return;
    setMode("create");
    setError(null);
    setForm({ ...BLANK_CHILD, parentId: personId });
  }

  function update<K extends keyof FormState>(key: K, value: FormState[K]) {
    setForm((f) => (f ? { ...f, [key]: value } : f));
  }

  async function handleSave() {
    if (!form) return;
    setSaving(true);
    setError(null);
    try {
      const payload = {
        ...form,
        aliasNote: form.aliasNote || null,
        birthDate: form.birthDate || null,
        deathDate: form.deathDate || null,
        photoUrl: form.photoUrl || null,
      };
      const url = mode === "create" ? "/api/persons" : `/api/persons/${personId}`;
      const method = mode === "create" ? "POST" : "PUT";
      const res = await authFetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });
      if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body.message ?? `सहेजा नहीं जा सका (${res.status})`);
      }
      onChanged();
      if (mode === "create") {
        setMode("edit");
        if (personId) {
          const d = await fetchPerson(personId);
          setForm(detailToForm(d));
        }
      }
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete() {
    if (!personId) return;
    if (!window.confirm(`क्या आप वाकई "${form?.name}" को हटाना चाहते हैं?`)) return;
    setSaving(true);
    setError(null);
    try {
      const res = await authFetch(`/api/persons/${personId}`, { method: "DELETE" });
      if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body.message ?? `हटाया नहीं जा सका (${res.status})`);
      }
      onChanged();
      onClose();
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setSaving(false);
    }
  }

  return (
    <>
      {open && (
        <div className="absolute inset-0 z-10 bg-ink/10" onClick={onClose} aria-hidden="true" />
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
            {mode === "create" ? "नया परिजन जोड़ें" : person?.node.n ?? "—"}
          </h3>
          <button
            onClick={onClose}
            aria-label="बंद करें"
            className="rounded-full p-1.5 text-ink-soft transition active:bg-ink/10"
          >
            <X size={18} />
          </button>
        </div>

        {loading && (
          <p className="py-6 text-center text-[12px] text-ink-soft">लोड हो रहा है…</p>
        )}

        {!loading && form && (
          <div className="space-y-3">
            <Field label="नाम">
              <input
                value={form.name}
                onChange={(e) => update("name", e.target.value)}
                className="w-full rounded-lg border border-ink/20 bg-white px-3 py-2 text-[13px] text-ink outline-none focus:border-indigo"
              />
            </Field>

            <Field label="उपनाम / नोट">
              <input
                value={form.aliasNote ?? ""}
                onChange={(e) => update("aliasNote", e.target.value)}
                className="w-full rounded-lg border border-ink/20 bg-white px-3 py-2 text-[13px] text-ink outline-none focus:border-indigo"
              />
            </Field>

            <Field label="पिता की ID (parentId)">
              <input
                type="number"
                value={form.parentId}
                onChange={(e) => update("parentId", Number(e.target.value))}
                className="w-full rounded-lg border border-ink/20 bg-white px-3 py-2 text-[13px] text-ink outline-none focus:border-indigo"
              />
            </Field>

            <div className="grid grid-cols-2 gap-3">
              <Field label="जन्म तिथि">
                <input
                  type="date"
                  value={form.birthDate ?? ""}
                  onChange={(e) => update("birthDate", e.target.value)}
                  className="w-full rounded-lg border border-ink/20 bg-white px-3 py-2 text-[13px] text-ink outline-none focus:border-indigo"
                />
              </Field>
              <Field label="मृत्यु तिथि">
                <input
                  type="date"
                  value={form.deathDate ?? ""}
                  onChange={(e) => update("deathDate", e.target.value)}
                  className="w-full rounded-lg border border-ink/20 bg-white px-3 py-2 text-[13px] text-ink outline-none focus:border-indigo"
                />
              </Field>
            </div>

            <Field label="फ़ोटो">
              {form.photoUrl ? (
                <div className="flex items-center gap-2">
                  <img src={form.photoUrl} alt="" className="h-10 w-10 rounded-lg object-cover" />
                  <button
                    type="button"
                    onClick={() => update("photoUrl", "")}
                    className="text-[11px] text-sindoor"
                  >
                    हटाएँ
                  </button>
                </div>
              ) : (
                <FileUploadField folder="persons" onUploaded={(url) => update("photoUrl", url)} />
              )}
            </Field>

            <div className="grid grid-cols-2 gap-2">
              <Checkbox label="वंश-रेखा" checked={form.directLine} onChange={(v) => update("directLine", v)} />
              <Checkbox label="लावल्द" checked={form.issueless} onChange={(v) => update("issueless", v)} />
              <Checkbox label="नाम अपुष्ट" checked={form.unconfirmed} onChange={(v) => update("unconfirmed", v)} />
              <Checkbox label="पुष्टि बाकी" checked={form.pending} onChange={(v) => update("pending", v)} />
              <Checkbox
                label="तिथि अनुमानित"
                checked={form.dateIsApproximate}
                onChange={(v) => update("dateIsApproximate", v)}
              />
            </div>

            {error && <p className="text-[12px] text-sindoor">{error}</p>}

            <div className="flex gap-2 pt-1">
              <button
                onClick={handleSave}
                disabled={saving}
                className="flex flex-1 items-center justify-center gap-1.5 rounded-lg bg-indigo py-2.5 text-[12px] font-medium text-cream transition active:scale-[0.98] disabled:opacity-60"
              >
                {saving ? <Loader2 size={14} className="animate-spin" /> : <Save size={14} />}
                सहेजें
              </button>
              {mode === "edit" && (
                <>
                  <button
                    onClick={startAddChild}
                    className="flex flex-1 items-center justify-center gap-1.5 rounded-lg border border-ink/20 bg-white py-2.5 text-[12px] font-medium text-ink transition active:scale-[0.98]"
                  >
                    <UserPlus size={14} />
                    संतान जोड़ें
                  </button>
                  <button
                    onClick={handleDelete}
                    disabled={saving}
                    className="flex items-center justify-center gap-1.5 rounded-lg border border-sindoor/40 bg-[#FDE8E8] px-3 py-2.5 text-[12px] font-medium text-[#7A2020] transition active:scale-[0.98] disabled:opacity-60"
                  >
                    <Trash2 size={14} />
                  </button>
                </>
              )}
            </div>
          </div>
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

function Checkbox({
  label,
  checked,
  onChange,
}: {
  label: string;
  checked: boolean;
  onChange: (v: boolean) => void;
}) {
  return (
    <label className="flex items-center gap-1.5 text-[12px] text-ink">
      <input
        type="checkbox"
        checked={checked}
        onChange={(e) => onChange(e.target.checked)}
        className="h-3.5 w-3.5 accent-indigo"
      />
      {label}
    </label>
  );
}
