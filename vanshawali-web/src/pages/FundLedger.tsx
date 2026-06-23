import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Coins, Settings, ShieldCheck, TrendingDown, TrendingUp, Wallet } from "lucide-react";
import { ToolButton } from "../components/ToolButton";
import { useAuth } from "../lib/auth";
import { formatINR } from "../lib/format";
import { fetchFundSummary, type FundSummary } from "../lib/api";

export default function FundLedger() {
  const { isAdmin } = useAuth();
  const navigate = useNavigate();
  const [summary, setSummary] = useState<FundSummary | null>(null);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    setError(null);
    try {
      setSummary(await fetchFundSummary());
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
        <Coins size={17} className="text-indigo" />
        <h2 className="flex-1 font-display text-base font-semibold text-indigo">चंदा सार</h2>
        {isAdmin ? (
          <ToolButton
            icon={<Settings size={13} />}
            label="प्रबंधन"
            onClick={() => navigate("/admin/fund")}
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
        {!summary && !error && (
          <p className="py-6 text-center text-[12px] text-ink-soft">लोड हो रहा है…</p>
        )}

        {summary && (
          <>
            <div className="grid grid-cols-3 gap-2">
              <div className="rounded-card border border-ink/10 bg-box/90 p-2.5 text-center shadow-panel">
                <TrendingUp size={15} className="mx-auto mb-1 text-[#27500A]" />
                <div className="text-[13px] font-semibold text-ink">
                  {formatINR(summary.totalContributions)}
                </div>
                <div className="text-[10px] text-ink-faint">कुल चंदा</div>
              </div>
              <div className="rounded-card border border-ink/10 bg-box/90 p-2.5 text-center shadow-panel">
                <TrendingDown size={15} className="mx-auto mb-1 text-[#7A2020]" />
                <div className="text-[13px] font-semibold text-ink">
                  {formatINR(summary.totalExpenses)}
                </div>
                <div className="text-[10px] text-ink-faint">कुल खर्च</div>
              </div>
              <div className="rounded-card border border-indigo/30 bg-indigo/5 p-2.5 text-center shadow-panel">
                <Wallet size={15} className="mx-auto mb-1 text-indigo" />
                <div className="text-[13px] font-semibold text-indigo">
                  {formatINR(summary.balance)}
                </div>
                <div className="text-[10px] text-ink-faint">शेष</div>
              </div>
            </div>

            <h3 className="mb-2 mt-4 px-1 text-[12px] font-medium text-ink-soft">
              कार्यक्रम अनुसार विवरण
            </h3>
            {summary.byEvent.length === 0 ? (
              <p className="py-4 text-center text-[12px] text-ink-soft">
                अभी कोई कार्यक्रम-संबंधी प्रविष्टि नहीं है।
              </p>
            ) : (
              <div className="space-y-2">
                {summary.byEvent.map((ev) => (
                  <div
                    key={ev.eventId}
                    className="rounded-card border border-ink/10 bg-box/90 p-3 shadow-panel"
                  >
                    <div className="text-[13px] font-medium text-ink">{ev.eventTitle}</div>
                    <div className="mt-1 flex justify-between text-[11px] text-ink-faint">
                      <span>चंदा: {formatINR(ev.contributions)}</span>
                      <span>खर्च: {formatINR(ev.expenses)}</span>
                      <span className="font-medium text-indigo">शेष: {formatINR(ev.balance)}</span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}
