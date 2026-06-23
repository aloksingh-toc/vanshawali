import { useState } from "react";
import { X, Send, Loader2, CheckCircle2 } from "lucide-react";
import { submitGalleryPost } from "../../lib/api";
import FileUploadField from "../FileUploadField";

interface Props {
  open: boolean;
  onClose: () => void;
  onSubmitted: () => void;
}

export default function UploadPhotoForm({ open, onClose, onSubmitted }: Props) {
  const [photoUrl, setPhotoUrl] = useState<string | null>(null);
  const [caption, setCaption] = useState("");
  const [uploaderName, setUploaderName] = useState("");
  const [uploaderContact, setUploaderContact] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [done, setDone] = useState(false);

  function reset() {
    setPhotoUrl(null);
    setCaption("");
    setUploaderName("");
    setUploaderContact("");
    setError(null);
    setDone(false);
  }

  function handleClose() {
    reset();
    onClose();
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);

    if (!photoUrl) {
      setError("कृपया एक फ़ोटो अपलोड करें");
      return;
    }
    if (!uploaderName.trim()) {
      setError("कृपया अपना नाम दर्ज करें");
      return;
    }

    setSubmitting(true);
    try {
      await submitGalleryPost({
        photoUrl,
        caption: caption.trim() || null,
        uploaderName: uploaderName.trim(),
        uploaderContact: uploaderContact.trim() || null,
      });
      setDone(true);
      onSubmitted();
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <>
      {open && (
        <div className="absolute inset-0 z-10 bg-ink/10" onClick={handleClose} aria-hidden="true" />
      )}
      <div
        className={`absolute inset-x-0 bottom-0 z-20 max-h-[85%] overflow-y-auto rounded-t-2xl border-t-[3px] border-indigo bg-box px-4 pb-5 pt-3 shadow-panel transition-transform duration-300 ${
          open ? "translate-y-0" : "translate-y-full"
        }`}
        role="dialog"
        aria-modal="true"
      >
        <div className="mx-auto mb-2 h-1 w-10 rounded-full bg-ink/15" />
        <div className="mb-3 flex items-center justify-between">
          <h3 className="font-display text-lg font-semibold text-indigo">फ़ोटो साझा करें</h3>
          <button
            onClick={handleClose}
            aria-label="बंद करें"
            className="rounded-full p-1.5 text-ink-soft transition active:bg-ink/10"
          >
            <X size={18} />
          </button>
        </div>

        {done ? (
          <div className="flex flex-col items-center gap-2 py-6 text-center">
            <CheckCircle2 size={32} className="text-indigo" />
            <p className="text-[13px] text-ink">
              आपकी फ़ोटो भेज दी गई है। Admin इसकी समीक्षा करेंगे।
            </p>
            <button
              onClick={handleClose}
              className="mt-2 rounded-lg bg-indigo px-4 py-2 text-[12px] font-medium text-cream transition active:scale-95"
            >
              ठीक है
            </button>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-3">
            <Field label="फ़ोटो">
              <FileUploadField folder="gallery" onUploaded={setPhotoUrl} />
            </Field>

            <Field label="कैप्शन (वैकल्पिक)">
              <textarea
                value={caption}
                onChange={(e) => setCaption(e.target.value)}
                rows={2}
                className="w-full rounded-lg border border-ink/20 bg-white px-3 py-2 text-[13px] text-ink outline-none focus:border-indigo"
              />
            </Field>

            <Field label="आपका नाम">
              <input
                value={uploaderName}
                onChange={(e) => setUploaderName(e.target.value)}
                className="w-full rounded-lg border border-ink/20 bg-white px-3 py-2 text-[13px] text-ink outline-none focus:border-indigo"
              />
            </Field>

            <Field label="संपर्क (फ़ोन/ईमेल, वैकल्पिक)">
              <input
                value={uploaderContact}
                onChange={(e) => setUploaderContact(e.target.value)}
                className="w-full rounded-lg border border-ink/20 bg-white px-3 py-2 text-[13px] text-ink outline-none focus:border-indigo"
              />
            </Field>

            {error && <p className="text-[12px] text-sindoor">{error}</p>}

            <button
              type="submit"
              disabled={submitting}
              className="flex w-full items-center justify-center gap-1.5 rounded-lg bg-indigo py-2.5 text-[12px] font-medium text-cream transition active:scale-[0.98] disabled:opacity-60"
            >
              {submitting ? <Loader2 size={14} className="animate-spin" /> : <Send size={14} />}
              भेजें
            </button>
          </form>
        )}
      </div>
    </>
  );
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <label className="block">
      <span className="mb-1 block text-[11px] text-ink-soft">{label}</span>
      {children}
    </label>
  );
}
