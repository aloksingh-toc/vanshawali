import type { ReactNode } from "react";

export function ToolButton({
  icon,
  label,
  onClick,
  variant = "header",
  className = "",
}: {
  icon: ReactNode;
  label: string;
  onClick: () => void;
  variant?: "header" | "toolbar";
  className?: string;
}) {
  const sizeCls = variant === "toolbar" ? "shrink-0 text-[12px]" : "text-[11px]";
  return (
    <button
      onClick={onClick}
      className={`flex items-center gap-1.5 rounded-lg border border-ink/20 bg-white px-2.5 py-1.5 font-medium text-ink transition active:scale-95 ${sizeCls} ${className}`}
    >
      {icon}
      {label}
    </button>
  );
}
