import { useRef, useState } from "react";
import { Upload, Loader2, Check } from "lucide-react";
import { apiUrl } from "../lib/api";

interface Props {
  folder: string;
  label?: string;
  onUploaded: (url: string) => void;
}

export default function FileUploadField({ folder, label, onUploaded }: Props) {
  const inputRef = useRef<HTMLInputElement>(null);
  const [status, setStatus] = useState<"idle" | "uploading" | "done" | "error">("idle");

  async function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    if (!file) return;

    setStatus("uploading");
    try {
      const form = new FormData();
      form.append("file", file);
      form.append("folder", folder);
      const res = await fetch(apiUrl("/api/uploads"), { method: "POST", body: form });
      if (!res.ok) throw new Error("upload failed");
      const data: { url: string } = await res.json();
      onUploaded(data.url);
      setStatus("done");
    } catch {
      setStatus("error");
    }
  }

  return (
    <button
      type="button"
      onClick={() => inputRef.current?.click()}
      className="flex w-full items-center justify-center gap-2 rounded-lg border-[1.5px] border-dashed border-ink/25 px-3 py-2.5 text-[12px] text-ink-faint transition hover:border-ink/40"
    >
      <input
        ref={inputRef}
        type="file"
        accept="image/*"
        className="hidden"
        onChange={handleChange}
      />
      {status === "uploading" && <Loader2 size={15} className="animate-spin" />}
      {status === "done" && <Check size={15} className="text-leaf" />}
      {(status === "idle" || status === "error") && <Upload size={15} />}
      <span>
        {status === "uploading"
          ? "अपलोड हो रहा है..."
          : status === "done"
            ? "अपलोड हो गया"
            : status === "error"
              ? "असफल — फिर कोशिश करें"
              : label ?? "फ़ोटो चुनें"}
      </span>
    </button>
  );
}
