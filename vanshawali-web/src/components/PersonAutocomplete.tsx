import { X } from "lucide-react";
import { Field, fieldInputClass } from "./FormSheet";
import { usePersonSearch } from "../hooks/usePersonSearch";
import type { PersonSearchResult } from "../lib/api";

export function PersonAutocomplete({
  label,
  query,
  onQueryChange,
  selected,
  onSelect,
  placeholder = "नाम टाइप करें…",
}: {
  label: string;
  query: string;
  onQueryChange: (q: string) => void;
  selected: PersonSearchResult | null;
  onSelect: (p: PersonSearchResult | null) => void;
  placeholder?: string;
}) {
  const results = usePersonSearch(query);

  return (
    <div className="relative">
      <Field label={label}>
        {selected ? (
          <div className="flex items-center justify-between rounded-lg border border-indigo/30 bg-indigo/5 px-3 py-2">
            <span className="text-[13px] text-ink">{selected.name}</span>
            <button type="button" onClick={() => onSelect(null)} className="text-ink-faint">
              <X size={14} />
            </button>
          </div>
        ) : (
          <input
            value={query}
            onChange={(e) => onQueryChange(e.target.value)}
            placeholder={placeholder}
            className={fieldInputClass}
          />
        )}
      </Field>
      {!selected && results.length > 0 && (
        <div className="absolute z-10 mt-1 w-full overflow-hidden rounded-lg border border-ink/15 bg-white shadow-panel">
          {results.map((p) => (
            <button
              key={p.id}
              type="button"
              onClick={() => {
                onSelect(p);
                onQueryChange("");
              }}
              className="block w-full px-3 py-2 text-left text-[13px] text-ink transition hover:bg-ink/[0.04]"
            >
              {p.name}
            </button>
          ))}
        </div>
      )}
    </div>
  );
}
