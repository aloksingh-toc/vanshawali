import { useState } from "react";
import { Inbox } from "lucide-react";
import { ApproveRejectButtons, StatusBadge } from "../components/ModerationUI";
import { useModerationQueue } from "../hooks/useModerationQueue";
import { useAuth } from "../lib/auth";
import { apiUrl, type GalleryCommentDetail, type GalleryPostDetail } from "../lib/api";

export default function AdminGalleryInbox() {
  const { isAdmin } = useAuth();
  const [tab, setTab] = useState<"posts" | "comments">("posts");
  const {
    items: posts,
    error: postsError,
    busyId: postsBusyId,
    decide: decidePost,
  } = useModerationQueue<GalleryPostDetail>("/api/gallery/pending", "/api/gallery", isAdmin);
  const {
    items: comments,
    error: commentsError,
    busyId: commentsBusyId,
    decide: decideComment,
  } = useModerationQueue<GalleryCommentDetail>(
    "/api/gallery/comments/pending",
    "/api/gallery/comments",
    isAdmin
  );

  const error = postsError ?? commentsError;

  return (
    <div className="flex h-full flex-col">
      <div className="flex shrink-0 items-center gap-2 border-b border-ink/10 bg-box/85 px-4 py-3 backdrop-blur-sm">
        <Inbox size={17} className="text-indigo" />
        <h2 className="font-display text-base font-semibold text-indigo">गैलरी निवेदन</h2>
      </div>

      <div className="flex shrink-0 gap-1.5 border-b border-ink/10 bg-box/70 px-3 py-2">
        <TabButton active={tab === "posts"} onClick={() => setTab("posts")}>
          फ़ोटो {posts ? `(${posts.length})` : ""}
        </TabButton>
        <TabButton active={tab === "comments"} onClick={() => setTab("comments")}>
          टिप्पणियाँ {comments ? `(${comments.length})` : ""}
        </TabButton>
      </div>

      <div className="flex-1 overflow-y-auto px-3 py-3">
        {error && <p className="mb-2 text-[12px] text-sindoor">{error}</p>}

        {tab === "posts" && (
          <PostsTab posts={posts} busyId={postsBusyId} onDecide={decidePost} />
        )}
        {tab === "comments" && (
          <CommentsTab comments={comments} busyId={commentsBusyId} onDecide={decideComment} />
        )}
      </div>
    </div>
  );
}

function TabButton({
  active,
  onClick,
  children,
}: {
  active: boolean;
  onClick: () => void;
  children: React.ReactNode;
}) {
  return (
    <button
      onClick={onClick}
      className={`rounded-lg px-3 py-1.5 text-[12px] font-medium transition ${
        active ? "bg-indigo text-cream" : "border border-ink/20 bg-white text-ink"
      }`}
    >
      {children}
    </button>
  );
}

function PostsTab({
  posts,
  busyId,
  onDecide,
}: {
  posts: GalleryPostDetail[] | null;
  busyId: number | null;
  onDecide: (id: number, action: "approve" | "reject") => void;
}) {
  if (!posts) return <p className="py-6 text-center text-[12px] text-ink-soft">लोड हो रहा है…</p>;
  if (posts.length === 0)
    return <p className="py-6 text-center text-[12px] text-ink-soft">कोई बाकी फ़ोटो नहीं है।</p>;

  return (
    <div className="space-y-2.5">
      {posts.map((p) => (
        <div key={p.id} className="overflow-hidden rounded-card border border-ink/10 bg-box/90 shadow-panel">
          <img src={apiUrl(p.photoUrl)} alt="" className="h-44 w-full object-cover" />
          <div className="p-3">
            <div className="mb-1.5 flex items-center justify-between gap-2">
              <span className="text-[13px] font-medium text-ink">{p.uploaderName}</span>
              <StatusBadge status={p.status} />
            </div>
            {p.caption && <p className="text-[12px] text-ink-soft">{p.caption}</p>}
            {p.uploaderContact && (
              <p className="mt-1 text-[11px] text-ink-faint">{p.uploaderContact}</p>
            )}

            <ApproveRejectButtons
              busy={busyId === p.id}
              onApprove={() => onDecide(p.id, "approve")}
              onReject={() => onDecide(p.id, "reject")}
            />
          </div>
        </div>
      ))}
    </div>
  );
}

function CommentsTab({
  comments,
  busyId,
  onDecide,
}: {
  comments: GalleryCommentDetail[] | null;
  busyId: number | null;
  onDecide: (id: number, action: "approve" | "reject") => void;
}) {
  if (!comments) return <p className="py-6 text-center text-[12px] text-ink-soft">लोड हो रहा है…</p>;
  if (comments.length === 0)
    return <p className="py-6 text-center text-[12px] text-ink-soft">कोई बाकी टिप्पणी नहीं है।</p>;

  return (
    <div className="space-y-2.5">
      {comments.map((c) => (
        <div key={c.id} className="rounded-card border border-ink/10 bg-box/90 p-3 shadow-panel">
          <p className="text-[13px] font-medium text-ink">{c.commenterName}</p>
          <p className="mt-0.5 text-[12px] text-ink-soft">{c.body}</p>
          <p className="mt-1 text-[11px] text-ink-faint">पोस्ट #{c.postId}</p>

          <ApproveRejectButtons
            busy={busyId === c.id}
            onApprove={() => onDecide(c.id, "approve")}
            onReject={() => onDecide(c.id, "reject")}
          />
        </div>
      ))}
    </div>
  );
}
