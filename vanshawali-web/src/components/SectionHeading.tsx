interface Props {
  title: string;
  icon?: React.ReactNode;
  live?: boolean;
  action?: string;
  onAction?: () => void;
}

export default function SectionHeading({
  title,
  icon,
  live,
  action,
  onAction,
}: Props) {
  return (
    <div className="mb-2 flex items-center justify-between">
      <div className="flex items-center gap-2 text-[13px] font-semibold text-ink">
        {live && (
          <span className="h-1.5 w-1.5 animate-pulseDot rounded-full bg-leaf" />
        )}
        {icon}
        {title}
      </div>
      {action && (
        <button
          onClick={onAction}
          className="text-[11px] font-medium text-sindoor transition hover:text-sindoor-soft"
        >
          {action} →
        </button>
      )}
    </div>
  );
}
