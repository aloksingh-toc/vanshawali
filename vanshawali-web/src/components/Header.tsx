import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { Bell, Search } from "lucide-react";
import { useLang } from "../i18n";
import Ticker from "./Ticker";
import Emblem from "./Emblem";

export default function Header() {
  const { t, lang, toggle } = useLang();
  const [query, setQuery] = useState("");
  const navigate = useNavigate();

  const submit = (e: React.FormEvent) => {
    e.preventDefault();
    const q = query.trim();
    if (q) navigate(`/tree?q=${encodeURIComponent(q)}`);
  };

  return (
    <header
      className="z-30 shrink-0 border-b-[3px] border-sindoor bg-indigo text-cream"
      style={{ paddingTop: "env(safe-area-inset-top)" }}
    >
      {/* thin gilt hairline at the very top */}
      <div className="h-[2px] w-full bg-gradient-to-r from-transparent via-[#C99A3A] to-transparent opacity-70" />

      <div className="flex items-center gap-2.5 px-3 py-2">
        {/* brand cluster — left */}
        <Emblem size={40} />
        <div className="mr-auto min-w-0">
          <h1 className="truncate font-display text-[21px] font-semibold leading-none tracking-wide">
            वंशावली
          </h1>
          <p className="mt-0.5 truncate text-[10px] text-[#E5C77E]/80">
            ग्राम इब्राहिमाबाद · बलिया
          </p>
        </div>

        {/* search — right */}
        <form onSubmit={submit} className="shrink-0">
          <div className="flex w-[136px] items-center gap-1.5 rounded-full border border-[#C99A3A]/40 bg-white/[0.07] px-3 py-2 shadow-inner transition-all duration-200 focus-within:w-[176px] focus-within:border-[#C99A3A]/80 focus-within:bg-white/[0.12]">
            <Search size={14} className="shrink-0 text-[#E5C77E]" />
            <input
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder={t("searchShort")}
              aria-label={t("search")}
              className="w-full bg-transparent text-[12.5px] text-cream placeholder:text-cream/45 focus:outline-none"
            />
          </div>
        </form>

        {/* language pill */}
        <button
          onClick={toggle}
          aria-label="Language"
          className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full border text-[11px] font-semibold transition active:scale-95"
          style={{
            borderColor: lang === "en" ? "#B3402A" : "rgba(201,154,58,0.45)",
            background: lang === "en" ? "#B3402A" : "rgba(255,255,255,0.05)",
          }}
        >
          {lang === "hi" ? "EN" : "हि"}
        </button>

        {/* notification bell */}
        <button
          aria-label="सूचनाएँ"
          className="relative flex h-9 w-9 shrink-0 items-center justify-center rounded-full border border-[#C99A3A]/45 bg-white/[0.05] text-cream/85 transition active:scale-95"
        >
          <Bell size={17} />
          <span className="absolute right-1.5 top-1.5 h-[7px] w-[7px] rounded-full border-2 border-indigo bg-sindoor" />
        </button>
      </div>

      <Ticker />
    </header>
  );
}
