import { useEffect, useState } from "react";
import { searchPersons, type PersonSearchResult } from "../lib/api";

export function usePersonSearch(query: string, limit = 6) {
  const [results, setResults] = useState<PersonSearchResult[]>([]);

  useEffect(() => {
    const id = setTimeout(() => {
      if (!query.trim()) {
        setResults([]);
        return;
      }
      searchPersons(query)
        .then((r) => setResults(r.slice(0, limit)))
        .catch(() => setResults([]));
    }, 200);
    return () => clearTimeout(id);
  }, [query, limit]);

  return results;
}
