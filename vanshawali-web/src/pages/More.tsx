import { useNavigate } from "react-router-dom";
import {
  CalendarHeart,
  Trophy,
  ArrowLeftRight,
  ChevronRight,
  Users,
} from "lucide-react";
import { useLang } from "../i18n";
import { useAuth } from "../lib/auth";

export default function More() {
  const { t } = useLang();
  const navigate = useNavigate();
  const { session } = useAuth();

  const items = [
    { to: "/events", key: "nav_events", Icon: CalendarHeart },
    { to: "/announcements", key: "nav_announce", Icon: Trophy },
    { to: "/relation", key: "nav_relation", Icon: ArrowLeftRight },
  ] as const;

  const adminItems =
    session?.role === "ADMIN"
      ? ([{ to: "/admin/users", label: "उपयोगकर्ता प्रबंधन", Icon: Users }] as const)
      : [];

  return (
    <div className="space-y-4 px-3 py-4">
      <h2 className="px-1 font-display text-xl text-ink">{t("more_title")}</h2>
      <div className="overflow-hidden rounded-card border border-ink/10 bg-box/85 backdrop-blur-sm">
        {items.map(({ to, key, Icon }) => (
          <button
            key={to}
            onClick={() => navigate(to)}
            className="flex w-full items-center gap-3 border-b border-ink/[0.07] px-4 py-3.5 text-left transition last:border-0 hover:bg-ink/[0.03]"
          >
            <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-indigo/10 text-indigo">
              <Icon size={19} />
            </span>
            <span className="flex-1 text-[14px] font-medium text-ink">
              {t(key as Parameters<typeof t>[0])}
            </span>
            <ChevronRight size={18} className="text-ink-faint" />
          </button>
        ))}
      </div>

      {adminItems.length > 0 && (
        <div className="overflow-hidden rounded-card border border-ink/10 bg-box/85 backdrop-blur-sm">
          {adminItems.map(({ to, label, Icon }) => (
            <button
              key={to}
              onClick={() => navigate(to)}
              className="flex w-full items-center gap-3 border-b border-ink/[0.07] px-4 py-3.5 text-left transition last:border-0 hover:bg-ink/[0.03]"
            >
              <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-indigo/10 text-indigo">
                <Icon size={19} />
              </span>
              <span className="flex-1 text-[14px] font-medium text-ink">{label}</span>
              <ChevronRight size={18} className="text-ink-faint" />
            </button>
          ))}
        </div>
      )}
    </div>
  );
}
