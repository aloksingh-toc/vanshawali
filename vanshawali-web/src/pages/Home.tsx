import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Coins,
  Image as ImageIcon,
  Upload,
  Network,
  ArrowLeftRight,
  PencilLine,
  Cake,
  Flame,
  BookOpen,
  ShieldCheck,
} from "lucide-react";
import { useLang } from "../i18n";
import { Pill } from "../components/ModerationUI";
import { useAuth } from "../lib/auth";
import { formatINR } from "../lib/format";
import { fetchOnThisDay, type OnThisDayItem } from "../lib/api";
import SectionHeading from "../components/SectionHeading";
import {
  events,
  funds,
  feed,
  gallery,
  stats,
  type UpcomingEvent,
} from "../data/mock";

const HI_DAYS = ["रविवार", "सोमवार", "मंगलवार", "बुधवार", "गुरुवार", "शुक्रवार", "शनिवार"];
const HI_MONTHS = [
  "जनवरी", "फ़रवरी", "मार्च", "अप्रैल", "मई", "जून",
  "जुलाई", "अगस्त", "सितम्बर", "अक्तूबर", "नवम्बर", "दिसम्बर",
];

function useClock() {
  const [now, setNow] = useState(new Date());
  useEffect(() => {
    const id = setInterval(() => setNow(new Date()), 1000);
    return () => clearInterval(id);
  }, []);
  return now;
}

function daysUntil(iso: string) {
  const target = new Date(iso + "T00:00:00");
  const now = new Date();
  now.setHours(0, 0, 0, 0);
  return Math.round((target.getTime() - now.getTime()) / 86_400_000);
}

const eventPill: Record<UpcomingEvent["type"], { label: string; cls: string }> = {
  WEDDING: { label: "विवाह", cls: "bg-[#FDE8E8] text-[#7A2020]" },
  FESTIVAL: { label: "त्योहार", cls: "bg-haldi-bg text-[#633806]" },
  PUJA: { label: "पूजा", cls: "bg-haldi-bg text-[#633806]" },
  REUNION: { label: "मिलन", cls: "bg-[#E6F1FB] text-[#0C447C]" },
  VILLAGE_PROJECT: { label: "परियोजना", cls: "bg-[#E6F1FB] text-[#0C447C]" },
};

const feedTone: Record<string, string> = {
  green: "bg-[#EAF3DE] text-[#27500A]",
  blue: "bg-[#E6F1FB] text-[#0C447C]",
  amber: "bg-haldi-bg text-[#633806]",
  red: "bg-[#FCEBEB] text-[#791F1F]",
};

export default function Home() {
  const { t } = useLang();
  const { isAdmin } = useAuth();
  const navigate = useNavigate();
  const now = useClock();
  const [onThisDay, setOnThisDay] = useState<OnThisDayItem[] | null>(null);

  const loadOnThisDay = useCallback(() => {
    fetchOnThisDay()
      .then(setOnThisDay)
      .catch(() => setOnThisDay([]));
  }, []);

  useEffect(() => {
    loadOnThisDay();
  }, [loadOnThisDay]);

  const clock = now.toLocaleTimeString("en-GB");
  const dateLine = `${HI_DAYS[now.getDay()]}, ${now.getDate()} ${
    HI_MONTHS[now.getMonth()]
  } ${now.getFullYear()}`;

  return (
    <div className="w-full min-w-0 space-y-4 px-3 py-4">
      {/* hero band */}
      <section className="animate-riseIn overflow-hidden rounded-card border border-ink/10 bg-indigo text-cream shadow-manuscript">
        <div className="flex flex-col items-center px-5 py-5 text-center">
          <p className="text-[10px] tracking-[0.3em] text-cream/45">
            हमारी वंश-परम्परा
          </p>
          <div className="gilt-divider my-2" />
          <div className="flex divide-x divide-cream/15">
            {[
              { n: stats.members, l: t("members") },
              { n: stats.generations, l: t("generations") },
              { n: stats.branches, l: t("branches") },
            ].map((s) => (
              <div key={s.l} className="px-5">
                <div className="text-xl font-semibold">{s.n}</div>
                <div className="text-[9px] text-cream/55">{s.l}</div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* today's dates */}
      <section className="overflow-hidden rounded-card border border-ink/10 bg-box/90 backdrop-blur-sm shadow-manuscript">
        <div className="flex items-center justify-between bg-indigo px-4 py-2">
          <div>
            <div className="text-[11px] text-cream/65">{dateLine}</div>
            <div className="text-[13px] font-semibold text-cream">
              {t("today")}
            </div>
          </div>
          <div className="flex items-center gap-2">
            <div className="font-mono text-[15px] tracking-wide text-cream/85 tabular-nums">
              {clock}
            </div>
            <button
              onClick={() => navigate(isAdmin ? "/admin/historical-notes" : "/admin/login")}
              aria-label="इतिहास प्रबंधन"
              className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full border border-cream/30 text-cream/80 transition active:scale-90"
            >
              {isAdmin ? <BookOpen size={12} /> : <ShieldCheck size={12} />}
            </button>
          </div>
        </div>
        <div>
          {onThisDay === null && (
            <p className="px-4 py-3 text-center text-[12px] text-ink-soft">लोड हो रहा है…</p>
          )}
          {onThisDay && onThisDay.length === 0 && (
            <p className="px-4 py-3 text-center text-[12px] text-ink-soft">
              आज कोई विशेष तिथि नहीं है।
            </p>
          )}
          {onThisDay?.map((item, i) => {
            const bday = item.type === "BIRTHDAY";
            const isNote = item.type === "HISTORICAL_NOTE";
            return (
              <div
                key={i}
                className="flex items-center gap-3 border-b border-ink/[0.07] px-4 py-2.5 last:border-0"
              >
                <div
                  className={`flex h-9 w-9 shrink-0 items-center justify-center rounded-full text-[13px] font-semibold ${
                    isNote
                      ? "bg-haldi-bg text-[#633806]"
                      : bday
                        ? "bg-[#FDE8E8] text-[#7A2020]"
                        : "bg-[#E8E8F5] text-[#2A2060]"
                  }`}
                >
                  {isNote ? <BookOpen size={15} /> : (item.personName ?? "?")[0]}
                </div>
                <div className="min-w-0 flex-1">
                  <div className="text-[13px] font-medium text-ink">{item.title}</div>
                  {item.description && (
                    <div className="truncate text-[11px] text-ink-soft">{item.description}</div>
                  )}
                </div>
                {!isNote && (
                  <span
                    className={`flex items-center gap-1 whitespace-nowrap rounded-full px-2.5 py-1 text-[10px] ${
                      bday ? "bg-[#FDE8E8] text-[#7A2020]" : "bg-[#E8E8F5] text-[#2A2060]"
                    }`}
                  >
                    {bday ? <Cake size={11} /> : <Flame size={11} />}
                    {bday ? t("birthday") : t("barsi")}
                  </span>
                )}
              </div>
            );
          })}
        </div>
      </section>

      {/* upcoming events */}
      <section>
        <SectionHeading
          title={t("upcoming")}
          live
          action={t("seeAll")}
          onAction={() => navigate("/events")}
        />
        <div className="space-y-1.5">
          {events.slice(0, 3).map((ev) => {
            const d = daysUntil(ev.date);
            const pill = eventPill[ev.type];
            return (
              <div
                key={ev.id}
                className="flex items-center gap-3 rounded-xl border border-ink/10 bg-box/85 px-3 py-2.5 backdrop-blur-sm transition hover:border-ink/25"
              >
                <div className="w-11 shrink-0 text-center">
                  {d <= 0 ? (
                    <div className="text-[11px] font-semibold text-sindoor">
                      {t("todayLabel")}
                    </div>
                  ) : (
                    <>
                      <div className="text-lg font-semibold leading-none text-indigo">
                        {d}
                      </div>
                      <div className="mt-0.5 text-[9px] text-ink-faint">
                        {t("daysLeft")}
                      </div>
                    </>
                  )}
                </div>
                <div className="h-9 w-px shrink-0 bg-ink/10" />
                <div className="min-w-0 flex-1">
                  <div className="truncate text-[13px] font-medium text-ink">
                    {ev.title}
                  </div>
                  <div className="mt-0.5 text-[11px] text-ink-soft">{ev.meta}</div>
                </div>
                <Pill cls={pill.cls}>{pill.label}</Pill>
              </div>
            );
          })}
        </div>
      </section>

      {/* public fund */}
      <section>
        <SectionHeading
          title={t("publicFund")}
          icon={<Coins size={15} className="text-sindoor" />}
          action={t("fullLedger")}
          onAction={() => navigate("/fund")}
        />
        <div className="space-y-1.5">
          {funds.map((f) => {
            const pct = Math.round((f.raised / f.goal) * 100);
            return (
              <div
                key={f.id}
                className="rounded-card border border-ink/10 bg-box/85 px-4 py-3 backdrop-blur-sm"
              >
                <div className="text-[13px] font-semibold text-ink">{f.title}</div>
                <div className="mb-2.5 text-[11px] text-ink-soft">{f.sub}</div>
                <div className="h-2 overflow-hidden rounded-full bg-paper-deep">
                  <ProgressBar pct={pct} accent={f.accent} />
                </div>
                <div className="mt-1.5 flex items-center justify-between text-[11px]">
                  <span className="font-semibold text-indigo">{formatINR(f.raised)}</span>
                  <span className="font-semibold text-sindoor">{pct}%</span>
                  <span className="text-ink-faint">
                    {t("goal")} {formatINR(f.goal)}
                  </span>
                </div>
                {f.accent === "indigo" && (
                  <button
                    onClick={() => navigate("/fund")}
                    className="mt-2.5 w-full rounded-lg bg-indigo py-2 text-[12px] font-semibold text-cream transition hover:bg-indigo-deep"
                  >
                    {t("contribute")}
                  </button>
                )}
              </div>
            );
          })}
        </div>
      </section>

      {/* recent photos */}
      <section>
        <SectionHeading
          title={t("recentPhotos")}
          icon={<ImageIcon size={15} className="text-sindoor" />}
          action={t("gallery")}
          onAction={() => navigate("/gallery")}
        />
        <div className="no-scrollbar flex gap-1.5 overflow-x-auto pb-1">
          {gallery.map((g) => (
            <button
              key={g.id}
              onClick={() => navigate("/gallery")}
              className="relative flex h-[88px] w-[88px] shrink-0 flex-col items-center justify-center gap-1 rounded-lg border border-ink/10 text-[10px] text-ink-faint transition hover:border-ink/30"
              style={{ background: g.tone }}
            >
              <ImageIcon size={22} />
              <span className="px-1 text-center">{g.title}</span>
              <span className="absolute bottom-1 right-1.5 text-[9px] text-ink-soft">
                ♥ {g.likes}
              </span>
            </button>
          ))}
          <button
            onClick={() => navigate("/gallery")}
            className="flex h-[88px] w-[88px] shrink-0 flex-col items-center justify-center gap-1 rounded-lg border-[1.5px] border-dashed border-ink/25 text-[10px] text-ink-faint transition hover:border-ink/40"
          >
            <Upload size={22} />
            <span>{t("upload")}</span>
          </button>
        </div>
      </section>

      {/* activity feed */}
      <section>
        <SectionHeading title={t("recentActivity")} live action={t("all")} />
        <div className="overflow-hidden rounded-card border border-ink/10 bg-box/85 backdrop-blur-sm">
          {feed.map((item) => (
            <div
              key={item.id}
              className="flex cursor-pointer items-start gap-3 border-b border-ink/[0.07] px-3 py-2.5 transition last:border-0 hover:bg-ink/[0.03]"
            >
              <div
                className={`mt-0.5 flex h-[34px] w-[34px] shrink-0 items-center justify-center rounded-full text-[12px] font-semibold ${
                  feedTone[item.tone]
                }`}
              >
                {item.initial}
              </div>
              <div className="min-w-0 flex-1">
                <div className="text-[12.5px] leading-snug text-ink">
                  {item.text}
                </div>
                <div className="mt-0.5 text-[10px] text-ink-faint">{item.meta}</div>
              </div>
              {item.fresh && (
                <span className="mt-0.5 shrink-0 rounded-full bg-[#FDE8E8] px-1.5 py-0.5 text-[9px] text-[#7A2020]">
                  {t("new")}
                </span>
              )}
            </div>
          ))}
        </div>
      </section>

      {/* quick actions */}
      <section className="grid grid-cols-3 gap-1.5 pb-6">
        <QuickAction
          icon={<Network size={20} />}
          label={t("q_tree")}
          sub={t("q_tree_sub")}
          onClick={() => navigate("/tree")}
        />
        <QuickAction
          icon={<ArrowLeftRight size={20} />}
          label={t("q_relation")}
          sub={t("q_relation_sub")}
          onClick={() => navigate("/relation")}
        />
        <QuickAction
          icon={<PencilLine size={20} />}
          label={t("q_change")}
          sub={t("q_change_sub")}
          onClick={() => navigate("/tree")}
        />
      </section>
    </div>
  );
}

function ProgressBar({ pct, accent }: { pct: number; accent: "indigo" | "sindoor" }) {
  const [w, setW] = useState(0);
  useEffect(() => {
    const id = setTimeout(() => setW(pct), 250);
    return () => clearTimeout(id);
  }, [pct]);
  return (
    <div
      className="h-full rounded-full transition-[width] duration-[1500ms] ease-out"
      style={{
        width: `${w}%`,
        background: accent === "sindoor" ? "#B3402A" : "#232E52",
      }}
    />
  );
}

function QuickAction({
  icon,
  label,
  sub,
  onClick,
}: {
  icon: React.ReactNode;
  label: string;
  sub: string;
  onClick: () => void;
}) {
  return (
    <button
      onClick={onClick}
      className="rounded-xl border border-ink/10 bg-box/85 px-2 py-3 text-center backdrop-blur-sm transition hover:bg-box"
    >
      <div className="mb-1 flex justify-center text-indigo">{icon}</div>
      <div className="text-[11px] font-medium leading-tight text-ink">{label}</div>
      <div className="mt-0.5 text-[10px] text-ink-faint">{sub}</div>
    </button>
  );
}
