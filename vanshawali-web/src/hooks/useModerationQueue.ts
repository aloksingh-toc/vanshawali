import { useCallback, useEffect, useState } from "react";
import { useAuth } from "../lib/auth";

type DecideAction = "approve" | "reject";

export function useModerationQueue<T>(listEndpoint: string, actionBase: string, enabled: boolean) {
  const { authFetch } = useAuth();
  const [items, setItems] = useState<T[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [busyId, setBusyId] = useState<number | null>(null);

  const load = useCallback(async () => {
    setError(null);
    try {
      const res = await authFetch(listEndpoint);
      if (!res.ok) throw new Error(`लोड नहीं हो सका (${res.status})`);
      setItems(await res.json());
    } catch (err) {
      setError((err as Error).message);
    }
  }, [authFetch, listEndpoint]);

  useEffect(() => {
    if (enabled) load();
  }, [enabled, load]);

  const decide = useCallback(
    async (id: number, action: DecideAction, body: Record<string, unknown> = {}) => {
      setBusyId(id);
      setError(null);
      try {
        const res = await authFetch(`${actionBase}/${id}/${action}`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(body),
        });
        if (!res.ok) {
          const errBody = await res.json().catch(() => ({}));
          throw new Error(errBody.message ?? `कार्रवाई विफल (${res.status})`);
        }
        await load();
      } catch (err) {
        setError((err as Error).message);
      } finally {
        setBusyId(null);
      }
    },
    [authFetch, actionBase, load]
  );

  return { items, error, busyId, decide, reload: load };
}
