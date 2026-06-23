import { useEffect, useRef, useState } from "react";
import { ArrowLeftRight, Loader2, Search, X } from "lucide-react";
import { usePersonSearch } from "../hooks/usePersonSearch";
import {
  fetchRelation,
  type PersonSearchResult,
  type RelationResult,
} from "../lib/api";

export default function RelationFinder() {
  const [from, setFrom] = useState<PersonSearchResult | null>(null);
  const [to, setTo] = useState<PersonSearchResult | null>(null);
  const [result, setResult] = useState<RelationResult | null>(null);
  const [showPaths, setShowPaths] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleFind() {
    if (!from || !to) return;
    setLoading(true);
    setError(null);
    setResult(null);
    try {
      setResult(await fetchRelation(from.id, to.id));
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="space-y-4 px-3 py-4">
      <div className="flex items-center gap-2 px-1">
        <ArrowLeftRight size={18} className="text-indigo" />
        <h2 className="font-display text-lg font-semibold text-indigo">रिश्ता खोजें</h2>
      </div>
      <p className="px-1 text-[12px] text-ink-soft">
        वंशवृक्ष में किसी भी दो व्यक्तियों के बीच का रिश्ता जानने के लिए दोनों नाम चुनें।
      </p>

      <div className="space-y-3 rounded-card border border-ink/10 bg-box/90 p-3 shadow-panel">
        <PersonPicker label="पहला व्यक्ति" selected={from} onSelect={setFrom} />
        <PersonPicker label="दूसरा व्यक्ति" selected={to} onSelect={setTo} />

        <button
          onClick={handleFind}
          disabled={!from || !to || loading}
          className="flex w-full items-center justify-center gap-2 rounded-lg bg-indigo py-2.5 text-[13px] font-semibold text-cream transition active:scale-[0.98] disabled:opacity-50"
        >
          {loading ? <Loader2 size={15} className="animate-spin" /> : <Search size={15} />}
          रिश्ता बताएं
        </button>

        {error && <p className="text-[12px] text-sindoor">{error}</p>}
      </div>

      {result && (
        <div className="space-y-2.5 rounded-card border border-ink/10 bg-box/90 p-4 shadow-panel">
          <div className="text-center">
            <p className="text-[12px] text-ink-soft">
              <span className="font-medium text-ink">{result.fromName}</span>
              {" "}और{" "}
              <span className="font-medium text-ink">{result.toName}</span>
            </p>
            <p className="mt-2 text-[15px] font-semibold text-indigo">{result.relationLabel}</p>
          </div>

          <div className="gilt-divider" />

          <div className="grid grid-cols-2 gap-2 text-center text-[11px] text-ink-soft">
            <div>
              <div className="text-[13px] font-semibold text-ink">{result.commonAncestorName}</div>
              <div>उभयनिष्ठ पूर्वज</div>
            </div>
            <div>
              <div className="text-[13px] font-semibold text-ink">{result.generationGap}</div>
              <div>पीढ़ी का अंतर</div>
            </div>
          </div>

          <button
            onClick={() => setShowPaths((s) => !s)}
            className="w-full text-center text-[11px] font-medium text-indigo underline"
          >
            {showPaths ? "वंश-पथ छिपाएं" : "वंश-पथ देखें"}
          </button>

          {showPaths && (
            <div className="space-y-2 border-t border-ink/10 pt-2 text-[11px]">
              <PathRow label={result.fromName} path={result.pathFrom} />
              <PathRow label={result.toName} path={result.pathTo} />
            </div>
          )}
        </div>
      )}
    </div>
  );
}

function PathRow({ label, path }: { label: string; path: string[] }) {
  return (
    <div>
      <div className="mb-1 font-medium text-ink">{label} का वंश-पथ</div>
      <div className="flex flex-wrap items-center gap-1 text-ink-soft">
        {path.map((name, i) => (
          <span key={i} className="flex items-center gap-1">
            <span className="rounded-full bg-ink/[0.05] px-2 py-0.5">{name}</span>
            {i < path.length - 1 && <span className="text-ink-faint">→</span>}
          </span>
        ))}
      </div>
    </div>
  );
}

function PersonPicker({
  label,
  selected,
  onSelect,
}: {
  label: string;
  selected: PersonSearchResult | null;
  onSelect: (p: PersonSearchResult | null) => void;
}) {
  const [query, setQuery] = useState("");
  const [open, setOpen] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);
  const results = usePersonSearch(query, 8);

  useEffect(() => {
    function onClickOutside(e: MouseEvent) {
      if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
        setOpen(false);
      }
    }
    document.addEventListener("mousedown", onClickOutside);
    return () => document.removeEventListener("mousedown", onClickOutside);
  }, []);

  if (selected) {
    return (
      <div>
        <div className="mb-1 text-[11px] font-medium text-ink-soft">{label}</div>
        <div className="flex items-center justify-between rounded-lg border border-indigo/30 bg-indigo/5 px-3 py-2">
          <span className="text-[13px] font-medium text-ink">{selected.name}</span>
          <button
            onClick={() => {
              onSelect(null);
              setQuery("");
            }}
            className="text-ink-faint transition active:scale-90"
            aria-label="हटाएं"
          >
            <X size={15} />
          </button>
        </div>
      </div>
    );
  }

  return (
    <div ref={containerRef} className="relative">
      <div className="mb-1 text-[11px] font-medium text-ink-soft">{label}</div>
      <div className="flex items-center gap-1.5 rounded-lg border border-ink/20 bg-white px-2.5 py-2">
        <Search size={14} className="shrink-0 text-ink-faint" />
        <input
          value={query}
          onChange={(e) => {
            setQuery(e.target.value);
            setOpen(true);
          }}
          onFocus={() => setOpen(true)}
          placeholder="नाम टाइप करें…"
          className="w-full bg-transparent text-[13px] text-ink outline-none"
        />
      </div>
      {open && results.length > 0 && (
        <div className="absolute z-10 mt-1 w-full overflow-hidden rounded-lg border border-ink/15 bg-white shadow-panel">
          {results.map((p) => (
            <button
              key={p.id}
              onClick={() => {
                onSelect(p);
                setOpen(false);
              }}
              className="flex w-full flex-col items-start gap-0.5 border-b border-ink/[0.06] px-3 py-2 text-left transition last:border-0 hover:bg-ink/[0.04]"
            >
              <span className="text-[13px] text-ink">{p.name}</span>
              {p.parentName && (
                <span className="text-[10px] text-ink-faint">{p.parentName} के पुत्र/पुत्री</span>
              )}
            </button>
          ))}
        </div>
      )}
    </div>
  );
}
