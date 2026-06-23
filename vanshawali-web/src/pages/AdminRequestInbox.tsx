import { Inbox } from "lucide-react";
import { ApproveRejectButtons, StatusBadge } from "../components/ModerationUI";
import { useModerationQueue } from "../hooks/useModerationQueue";
import { useAuth } from "../lib/auth";
import type { ChangeRequestDetail } from "../lib/api";

const TYPE_LABELS: Record<string, string> = {
  RENAME: "नाम सुधार",
  ADD_CHILD: "संतान जोड़ें",
  MARK_ISSUELESS: "निःसंतान चिह्नित करें",
  CONFIRM_NAME: "नाम की पुष्टि करें",
  ADD_NOTE: "टिप्पणी जोड़ें",
  DELETE: "हटाने का अनुरोध",
  OTHER: "अन्य",
};

export default function AdminRequestInbox() {
  const { isAdmin } = useAuth();
  const { items: requests, error, busyId, decide: decideRaw } = useModerationQueue<ChangeRequestDetail>(
    "/api/requests",
    "/api/requests",
    isAdmin
  );

  function decide(id: number, action: "approve" | "reject") {
    const notes = window.prompt(
      action === "approve" ? "स्वीकृति हेतु टिप्पणी (वैकल्पिक):" : "अस्वीकृति का कारण (वैकल्पिक):",
      ""
    );
    if (notes === null) return;
    decideRaw(id, action, { notes: notes || null });
  }

  return (
    <div className="flex h-full flex-col">
      <div className="flex shrink-0 items-center gap-2 border-b border-ink/10 bg-box/85 px-4 py-3 backdrop-blur-sm">
        <Inbox size={17} className="text-indigo" />
        <h2 className="font-display text-base font-semibold text-indigo">परिवर्तन निवेदन</h2>
      </div>

      <div className="flex-1 overflow-y-auto px-3 py-3">
        {error && <p className="mb-2 text-[12px] text-sindoor">{error}</p>}

        {!requests && !error && (
          <p className="py-6 text-center text-[12px] text-ink-soft">लोड हो रहा है…</p>
        )}

        {requests && requests.length === 0 && (
          <p className="py-6 text-center text-[12px] text-ink-soft">कोई निवेदन नहीं है।</p>
        )}

        <div className="space-y-2.5">
          {requests?.map((r) => (
            <div
              key={r.id}
              className="rounded-card border border-ink/10 bg-box/90 p-3 shadow-panel"
            >
              <div className="mb-1.5 flex items-center justify-between gap-2">
                <span className="text-[13px] font-medium text-ink">
                  {TYPE_LABELS[r.requestType] ?? r.requestType}
                  {r.targetPersonName && (
                    <span className="text-ink-soft"> · {r.targetPersonName}</span>
                  )}
                </span>
                <StatusBadge status={r.status} />
              </div>

              {r.proposedData?.name != null && (
                <p className="text-[12px] text-ink-soft">नाम: {String(r.proposedData.name)}</p>
              )}
              {r.proposedData?.note != null && (
                <p className="text-[12px] text-ink-soft">टिप्पणी: {String(r.proposedData.note)}</p>
              )}
              <p className="mt-1 text-[11px] text-ink-faint">
                {r.requesterName}
                {r.requesterContact ? ` · ${r.requesterContact}` : ""}
              </p>
              {r.adminNotes && (
                <p className="mt-1 text-[11px] text-ink-faint">Admin: {r.adminNotes}</p>
              )}

              {r.status === "PENDING" && (
                <ApproveRejectButtons
                  busy={busyId === r.id}
                  onApprove={() => decide(r.id, "approve")}
                  onReject={() => decide(r.id, "reject")}
                />
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
