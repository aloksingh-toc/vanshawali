import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { CalendarHeart, MapPin, Settings2, ShieldCheck } from "lucide-react";
import { Pill } from "../components/ModerationUI";
import { ToolButton } from "../components/ToolButton";
import { useAuth } from "../lib/auth";
import { fetchEvents, type CommunityEventDetail, type EventType } from "../lib/api";

const MONTHS = [
  "जनवरी", "फ़रवरी", "मार्च", "अप्रैल", "मई", "जून",
  "जुलाई", "अगस्त", "सितम्बर", "अक्तूबर", "नवम्बर", "दिसम्बर",
];

const TYPE_PILL: Record<EventType, { label: string; cls: string }> = {
  WEDDING: { label: "विवाह", cls: "bg-[#FDE8E8] text-[#7A2020]" },
  FESTIVAL: { label: "त्योहार", cls: "bg-haldi-bg text-[#633806]" },
  PUJA: { label: "पूजा", cls: "bg-haldi-bg text-[#633806]" },
  REUNION: { label: "मिलन", cls: "bg-[#E6F1FB] text-[#0C447C]" },
  VILLAGE_PROJECT: { label: "परियोजना", cls: "bg-[#E6F1FB] text-[#0C447C]" },
  OTHER: { label: "अन्य", cls: "bg-ink/[0.06] text-ink-soft" },
};

function groupByMonth(events: CommunityEventDetail[]) {
  const groups: { key: string; label: string; items: CommunityEventDetail[] }[] = [];
  for (const ev of events) {
    const d = new Date(ev.eventDate + "T00:00:00");
    const key = `${d.getFullYear()}-${d.getMonth()}`;
    const label = `${MONTHS[d.getMonth()]} ${d.getFullYear()}`;
    let group = groups.find((g) => g.key === key);
    if (!group) {
      group = { key, label, items: [] };
      groups.push(group);
    }
    group.items.push(ev);
  }
  return groups;
}

export default function EventsCalendar() {
  const { isAdmin } = useAuth();
  const navigate = useNavigate();
  const [events, setEvents] = useState<CommunityEventDetail[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    setError(null);
    try {
      setEvents(await fetchEvents());
    } catch (err) {
      setError((err as Error).message);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const sorted = events
    ? [...events].sort((a, b) => a.eventDate.localeCompare(b.eventDate))
    : null;
  const upcoming = sorted?.filter((e) => e.eventDate >= today.toISOString().slice(0, 10));
  const past = sorted?.filter((e) => e.eventDate < today.toISOString().slice(0, 10)).reverse();

  return (
    <div className="flex h-full flex-col">
      <div className="flex shrink-0 items-center gap-2 border-b border-ink/10 bg-box/85 px-4 py-3 backdrop-blur-sm">
        <CalendarHeart size={17} className="text-indigo" />
        <h2 className="flex-1 font-display text-base font-semibold text-indigo">
          गाँव के कार्यक्रम
        </h2>
        {isAdmin ? (
          <ToolButton
            icon={<Settings2 size={13} />}
            label="प्रबंधन"
            onClick={() => navigate("/admin/events")}
          />
        ) : (
          <ToolButton
            icon={<ShieldCheck size={13} />}
            label="Admin"
            onClick={() => navigate("/admin/login")}
          />
        )}
      </div>

      <div className="flex-1 overflow-y-auto px-3 py-3">
        {error && <p className="mb-2 text-[12px] text-sindoor">{error}</p>}
        {!events && !error && (
          <p className="py-6 text-center text-[12px] text-ink-soft">लोड हो रहा है…</p>
        )}
        {events && events.length === 0 && (
          <p className="py-6 text-center text-[12px] text-ink-soft">
            अभी कोई कार्यक्रम तय नहीं है।
          </p>
        )}

        {upcoming && upcoming.length > 0 && (
          <EventGroups events={upcoming} />
        )}

        {past && past.length > 0 && (
          <div className="mt-4">
            <p className="mb-2 px-1 text-[11px] font-medium uppercase tracking-wide text-ink-faint">
              बीते कार्यक्रम
            </p>
            <EventGroups events={past} muted />
          </div>
        )}
      </div>
    </div>
  );
}

function EventGroups({
  events,
  muted,
}: {
  events: CommunityEventDetail[];
  muted?: boolean;
}) {
  const groups = groupByMonth(events);
  return (
    <div className="space-y-4">
      {groups.map((g) => (
        <div key={g.key}>
          <p className="mb-1.5 px-1 text-[11px] font-medium text-indigo">{g.label}</p>
          <div className="space-y-2">
            {g.items.map((ev) => {
              const d = new Date(ev.eventDate + "T00:00:00");
              const pill = TYPE_PILL[ev.eventType];
              return (
                <div
                  key={ev.id}
                  className={`flex items-start gap-3 rounded-card border border-ink/10 bg-box/90 px-3 py-2.5 shadow-panel ${
                    muted ? "opacity-65" : ""
                  }`}
                >
                  <div className="w-10 shrink-0 text-center">
                    <div className="text-lg font-semibold leading-none text-indigo">
                      {d.getDate()}
                    </div>
                  </div>
                  <div className="h-9 w-px shrink-0 bg-ink/10" />
                  <div className="min-w-0 flex-1">
                    <div className="flex items-center justify-between gap-2">
                      <div className="truncate text-[13px] font-medium text-ink">
                        {ev.title}
                      </div>
                      <Pill cls={pill.cls}>{pill.label}</Pill>
                    </div>
                    {ev.description && (
                      <p className="mt-0.5 text-[12px] text-ink-soft">{ev.description}</p>
                    )}
                    {ev.location && (
                      <p className="mt-1 flex items-center gap-1 text-[11px] text-ink-faint">
                        <MapPin size={11} />
                        {ev.location}
                      </p>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      ))}
    </div>
  );
}
