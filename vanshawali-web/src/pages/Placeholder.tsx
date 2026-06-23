import { useNavigate } from "react-router-dom";
import { Hammer, ArrowLeft } from "lucide-react";

export default function Placeholder({ title }: { title: string }) {
  const navigate = useNavigate();
  return (
    <div className="mx-auto flex max-w-2xl flex-col items-center px-4 py-16 text-center">
      <div className="flex h-16 w-16 items-center justify-center rounded-full border border-ink/15 bg-box/80 text-sindoor backdrop-blur-sm">
        <Hammer size={26} />
      </div>
      <h2 className="mt-5 font-display text-2xl text-ink">{title}</h2>
      <p className="mt-2 max-w-sm text-[13px] text-ink-soft">
        यह खंड निर्माणाधीन है — आने वाले चरणों में जोड़ा जाएगा।
      </p>
      <button
        onClick={() => navigate("/")}
        className="mt-6 inline-flex items-center gap-1.5 rounded-full border border-ink/20 bg-box/80 px-4 py-2 text-[12px] font-medium text-ink backdrop-blur-sm transition hover:bg-box"
      >
        <ArrowLeft size={14} />
        मुख्य पृष्ठ
      </button>
    </div>
  );
}
