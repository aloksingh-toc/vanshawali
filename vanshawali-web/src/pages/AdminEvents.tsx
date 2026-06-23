import { useCallback, useEffect, useState } from "react";
import { CalendarHeart, Loader2, Pencil, Plus, Trash2 } from "lucide-react";
import { Field, FormSheet, fieldInputClass } from "../components/FormSheet";
import { useAuth } from "../lib/auth";
import type { CommunityEventDetail, CommunityEventWriteInput, EventType } from "../lib/api";

const TYPE_OPTIONS: { value: EventType; label: string }[] = [
  { value: "WEDDING", label: "विवाह" },
  { value: "FESTIVAL", label: "त्योहार" },
  { value: "PUJA", label: "पूजा" },
  { value: "REUNION", label: "मिलन" },
  { value: "VILLAGE_PROJECT", label: "परियोजना" },
  { value: "OTHER", label: "अन्य" },
];

const TYPE_LABEL: Record<EventType, string> = Object.fromEntries(
  TYPE_OPTIONS.map((o) => [o.value, o.label])
) as Record<EventType, string>;

export default function AdminEvents() {
  const { isAdmin, authFetch } = useAuth();
  const [events, setEvents] = useState<CommunityEventDetail[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [formTarget, setFormTarget] = useState<CommunityEventDetail | "new" | null>(null);
  const [busyId, setBusyId] = useState<number | null>(null);

  const load = useCallback(async () => {
    setError(null);
    try {
      const res = await authFetch("/api/events");
      if (!res.ok) throw new Error(`सूची लोड नहीं हो सकी (${res.status})`);
      setEvents(await res.json());
    } catch (err) {
      setError((err as Error).message);
    }
  }, [authFetch]);

  useEffect(() => {
    if (isAdmin) load();
  }, [isAdmin, load]);

  async function handleDelete(id: number) {
    if (!window.confirm("क्या आप इस कार्यक्रम को मिटाना चाहते हैं?")) return;
    setBusyId(id);
    setError(null);
    try {
      const res = await authFetch(`/api/events/${id}`, { method: "DELETE" });
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
        <CalendarHeart size={17} className="text-indigo" />
        <h2 className="flex-1 font-display text-base font-semibold text-indigo">
          कार्यक्रम — प्रबंधन
        </h2>
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
        {!events && !error && (
          <p className="py-6 text-center text-[12px] text-ink-soft">लोड हो रहा है…</p>
        )}
        {events && events.length === 0 && (
          <p className="py-6 text-center text-[12px] text-ink-soft">अभी कोई कार्यक्रम नहीं है।</p>
        )}

        <div className="space-y-2.5">
          {events?.map((ev) => (
            <div
              key={ev.id}
              className="rounded-card border border-ink/10 bg-box/90 p-3 shadow-panel"
            >
              <div className="flex items-start justify-between gap-2">
                <div className="min-w-0 flex-1">
                  <div className="text-[11px] font-medium text-indigo">
                    {ev.eventDate} · {TYPE_LABEL[ev.eventType]}
                  </div>
                  <div className="text-[13px] font-medium text-ink">{ev.title}</div>
                  {ev.description && (
                    <p className="mt-0.5 text-[12px] text-ink-soft">{ev.description}</p>
                  )}
                  {ev.location && (
                    <p className="mt-1 text-[11px] text-ink-faint">{ev.location}</p>
                  )}
                </div>
                <div className="flex shrink-0 gap-1.5">
                  <button
                    onClick={() => setFormTarget(ev)}
                    className="rounded-lg border border-ink/20 bg-white p-1.5 text-ink transition active:scale-95"
                    aria-label="संपादित करें"
                  >
                    <Pencil size={14} />
                  </button>
                  <button
                    onClick={() => handleDelete(ev.id)}
                    disabled={busyId === ev.id}
                    className="rounded-lg border border-sindoor/40 bg-[#FDE8E8] p-1.5 text-[#7A2020] transition active:scale-95 disabled:opacity-60"
                    aria-label="मिटाएं"
                  >
                    {busyId === ev.id ? (
                      <Loader2 size={14} className="animate-spin" />
                    ) : (
                      <Trash2 size={14} />
                    )}
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {formTarget && (
        <EventForm
          target={formTarget === "new" ? null : formTarget}
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

function EventForm({
  target,
  onClose,
  onSaved,
}: {
  target: CommunityEventDetail | null;
  onClose: () => void;
  onSaved: () => void;
}) {
  const { authFetch } = useAuth();
  const [title, setTitle] = useState(target?.title ?? "");
  const [description, setDescription] = useState(target?.description ?? "");
  const [eventDate, setEventDate] = useState(target?.eventDate ?? "");
  const [eventType, setEventType] = useState<EventType>(target?.eventType ?? "FESTIVAL");
  const [location, setLocation] = useState(target?.location ?? "");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!title.trim() || !eventDate) {
      setError("शीर्षक और तारीख़ आवश्यक हैं");
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      const input: CommunityEventWriteInput = {
        title: title.trim(),
        description: description.trim() || null,
        eventDate,
        eventType,
        location: location.trim() || null,
      };
      const res = await authFetch(target ? `/api/events/${target.id}` : "/api/events", {
        method: target ? "PUT" : "POST",
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
      title={target ? "कार्यक्रम संपादित करें" : "नया कार्यक्रम"}
      onClose={onClose}
      onSubmit={handleSubmit}
      submitting={submitting}
      submitLabel="सहेजें"
      error={error}
    >
      <Field label="शीर्षक">
        <input
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="जैसे: होली मिलन समारोह"
          className={fieldInputClass}
        />
      </Field>

      <div className="flex gap-2">
        <div className="flex-1">
          <Field label="तारीख़">
            <input
              type="date"
              value={eventDate}
              onChange={(e) => setEventDate(e.target.value)}
              className={fieldInputClass}
            />
          </Field>
        </div>
        <div className="flex-1">
          <Field label="प्रकार">
            <select
              value={eventType}
              onChange={(e) => setEventType(e.target.value as EventType)}
              className={fieldInputClass}
            >
              {TYPE_OPTIONS.map((o) => (
                <option key={o.value} value={o.value}>
                  {o.label}
                </option>
              ))}
            </select>
          </Field>
        </div>
      </div>

      <Field label="स्थान (वैकल्पिक)">
        <input
          value={location}
          onChange={(e) => setLocation(e.target.value)}
          placeholder="जैसे: गाँव चौपाल"
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
    </FormSheet>
  );
}
