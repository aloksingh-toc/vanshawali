import { NavLink } from "react-router-dom";
import { Home, Network, Image as ImageIcon, Coins, Menu } from "lucide-react";
import { useLang } from "../i18n";

const items = [
  { to: "/", key: "nav_home", Icon: Home, end: true },
  { to: "/tree", key: "nav_tree", Icon: Network, end: false },
  { to: "/gallery", key: "nav_gallery", Icon: ImageIcon, end: false },
  { to: "/fund", key: "nav_fund", Icon: Coins, end: false },
  { to: "/more", key: "nav_more", Icon: Menu, end: false },
] as const;

export default function BottomNav() {
  const { t } = useLang();
  return (
    <nav
      className="z-30 shrink-0 border-t-[3px] border-sindoor bg-indigo text-cream"
      style={{ paddingBottom: "env(safe-area-inset-bottom)" }}
    >
      <div className="flex items-stretch justify-around">
        {items.map(({ to, key, Icon, end }) => (
          <NavLink
            key={to}
            to={to}
            end={end}
            className={({ isActive }) =>
              `flex min-h-[56px] flex-1 flex-col items-center justify-center gap-1 py-1.5 text-[10px] font-medium transition ${
                isActive ? "text-cream" : "text-cream/55"
              }`
            }
          >
            {({ isActive }) => (
              <>
                <span
                  className={`flex h-7 w-12 items-center justify-center rounded-full transition ${
                    isActive ? "bg-sindoor text-white" : "bg-transparent"
                  }`}
                >
                  <Icon size={19} strokeWidth={isActive ? 2.4 : 2} />
                </span>
                <span>{t(key as Parameters<typeof t>[0])}</span>
              </>
            )}
          </NavLink>
        ))}
      </div>
    </nav>
  );
}
