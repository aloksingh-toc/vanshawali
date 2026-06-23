import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Trophy, Plus, Inbox, ShieldCheck } from "lucide-react";
import { Pill } from "../components/ModerationUI";
import { ToolButton } from "../components/ToolButton";
import { useAuth } from "../lib/auth";
import {
  fetchApprovedAnnouncements,
  type AnnouncementDetail,
  type AnnouncementType,
} from "../lib/api";
import AnnouncementSubmitForm from "../components/announcements/AnnouncementSubmitForm";

const TYPE_PILL: Record<AnnouncementType, { label: string; cls: string }> = {
  JOB: { label: "नौकरी", cls: "bg-[#E6F1FB] text-[#0C447C]" },
  DEGREE: { label: "शिक्षा", cls: "bg-haldi-bg text-[#633806]" },
  MARRIAGE: { label: "विवाह", cls: "bg-[#FDE8E8] text-[#7A2020]" },
  BIRTH: { label: "जन्म", cls: "bg-[#EAF3DE] text-[#27500A]" },
  OTHER: { label: "अन्य", cls: "bg-ink/[0.06] text-ink-soft" },
};

export default function AnnouncementsBoard() {
  const { isAdmin } = useAuth();
  const navigate = useNavigate();
  const [items, setItems] = useState<AnnouncementDetail[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [formOpen, setFormOpen] = useState(false);

  const load = useCallback(async () => {
    setError(null);
    try {
      setItems(await fetchApprovedAnnouncements());
    } catch (err) {
      setError((err as Error).message);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  return (
    <div className="flex h-full flex-col">
      <div className="flex shrink-0 items-center gap-2 border-b border-ink/10 bg-box/85 px-4 py-3 backdrop-blur-sm">
        <Trophy size={17} className="text-indigo" />
        <h2 className="flex-1 font-display text-base font-semibold text-indigo">उपलब्धियाँ</h2>
        {isAdmin ? (
          <ToolButton
            icon={<Inbox size={13} />}
            label="निवेदन"
            onClick={() => navigate("/admin/announcements")}
          />
        ) : (
          <ToolButton
            icon={<ShieldCheck size={13} />}
            label="Admin"
            onClick={() => navigate("/admin/login")}
          />
        )}
      </div>

      <div className="relative min-h-0 flex-1 overflow-hidden">
        <div className="h-full overflow-y-auto px-3 py-3">
          {error && <p className="mb-2 text-[12px] text-sindoor">{error}</p>}
          {!items && !error && (
            <p className="py-6 text-center text-[12px] text-ink-soft">लोड हो रहा है…</p>
          )}
          {items && items.length === 0 && (
            <p className="py-6 text-center text-[12px] text-ink-soft">
              अभी कोई उपलब्धि साझा नहीं हुई है। सबसे पहले आप साझा करें!
            </p>
          )}

          <div className="space-y-2.5 pb-14">
            {items?.map((item) => {
              const pill = TYPE_PILL[item.announcementType];
              return (
                <div
                  key={item.id}
                  className="rounded-card border border-ink/10 bg-box/90 p-3 shadow-panel"
                >
                  <div className="flex items-start justify-between gap-2">
                    <div className="min-w-0 flex-1">
                      <div className="text-[13px] font-medium text-ink">{item.title}</div>
                      {item.description && (
                        <p className="mt-0.5 text-[12px] text-ink-soft">{item.description}</p>
                      )}
                      <p className="mt-1.5 text-[11px] text-ink-faint">
                        {item.personName ?? item.submitterName}
                      </p>
                    </div>
                    <Pill cls={pill.cls}>{pill.label}</Pill>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        <button
          onClick={() => setFormOpen(true)}
          className="absolute bottom-4 right-4 flex h-12 w-12 items-center justify-center rounded-full bg-indigo text-cream shadow-panel transition active:scale-95"
          aria-label="उपलब्धि साझा करें"
        >
          <Plus size={20} />
        </button>

        <AnnouncementSubmitForm
          open={formOpen}
          onClose={() => setFormOpen(false)}
          onSubmitted={load}
        />
      </div>
    </div>
  );
}
