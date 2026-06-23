import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
  type ReactNode,
} from "react";
import { apiUrl } from "./api";

interface AdminSession {
  token: string;
  username: string;
  displayName: string;
  role: string;
}

interface AuthCtx {
  session: AdminSession | null;
  isAdmin: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  authFetch: (path: string, init?: RequestInit) => Promise<Response>;
}

const STORAGE_KEY = "vanshawali-admin-session";

const AuthContext = createContext<AuthCtx | null>(null);

function readStoredSession(): AdminSession | null {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? (JSON.parse(raw) as AdminSession) : null;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<AdminSession | null>(() => readStoredSession());

  const login = useCallback(async (username: string, password: string) => {
    const res = await fetch(apiUrl("/api/auth/login"), {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password }),
    });
    if (!res.ok) {
      throw new Error(
        res.status === 401 ? "उपयोगकर्ता-नाम या पासवर्ड गलत है" : `लॉगिन विफल (${res.status})`
      );
    }
    const data: { token: string; username: string; displayName: string; role: string } =
      await res.json();
    const next: AdminSession = {
      token: data.token,
      username: data.username,
      displayName: data.displayName,
      role: data.role,
    };
    localStorage.setItem(STORAGE_KEY, JSON.stringify(next));
    setSession(next);
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem(STORAGE_KEY);
    setSession(null);
  }, []);

  const authFetch = useCallback(
    async (path: string, init: RequestInit = {}) => {
      const headers = new Headers(init.headers);
      if (session) headers.set("Authorization", `Bearer ${session.token}`);
      const res = await fetch(apiUrl(path), { ...init, headers });
      if (res.status === 401 || res.status === 403) {
        // token expired/invalid — drop the local session so the UI falls back to login
        localStorage.removeItem(STORAGE_KEY);
        setSession(null);
      }
      return res;
    },
    [session]
  );

  const value = useMemo<AuthCtx>(
    () => ({ session, isAdmin: !!session, login, logout, authFetch }),
    [session, login, logout, authFetch]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
