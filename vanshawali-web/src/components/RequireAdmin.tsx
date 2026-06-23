import type { ReactNode } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../lib/auth";

export default function RequireAdmin({
  children,
  message = "यह पृष्ठ केवल Admin के लिए है।",
}: {
  children: ReactNode;
  message?: string;
}) {
  const { isAdmin } = useAuth();
  const navigate = useNavigate();

  if (!isAdmin) {
    return (
      <div className="flex h-full flex-col items-center justify-center gap-3 px-6 text-center">
        <p className="text-sm text-ink-soft">{message}</p>
        <button
          onClick={() => navigate("/admin/login")}
          className="rounded-lg bg-indigo px-4 py-2 text-[12px] font-medium text-cream transition active:scale-95"
        >
          Admin लॉगिन
        </button>
      </div>
    );
  }

  return <>{children}</>;
}
