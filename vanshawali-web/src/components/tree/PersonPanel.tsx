import { X, PencilLine, ArrowLeftRight } from "lucide-react";
import { useLang } from "../../i18n";
import type { SelectedPerson } from "./FamilyTree";

interface Props {
  person: SelectedPerson | null;
  onClose: () => void;
  onRequestChange: () => void;
  onFindRelation: () => void;
}

export default function PersonPanel({
  person,
  onClose,
  onRequestChange,
  onFindRelation,
}: Props) {
  const { t } = useLang();
  const open = !!person;
  const node = person?.node;

  const flags: { label: string; cls: string }[] = [];
  if (node?.hl)
    flags.push({ label: t("flag_line"), cls: "border-sindoor/50 bg-[#FDE8E8] text-[#7A2020]" });
  if (node?.x)
    flags.push({ label: t("flag_issueless"), cls: "border-ink/25 bg-paper-deep text-ink-soft" });
  if (node?.q)
    flags.push({ label: t("flag_unconfirmed"), cls: "border-haldi/40 bg-haldi-bg text-[#633806]" });
  if (node?.u)
    flags.push({ label: t("flag_pending"), cls: "border-ink/25 bg-white text-ink-soft" });

  return (
    <>
      {open && (
        <div
          className="absolute inset-0 z-10 bg-ink/10"
          onClick={onClose}
          aria-hidden="true"
        />
      )}
      <div
        className={`absolute inset-x-0 bottom-0 z-20 rounded-t-2xl border-t-[3px] border-indigo bg-box px-4 pb-5 pt-3 shadow-panel transition-transform duration-300 ${
          open ? "translate-y-0" : "translate-y-full"
        }`}
        role="dialog"
        aria-modal="true"
      >
        <div className="mx-auto mb-2 h-1 w-10 rounded-full bg-ink/15" />
        <div className="mb-2 flex items-center justify-between">
          <h3 className="font-display text-lg font-semibold text-indigo">
            {node?.n ?? "—"}
          </h3>
          <button
            onClick={onClose}
            aria-label="बंद करें"
            className="rounded-full p-1.5 text-ink-soft transition active:bg-ink/10"
          >
            <X size={18} />
          </button>
        </div>

        <div className="mb-3 flex flex-wrap items-center gap-1.5">
          <span className="rounded-full border border-indigo/20 bg-indigo/5 px-2.5 py-1 text-[10px] font-medium text-indigo">
            {t("gen")} {person ? toDevanagari(person.generation) : ""}
          </span>
          {flags.map((f) => (
            <span
              key={f.label}
              className={`rounded-full border px-2.5 py-1 text-[10px] ${f.cls}`}
            >
              {f.label}
            </span>
          ))}
        </div>

        {node?.note && (
          <p className="mb-3 rounded-lg bg-paper/60 px-3 py-2 text-[12px] text-ink-soft">
            {node.note}
          </p>
        )}

        <div className="flex gap-2">
          <button
            onClick={onRequestChange}
            className="flex flex-1 items-center justify-center gap-1.5 rounded-lg border border-ink/20 bg-white py-2.5 text-[12px] font-medium text-ink transition active:scale-[0.98]"
          >
            <PencilLine size={14} />
            {t("panel_requestChange")}
          </button>
          <button
            onClick={onFindRelation}
            className="flex flex-1 items-center justify-center gap-1.5 rounded-lg bg-indigo py-2.5 text-[12px] font-medium text-cream transition active:scale-[0.98]"
          >
            <ArrowLeftRight size={14} />
            {t("panel_findRelation")}
          </button>
        </div>
      </div>
    </>
  );
}

const DEV = ["०", "१", "२", "३", "४", "५", "६", "७", "८", "९"];
function toDevanagari(n: number) {
  return String(n)
    .split("")
    .map((c) => DEV[+c] ?? c)
    .join("");
}
