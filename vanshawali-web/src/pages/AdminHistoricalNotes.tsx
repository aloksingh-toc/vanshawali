import { useCallback, useEffect, useState } from "react";
import { BookOpen, Loader2, Plus, Trash2 } from "lucide-react";
import { Field, FormSheet, fieldInputClass } from "../components/FormSheet";
import { PersonAutocomplete } from "../components/PersonAutocomplete";
import { useAuth } from "../lib/auth";
import {
  type HistoricalNoteDetail,
  type HistoricalNoteWriteInput,
  type PersonSearchResult,
} from "../lib/api";

const MONTHS = [
  "जनवरी", "फ़रवरी", "मार्च", "अप्रैल", "मई", "जून",
  "जुलाई", "अगस्त", "सितम्बर", "अक्तूबर", "नवम्बर", "दिसम्बर",
];

export default function AdminHistoricalNotes() {
  const { isAdmin, authFetch } = useAuth();
  const [notes, setNotes] = useState<HistoricalNoteDetail[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [formOpen, setFormOpen] = useState(false);
  const [busyId, setBusyId] = useState<number | null>(null);

  const load = useCallback(async () => {
    setError(null);
    try {
      const res = await authFetch("/api/historical-notes");
      if (!res.ok) throw new Error(`सूची लोड नहीं हो सकी (${res.status})`);
      setNotes(await res.json());
    } catch (err) {
      setError((err as Error).message);
    }
  }, [authFetch]);

  useEffect(() => {
    if (isAdmin) load();
  }, [isAdmin, load]);

  async function handleDelete(id: number) {
    if (!window.confirm("क्या आप इस ऐतिहासिक टिप्पणी को मिटाना चाहते हैं?")) return;
    setBusyId(id);
    setError(null);
    try {
      const res = await authFetch(`/api/historical-notes/${id}`, { method: "DELETE" });
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
        <BookOpen size={17} className="text-indigo" />
        <h2 className="flex-1 font-display text-base font-semibold text-indigo">
          आज का इतिहास — प्रबंधन
        </h2>
        <button
          onClick={() => setFormOpen(true)}
          className="flex items-center gap-1.5 rounded-lg bg-indigo px-2.5 py-1.5 text-[11px] font-medium text-cream transition active:scale-95"
        >
          <Plus size={13} />
          नया
        </button>
      </div>

      <div className="flex-1 overflow-y-auto px-3 py-3">
        {error && <p className="mb-2 text-[12px] text-sindoor">{error}</p>}
        {!notes && !error && (
          <p className="py-6 text-center text-[12px] text-ink-soft">लोड हो रहा है…</p>
        )}
        {notes && notes.length === 0 && (
          <p className="py-6 text-center text-[12px] text-ink-soft">
            अभी कोई ऐतिहासिक टिप्पणी नहीं है।
          </p>
        )}

        <div className="space-y-2.5">
          {notes?.map((note) => (
            <div
              key={note.id}
              className="rounded-card border border-ink/10 bg-box/90 p-3 shadow-panel"
            >
              <div className="flex items-start justify-between gap-2">
                <div className="min-w-0 flex-1">
                  <div className="text-[11px] font-medium text-indigo">
                    {note.noteDay} {MONTHS[note.noteMonth - 1]}
                  </div>
                  <div className="text-[13px] font-medium text-ink">{note.title}</div>
                  {note.description && (
                    <p className="mt-0.5 text-[12px] text-ink-soft">{note.description}</p>
                  )}
                  {note.relatedPersonName && (
                    <p className="mt-1 text-[11px] text-ink-faint">
                      सम्बंधित: {note.relatedPersonName}
                    </p>
                  )}
                </div>
                <button
                  onClick={() => handleDelete(note.id)}
                  disabled={busyId === note.id}
                  className="shrink-0 rounded-lg border border-sindoor/40 bg-[#FDE8E8] p-1.5 text-[#7A2020] transition active:scale-95 disabled:opacity-60"
                  aria-label="मिटाएं"
                >
                  {busyId === note.id ? (
                    <Loader2 size={14} className="animate-spin" />
                  ) : (
                    <Trash2 size={14} />
                  )}
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

      {formOpen && (
        <NoteForm
          onClose={() => setFormOpen(false)}
          onSaved={() => {
            setFormOpen(false);
            load();
          }}
        />
      )}
    </div>
  );
}

function NoteForm({ onClose, onSaved }: { onClose: () => void; onSaved: () => void }) {
  const { authFetch } = useAuth();
  const [noteMonth, setNoteMonth] = useState(1);
  const [noteDay, setNoteDay] = useState(1);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [personQuery, setPersonQuery] = useState("");
  const [relatedPerson, setRelatedPerson] = useState<PersonSearchResult | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!title.trim()) {
      setError("शीर्षक आवश्यक है");
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      const input: HistoricalNoteWriteInput = {
        noteMonth,
        noteDay,
        title: title.trim(),
        description: description.trim() || null,
        relatedPersonId: relatedPerson?.id ?? null,
      };
      const res = await authFetch("/api/historical-notes", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(input),
      });
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
      title="नई टिप्पणी"
      onClose={onClose}
      onSubmit={handleSubmit}
      submitting={submitting}
      submitLabel="सहेजें"
      error={error}
    >
      <div className="flex gap-2">
        <div className="flex-1">
          <Field label="महीना">
            <select
              value={noteMonth}
              onChange={(e) => setNoteMonth(Number(e.target.value))}
              className={fieldInputClass}
            >
              {MONTHS.map((m, i) => (
                <option key={i} value={i + 1}>
                  {m}
                </option>
              ))}
            </select>
          </Field>
        </div>
        <div className="w-20">
          <Field label="तारीख़">
            <input
              type="number"
              min={1}
              max={31}
              value={noteDay}
              onChange={(e) => setNoteDay(Number(e.target.value))}
              className={fieldInputClass}
            />
          </Field>
        </div>
      </div>

      <Field label="शीर्षक">
        <input
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="जैसे: गाँव में पहला विद्यालय खुला"
          className={fieldInputClass}
        />
      </Field>

      <Field label="विवरण (वैकल्पिक)">
        <textarea
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          rows={2}
          className={`resize-none ${fieldInputClass}`}
        />
      </Field>

      <PersonAutocomplete
        label="सम्बंधित व्यक्ति (वैकल्पिक)"
        query={personQuery}
        onQueryChange={setPersonQuery}
        selected={relatedPerson}
        onSelect={setRelatedPerson}
      />
    </FormSheet>
  );
}
