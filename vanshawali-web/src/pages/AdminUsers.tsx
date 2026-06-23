import { useCallback, useEffect, useState } from "react";
import { Loader2, Plus, Trash2, Users } from "lucide-react";
import { Field, FormSheet, fieldInputClass } from "../components/FormSheet";
import { useAuth } from "../lib/auth";
import type { AppUserCreateInput, AppUserDetail, AppUserRole } from "../lib/api";

const ROLE_OPTIONS: { value: AppUserRole; label: string }[] = [
  { value: "ADMIN", label: "Admin" },
  { value: "FUND_MANAGER", label: "कोषाध्यक्ष" },
];

const ROLE_LABEL: Record<AppUserRole, string> = Object.fromEntries(
  ROLE_OPTIONS.map((o) => [o.value, o.label])
) as Record<AppUserRole, string>;

export default function AdminUsers() {
  const { session, authFetch } = useAuth();
  const [users, setUsers] = useState<AppUserDetail[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [formOpen, setFormOpen] = useState(false);
  const [busyId, setBusyId] = useState<number | null>(null);

  const isSiteAdmin = session?.role === "ADMIN";

  const load = useCallback(async () => {
    setError(null);
    try {
      const res = await authFetch("/api/users");
      if (!res.ok) throw new Error(`सूची लोड नहीं हो सकी (${res.status})`);
      setUsers(await res.json());
    } catch (err) {
      setError((err as Error).message);
    }
  }, [authFetch]);

  useEffect(() => {
    if (isSiteAdmin) load();
  }, [isSiteAdmin, load]);

  async function handleDelete(user: AppUserDetail) {
    if (!window.confirm(`क्या आप "${user.username}" को मिटाना चाहते हैं?`)) return;
    setBusyId(user.id);
    setError(null);
    try {
      const res = await authFetch(`/api/users/${user.id}`, { method: "DELETE" });
      if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body.message ?? `मिटाया नहीं जा सका (${res.status})`);
      }
      await load();
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setBusyId(null);
    }
  }

  if (!isSiteAdmin) {
    return (
      <div className="flex h-full flex-col items-center justify-center gap-3 px-6 text-center">
        <p className="text-sm text-ink-soft">यह पृष्ठ केवल Admin के लिए है।</p>
      </div>
    );
  }

  return (
    <div className="flex h-full flex-col">
      <div className="flex shrink-0 items-center gap-2 border-b border-ink/10 bg-box/85 px-4 py-3 backdrop-blur-sm">
        <Users size={17} className="text-indigo" />
        <h2 className="flex-1 font-display text-base font-semibold text-indigo">
          उपयोगकर्ता प्रबंधन
        </h2>
        <button
          onClick={() => setFormOpen(true)}
          className="flex items-center gap-1.5 rounded-lg bg-indigo px-2.5 py-1.5 text-[11px] font-medium text-cream transition active:scale-95"
        >
          <Plus size={13} />
          नया
        </button>
      </div>

      <div className="flex-1 overflow-y-auto px-3 py-3">
        {error && <p className="mb-2 text-[12px] text-sindoor">{error}</p>}
        {!users && !error && (
          <p className="py-6 text-center text-[12px] text-ink-soft">लोड हो रहा है…</p>
        )}

        <div className="space-y-2.5">
          {users?.map((user) => {
            const isSelf = user.username === session?.username;
            return (
              <div
                key={user.id}
                className="flex items-center justify-between gap-2 rounded-card border border-ink/10 bg-box/90 p-3 shadow-panel"
              >
                <div className="min-w-0 flex-1">
                  <div className="text-[13px] font-medium text-ink">
                    {user.displayName || user.username}
                  </div>
                  <div className="text-[11px] text-ink-faint">
                    @{user.username} · {ROLE_LABEL[user.role]}
                  </div>
                </div>
                <button
                  onClick={() => handleDelete(user)}
                  disabled={isSelf || busyId === user.id}
                  title={isSelf ? "अपना ही खाता नहीं मिटा सकते" : undefined}
                  className="shrink-0 rounded-lg border border-sindoor/40 bg-[#FDE8E8] p-1.5 text-[#7A2020] transition active:scale-95 disabled:opacity-40"
                  aria-label="मिटाएं"
                >
                  {busyId === user.id ? (
                    <Loader2 size={14} className="animate-spin" />
                  ) : (
                    <Trash2 size={14} />
                  )}
                </button>
              </div>
            );
          })}
        </div>
      </div>

      {formOpen && (
        <UserForm
          onClose={() => setFormOpen(false)}
          onSaved={() => {
            setFormOpen(false);
            load();
          }}
        />
      )}
    </div>
  );
}

function UserForm({ onClose, onSaved }: { onClose: () => void; onSaved: () => void }) {
  const { authFetch } = useAuth();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [displayName, setDisplayName] = useState("");
  const [role, setRole] = useState<AppUserRole>("FUND_MANAGER");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!username.trim() || password.length < 6) {
      setError("उपयोगकर्ता-नाम आवश्यक है और पासवर्ड कम से कम 6 अक्षर का होना चाहिए");
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      const input: AppUserCreateInput = {
        username: username.trim(),
        password,
        displayName: displayName.trim() || null,
        role,
      };
      const res = await authFetch("/api/users", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(input),
      });
      if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body.message ?? `बनाया नहीं जा सका (${res.status})`);
      }
      onSaved();
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <FormSheet
      title="नया उपयोगकर्ता"
      onClose={onClose}
      onSubmit={handleSubmit}
      submitting={submitting}
      submitLabel="बनाएं"
      error={error}
    >
      <Field label="उपयोगकर्ता-नाम">
        <input
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          className={fieldInputClass}
        />
      </Field>

      <Field label="पासवर्ड">
        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className={fieldInputClass}
        />
      </Field>

      <Field label="प्रदर्शन नाम (वैकल्पिक)">
        <input
          value={displayName}
          onChange={(e) => setDisplayName(e.target.value)}
          className={fieldInputClass}
        />
      </Field>

      <Field label="भूमिका">
        <select
          value={role}
          onChange={(e) => setRole(e.target.value as AppUserRole)}
          className={fieldInputClass}
        >
          {ROLE_OPTIONS.map((o) => (
            <option key={o.value} value={o.value}>
              {o.label}
            </option>
          ))}
        </select>
      </Field>
    </FormSheet>
  );
}
