import { NavLink } from "react-router-dom";
import { useLang } from "../i18n";

const items = [
  { to: "/tree", key: "nav_tree" },
  { to: "/gallery", key: "nav_gallery" },
  { to: "/events", key: "nav_events" },
  { to: "/announcements", key: "nav_announce" },
  { to: "/fund", key: "nav_fund" },
  { to: "/relation", key: "nav_relation" },
] as const;

export default function Navbar() {
  const { t } = useLang();
  return (
    <nav className="border-t border-cream/10 bg-indigo-deep/60">
      <div className="no-scrollbar mx-auto flex max-w-5xl justify-center gap-1 overflow-x-auto px-3 py-1.5">
        {items.map((it) => (
          <NavLink
            key={it.to}
            to={it.to}
            className={({ isActive }) =>
              `whitespace-nowrap rounded-full px-3.5 py-1.5 text-[12.5px] font-medium transition ${
                isActive
                  ? "bg-cream text-indigo"
                  : "text-cream/75 hover:bg-white/10 hover:text-cream"
              }`
            }
          >
            {t(it.key)}
          </NavLink>
        ))}
      </div>
    </nav>
  );
}
