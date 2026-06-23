import { Inbox } from "lucide-react";
import { ApproveRejectButtons, StatusBadge } from "../components/ModerationUI";
import { useAuth } from "../lib/auth";
import { useModerationQueue } from "../hooks/useModerationQueue";
import type { AnnouncementDetail, AnnouncementType } from "../lib/api";

const TYPE_LABEL: Record<AnnouncementType, string> = {
  JOB: "नौकरी",
  DEGREE: "शिक्षा",
  MARRIAGE: "विवाह",
  BIRTH: "जन्म",
  OTHER: "अन्य",
};

export default function AdminAnnouncements() {
  const { isAdmin } = useAuth();
  const { items, error, busyId, decide } = useModerationQueue<AnnouncementDetail>(
    "/api/announcements/pending",
    "/api/announcements",
    isAdmin
  );

  return (
    <div className="flex h-full flex-col">
      <div className="flex shrink-0 items-center gap-2 border-b border-ink/10 bg-box/85 px-4 py-3 backdrop-blur-sm">
        <Inbox size={17} className="text-indigo" />
        <h2 className="font-display text-base font-semibold text-indigo">उपलब्धि निवेदन</h2>
      </div>

      <div className="flex-1 overflow-y-auto px-3 py-3">
        {error && <p className="mb-2 text-[12px] text-sindoor">{error}</p>}
        {!items && !error && (
          <p className="py-6 text-center text-[12px] text-ink-soft">लोड हो रहा है…</p>
        )}
        {items && items.length === 0 && (
          <p className="py-6 text-center text-[12px] text-ink-soft">कोई बाकी निवेदन नहीं है।</p>
        )}

        <div className="space-y-2.5">
          {items?.map((item) => (
            <div
              key={item.id}
              className="rounded-card border border-ink/10 bg-box/90 p-3 shadow-panel"
            >
              <div className="mb-1.5 flex items-center justify-between gap-2">
                <span className="text-[13px] font-medium text-ink">{item.title}</span>
                <StatusBadge status={item.status} />
              </div>
              <p className="text-[11px] text-ink-faint">{TYPE_LABEL[item.announcementType]}</p>
              {item.description && (
                <p className="mt-1 text-[12px] text-ink-soft">{item.description}</p>
              )}
              {item.personName && (
                <p className="mt-1 text-[11px] text-ink-faint">सम्बंधित: {item.personName}</p>
              )}
              <p className="mt-1 text-[11px] text-ink-faint">
                {item.submitterName}
                {item.submitterContact ? ` · ${item.submitterContact}` : ""}
              </p>

              <ApproveRejectButtons
                busy={busyId === item.id}
                onApprove={() => decide(item.id, "approve")}
                onReject={() => decide(item.id, "reject")}
              />
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
