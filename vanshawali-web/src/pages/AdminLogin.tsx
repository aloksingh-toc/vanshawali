import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { LogIn, Loader2 } from "lucide-react";
import { useAuth } from "../lib/auth";

export default function AdminLogin() {
  const { login, isAdmin, session, logout } = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    setBusy(true);
    try {
      await login(username, password);
      navigate("/tree");
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setBusy(false);
    }
  }

  if (isAdmin && session) {
    return (
      <div className="flex h-full flex-col items-center justify-center gap-4 px-6 text-center">
        <p className="text-sm text-ink">
          आप <span className="font-semibold text-indigo">{session.displayName}</span> के रूप में लॉगिन हैं।
        </p>
        <div className="flex gap-2">
          <button
            onClick={() => navigate("/tree")}
            className="rounded-lg bg-indigo px-4 py-2 text-[12px] font-medium text-cream transition active:scale-95"
          >
            वंश-वृक्ष पर जाएँ
          </button>
          <button
            onClick={logout}
            className="rounded-lg border border-ink/20 bg-white px-4 py-2 text-[12px] font-medium text-ink transition active:scale-95"
          >
            लॉग आउट करें
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="flex h-full flex-col items-center justify-center px-6">
      <form
        onSubmit={handleSubmit}
        className="w-full max-w-xs space-y-3 rounded-card border border-ink/10 bg-box/90 p-5 shadow-panel backdrop-blur-sm"
      >
        <h2 className="mb-1 text-center font-display text-lg font-semibold text-indigo">
          Admin लॉगिन
        </h2>
        <div>
          <label className="mb-1 block text-[11px] text-ink-soft">उपयोगकर्ता-नाम</label>
          <input
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoComplete="username"
            required
            className="w-full rounded-lg border border-ink/20 bg-white px-3 py-2 text-[13px] text-ink outline-none focus:border-indigo"
          />
        </div>
        <div>
          <label className="mb-1 block text-[11px] text-ink-soft">पासवर्ड</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
            required
            className="w-full rounded-lg border border-ink/20 bg-white px-3 py-2 text-[13px] text-ink outline-none focus:border-indigo"
          />
        </div>
        {error && <p className="text-[12px] text-sindoor">{error}</p>}
        <button
          type="submit"
          disabled={busy}
          className="flex w-full items-center justify-center gap-1.5 rounded-lg bg-indigo py-2.5 text-[13px] font-medium text-cream transition active:scale-[0.98] disabled:opacity-60"
        >
          {busy ? <Loader2 size={15} className="animate-spin" /> : <LogIn size={15} />}
          लॉगिन करें
        </button>
      </form>
    </div>
  );
}
