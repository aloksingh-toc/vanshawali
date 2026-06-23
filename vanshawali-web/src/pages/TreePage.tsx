import { useEffect, useRef, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { Maximize2, FoldVertical, UnfoldVertical, RotateCw, ShieldCheck, LogOut, Inbox, Printer } from "lucide-react";
import { useLang } from "../i18n";
import { ToolButton } from "../components/ToolButton";
import { fetchTree } from "../lib/api";
import { useAuth } from "../lib/auth";
import type { TreeNode } from "../types/tree";
import FamilyTree, {
  type FamilyTreeHandle,
  type SelectedPerson,
} from "../components/tree/FamilyTree";
import PersonPanel from "../components/tree/PersonPanel";
import AdminEditPanel from "../components/tree/AdminEditPanel";
import RequestChangeForm from "../components/tree/RequestChangeForm";

export default function TreePage() {
  const { t } = useLang();
  const { isAdmin, logout } = useAuth();
  const navigate = useNavigate();
  const treeRef = useRef<FamilyTreeHandle>(null);
  const [params] = useSearchParams();
  const [selected, setSelected] = useState<SelectedPerson | null>(null);
  const [requestTarget, setRequestTarget] = useState<SelectedPerson | null>(null);
  const [resultCount, setResultCount] = useState<number | null>(null);
  const [tree, setTree] = useState<TreeNode | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [reloadKey, setReloadKey] = useState(0);

  const q = params.get("q") ?? "";

  useEffect(() => {
    let cancelled = false;
    setError(null);
    fetchTree()
      .then((data) => {
        if (!cancelled) setTree(data);
      })
      .catch((err: Error) => {
        if (!cancelled) setError(err.message);
      });
    return () => {
      cancelled = true;
    };
  }, [reloadKey]);

  // run the search coming from the header bar once the tree is mounted
  useEffect(() => {
    if (!q) {
      setResultCount(null);
      return;
    }
    const id = setTimeout(() => {
      const n = treeRef.current?.search(q) ?? 0;
      setResultCount(n);
    }, 200);
    return () => clearTimeout(id);
  }, [q, tree]);

  if (error) {
    return (
      <div className="flex h-full flex-col items-center justify-center gap-3 px-6 text-center">
        <p className="text-sm text-ink-soft">{error}</p>
        <button
          onClick={() => setReloadKey((k) => k + 1)}
          className="flex items-center gap-1.5 rounded-lg bg-indigo px-4 py-2 text-[12px] font-medium text-cream transition active:scale-95"
        >
          <RotateCw size={14} />
          {t("retry")}
        </button>
      </div>
    );
  }

  if (!tree) {
    return (
      <div className="flex h-full items-center justify-center">
        <p className="text-sm text-ink-soft">{t("loading")}</p>
      </div>
    );
  }

  return (
    <div className="flex h-full flex-col">
      {/* toolbar */}
      <div className="no-scrollbar flex shrink-0 items-center gap-1.5 overflow-x-auto border-b border-ink/10 bg-box/85 px-3 py-2 backdrop-blur-sm">
        <ToolButton
          variant="toolbar"
          icon={<UnfoldVertical size={15} />}
          label={t("tree_expand")}
          onClick={() => treeRef.current?.expandAll()}
        />
        <ToolButton
          variant="toolbar"
          icon={<FoldVertical size={15} />}
          label={t("tree_collapse")}
          onClick={() => treeRef.current?.collapseAll()}
        />
        <ToolButton
          variant="toolbar"
          icon={<Maximize2 size={15} />}
          label={t("tree_fit")}
          onClick={() => treeRef.current?.fit()}
        />
        <ToolButton
          variant="toolbar"
          icon={<Printer size={15} />}
          label={t("tree_print")}
          onClick={() => treeRef.current?.exportPrint()}
        />
        {q && (
          <span className="ml-auto truncate rounded-full bg-sindoor/10 px-2.5 py-1 text-[11px] font-medium text-sindoor">
            “{q}” · {resultCount ?? 0} {t("searchResults")}
          </span>
        )}
        {!q && isAdmin && (
          <ToolButton
            className="ml-auto"
            icon={<Inbox size={13} />}
            label="निवेदन"
            onClick={() => navigate("/admin/requests")}
          />
        )}
        {!q && (
          <button
            onClick={() => (isAdmin ? logout() : navigate("/admin/login"))}
            className={`flex items-center gap-1.5 rounded-lg px-2.5 py-1.5 text-[11px] font-medium transition active:scale-95 ${
              isAdmin
                ? "border border-sindoor/40 bg-[#FDE8E8] text-[#7A2020]"
                : "border border-ink/20 bg-white text-ink"
            } ${isAdmin ? "" : "ml-auto"}`}
          >
            {isAdmin ? <LogOut size={13} /> : <ShieldCheck size={13} />}
            {isAdmin ? "लॉग आउट" : "Admin"}
          </button>
        )}
      </div>

      {/* legend */}
      <div className="flex shrink-0 flex-wrap gap-x-3 gap-y-1 border-b border-ink/10 bg-paper/70 px-3 py-1.5 text-[10px] text-ink-soft">
        <Legend swatch={<span className="h-2.5 w-2.5 rounded-sm border-2 border-sindoor" />} text={t("legend_line")} />
        <Legend swatch={<span className="font-bold text-sindoor">✗</span>} text={t("legend_issueless")} />
        <Legend swatch={<span className="h-2.5 w-2.5 rounded-sm bg-haldi-bg ring-1 ring-haldi" />} text={t("legend_unconfirmed")} />
        <Legend swatch={<span className="rounded-full bg-indigo px-1 text-[8px] font-bold text-cream">+N</span>} text={t("legend_hidden")} />
      </div>

      {/* canvas + panel */}
      <div className="relative min-h-0 flex-1 overflow-hidden">
        <FamilyTree ref={treeRef} data={tree} onSelect={setSelected} />
        {isAdmin ? (
          <AdminEditPanel
            person={selected}
            onClose={() => setSelected(null)}
            onChanged={() => setReloadKey((k) => k + 1)}
          />
        ) : (
          <PersonPanel
            person={selected}
            onClose={() => setSelected(null)}
            onRequestChange={() => {
              setRequestTarget(selected);
              setSelected(null);
            }}
            onFindRelation={() => navigate("/relation")}
          />
        )}
        {!isAdmin && (
          <RequestChangeForm person={requestTarget} onClose={() => setRequestTarget(null)} />
        )}
      </div>
    </div>
  );
}

function Legend({ swatch, text }: { swatch: React.ReactNode; text: string }) {
  return (
    <span className="inline-flex items-center gap-1.5">
      <span className="inline-flex h-3 items-center justify-center">{swatch}</span>
      {text}
    </span>
  );
}
