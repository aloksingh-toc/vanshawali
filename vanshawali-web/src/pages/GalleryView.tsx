import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Camera, MessageCircle, Send, Loader2, Inbox, ShieldCheck } from "lucide-react";
import { ToolButton } from "../components/ToolButton";
import { useAuth } from "../lib/auth";
import {
  apiUrl,
  fetchApprovedComments,
  fetchApprovedGallery,
  submitGalleryComment,
  type GalleryCommentDetail,
  type GalleryPostDetail,
} from "../lib/api";
import UploadPhotoForm from "../components/gallery/UploadPhotoForm";

export default function GalleryView() {
  const { isAdmin } = useAuth();
  const navigate = useNavigate();
  const [posts, setPosts] = useState<GalleryPostDetail[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [uploadOpen, setUploadOpen] = useState(false);
  const [expandedId, setExpandedId] = useState<number | null>(null);

  const load = useCallback(async () => {
    setError(null);
    try {
      setPosts(await fetchApprovedGallery());
    } catch (err) {
      setError((err as Error).message);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  return (
    <div className="flex h-full flex-col">
      <div className="flex shrink-0 items-center gap-2 border-b border-ink/10 bg-box/85 px-4 py-3 backdrop-blur-sm">
        <Camera size={17} className="text-indigo" />
        <h2 className="flex-1 font-display text-base font-semibold text-indigo">फ़ोटो गैलरी</h2>
        {isAdmin ? (
          <ToolButton
            icon={<Inbox size={13} />}
            label="निवेदन"
            onClick={() => navigate("/admin/gallery")}
          />
        ) : (
          <ToolButton
            icon={<ShieldCheck size={13} />}
            label="Admin"
            onClick={() => navigate("/admin/login")}
          />
        )}
      </div>

      <div className="relative min-h-0 flex-1 overflow-hidden">
        <div className="h-full overflow-y-auto px-3 py-3">
          {error && <p className="mb-2 text-[12px] text-sindoor">{error}</p>}

          {!posts && !error && (
            <p className="py-6 text-center text-[12px] text-ink-soft">लोड हो रहा है…</p>
          )}

          {posts && posts.length === 0 && (
            <p className="py-6 text-center text-[12px] text-ink-soft">
              अभी कोई फ़ोटो नहीं है। सबसे पहले आप साझा करें!
            </p>
          )}

          <div className="space-y-3 pb-14">
            {posts?.map((post) => (
              <GalleryCard
                key={post.id}
                post={post}
                expanded={expandedId === post.id}
                onToggleExpand={() =>
                  setExpandedId((cur) => (cur === post.id ? null : post.id))
                }
                onCommentAdded={load}
              />
            ))}
          </div>
        </div>

        <button
          onClick={() => setUploadOpen(true)}
          className="absolute bottom-4 right-4 flex h-12 w-12 items-center justify-center rounded-full bg-indigo text-cream shadow-panel transition active:scale-95"
          aria-label="फ़ोटो साझा करें"
        >
          <Camera size={20} />
        </button>

        <UploadPhotoForm
          open={uploadOpen}
          onClose={() => setUploadOpen(false)}
          onSubmitted={load}
        />
      </div>
    </div>
  );
}

function GalleryCard({
  post,
  expanded,
  onToggleExpand,
  onCommentAdded,
}: {
  post: GalleryPostDetail;
  expanded: boolean;
  onToggleExpand: () => void;
  onCommentAdded: () => void;
}) {
  const [comments, setComments] = useState<GalleryCommentDetail[] | null>(null);
  const [commentBody, setCommentBody] = useState("");
  const [commenterName, setCommenterName] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [info, setInfo] = useState<string | null>(null);

  useEffect(() => {
    if (expanded && comments === null) {
      fetchApprovedComments(post.id)
        .then(setComments)
        .catch((err) => setError((err as Error).message));
    }
  }, [expanded, comments, post.id]);

  async function handleAddComment(e: React.FormEvent) {
    e.preventDefault();
    if (!commentBody.trim() || !commenterName.trim()) {
      setInfo(null);
      setError("कृपया नाम और टिप्पणी दोनों दर्ज करें");
      return;
    }
    setSubmitting(true);
    setError(null);
    setInfo(null);
    try {
      await submitGalleryComment(post.id, {
        commenterName: commenterName.trim(),
        body: commentBody.trim(),
      });
      setCommentBody("");
      setCommenterName("");
      setInfo("टिप्पणी भेज दी गई है, Admin समीक्षा के बाद दिखेगी।");
      onCommentAdded();
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="overflow-hidden rounded-card border border-ink/10 bg-box/90 shadow-panel">
      <img
        src={apiUrl(post.photoUrl)}
        alt={post.caption ?? ""}
        className="h-56 w-full object-cover"
      />
      <div className="p-3">
        {post.caption && <p className="text-[13px] text-ink">{post.caption}</p>}
        <p className="mt-1 text-[11px] text-ink-faint">{post.uploaderName}</p>

        <button
          onClick={onToggleExpand}
          className="mt-2 flex items-center gap-1.5 text-[12px] font-medium text-indigo"
        >
          <MessageCircle size={13} />
          {post.approvedCommentCount > 0
            ? `${post.approvedCommentCount} टिप्पणियाँ`
            : "टिप्पणी करें"}
        </button>

        {expanded && (
          <div className="mt-2 space-y-2 border-t border-ink/10 pt-2">
            {comments === null && (
              <p className="text-[11px] text-ink-soft">लोड हो रहा है…</p>
            )}
            {comments?.length === 0 && (
              <p className="text-[11px] text-ink-soft">अभी कोई टिप्पणी नहीं है।</p>
            )}
            {comments?.map((c) => (
              <div key={c.id} className="rounded-lg bg-ink/[0.03] px-2.5 py-1.5">
                <p className="text-[11px] font-medium text-ink">{c.commenterName}</p>
                <p className="text-[12px] text-ink-soft">{c.body}</p>
              </div>
            ))}

            <form onSubmit={handleAddComment} className="space-y-1.5 pt-1">
              <input
                value={commenterName}
                onChange={(e) => setCommenterName(e.target.value)}
                placeholder="आपका नाम"
                className="w-full rounded-lg border border-ink/20 bg-white px-2.5 py-1.5 text-[12px] text-ink outline-none focus:border-indigo"
              />
              <div className="flex gap-1.5">
                <input
                  value={commentBody}
                  onChange={(e) => setCommentBody(e.target.value)}
                  placeholder="टिप्पणी लिखें…"
                  className="flex-1 rounded-lg border border-ink/20 bg-white px-2.5 py-1.5 text-[12px] text-ink outline-none focus:border-indigo"
                />
                <button
                  type="submit"
                  disabled={submitting}
                  className="flex items-center justify-center rounded-lg bg-indigo px-3 text-cream transition active:scale-95 disabled:opacity-60"
                >
                  {submitting ? (
                    <Loader2 size={14} className="animate-spin" />
                  ) : (
                    <Send size={14} />
                  )}
                </button>
              </div>
              {error && <p className="text-[11px] text-sindoor">{error}</p>}
              {info && <p className="text-[11px] text-leaf">{info}</p>}
            </form>
          </div>
        )}
      </div>
    </div>
  );
}
