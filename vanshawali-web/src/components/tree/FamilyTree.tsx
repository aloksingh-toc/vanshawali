import { forwardRef, useEffect, useImperativeHandle, useRef } from "react";
import * as d3 from "d3";
import type { TreeNode } from "../../types/tree";

export interface SelectedPerson {
  node: TreeNode;
  generation: number;
}

export interface FamilyTreeHandle {
  expandAll: () => void;
  collapseAll: () => void;
  fit: () => void;
  search: (q: string) => number;
  exportPrint: () => void;
}

interface Props {
  data: TreeNode;
  onSelect?: (p: SelectedPerson) => void;
}

const BOX_H = 34;
const LEVEL_H = 120;
const UNIT = 12;
const INITIAL_COLLAPSE_DEPTH = 3;

// d3 hierarchy nodes are mutated in place by the classic collapse pattern
// (reassigning children/_children to null, stashing x0/y0/_w) in ways d3's
// static types don't model. We keep the internal node loosely typed and rely
// on the typed data payload (TreeNode) and public API at the boundaries.
// eslint-disable-next-line @typescript-eslint/no-explicit-any
type D3Node = any;

const FamilyTree = forwardRef<FamilyTreeHandle, Props>(function FamilyTree(
  { data, onSelect },
  ref
) {
  const hostRef = useRef<HTMLDivElement>(null);
  const api = useRef<FamilyTreeHandle | null>(null);

  useImperativeHandle(ref, () => ({
    expandAll: () => api.current?.expandAll(),
    collapseAll: () => api.current?.collapseAll(),
    fit: () => api.current?.fit(),
    search: (q: string) => api.current?.search(q) ?? 0,
    exportPrint: () => api.current?.exportPrint(),
  }));

  useEffect(() => {
    const host = hostRef.current;
    if (!host) return;

    const reduced = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
    const DUR = reduced ? 0 : 300;

    const svg = d3
      .select(host)
      .append("svg")
      .attr("class", "tree-svg")
      .attr("width", "100%")
      .attr("height", "100%");
    const g = svg.append("g");
    const gLink = g.append("g");
    const gNode = g.append("g");

    const zoom = d3
      .zoom<SVGSVGElement, unknown>()
      .scaleExtent([0.1, 3])
      .on("zoom", (e) => g.attr("transform", e.transform.toString()));
    svg.call(zoom as never).on("dblclick.zoom", null);

    const measure = document.createElement("canvas").getContext("2d")!;
    const textW = (s: string) => {
      measure.font =
        "600 13px 'Noto Sans Devanagari','Nirmala UI',sans-serif";
      return measure.measureText(s).width;
    };

    const tree = d3
      .tree<TreeNode>()
      .nodeSize([UNIT, LEVEL_H])
      .separation((a, b) => {
        const aw = (a.data as TreeNode & { _w?: number })._w ?? UNIT;
        const bw = (b.data as TreeNode & { _w?: number })._w ?? UNIT;
        return (aw + bw) / 2 / UNIT + (a.parent === b.parent ? 2.5 : 5);
      });

    const label = (d: D3Node) => d.data.n + (d.data.q ? " ?" : "");

    let idSeq = 0;
    const hitIds = new Set<number>();

    const root = d3.hierarchy(data, (d) => d.k) as unknown as D3Node;
    root.each((d: D3Node) => {
      const n = d as D3Node;
      n.id = ++idSeq;
      // Canvas measureText underestimates Devanagari width; scale up by 1.6×
      n._w = Math.max(80, textW(label(n)) * 1.6 + 36);
    });

    const collapse = (d: D3Node) => {
      if (d.children) {
        d._children = d.children as D3Node[];
        d._children.forEach(collapse);
        d.children = null;
      }
    };
    const expand = (d: D3Node) => {
      if (d._children) {
        d.children = d._children;
        d._children = null;
      }
      if (d.children) d.children.forEach(expand);
    };
    const hiddenCount = (d: D3Node) => {
      let c = 0;
      const rec = (n: D3Node) => {
        const kk = (n.children || n._children) as D3Node[] | null | undefined;
        if (!kk) return;
        kk.forEach((x) => {
          c++;
          rec(x);
        });
      };
      rec(d);
      return c;
    };

    root.each((d: D3Node) => {
      if ((d as D3Node).depth === INITIAL_COLLAPSE_DEPTH) collapse(d as D3Node);
    });
    root.x0 = 0;
    root.y0 = 0;

    const elbow = (s: { x: number; y: number }, t: { x: number; y: number }) => {
      const sy = s.y + BOX_H;
      const ty = t.y;
      const my = (sy + ty) / 2;
      return `M${s.x},${sy} L${s.x},${my} L${t.x},${my} L${t.x},${ty}`;
    };

    function update(source: D3Node) {
      tree(root);
      const nodes = root.descendants() as D3Node[];
      const links = root.links();

      const node = gNode
        .selectAll<SVGGElement, D3Node>("g.tree-node")
        .data(nodes, (d) => d.id);

      const nEnter = node
        .enter()
        .append("g")
        .attr(
          "class",
          (d) =>
            "tree-node" +
            (d.data.q ? " q" : "") +
            (d.data.x ? " x" : "") +
            (d.data.u ? " u" : "") +
            (d.data.hl ? " hl" : "")
        )
        .attr("transform", `translate(${source.x0},${source.y0})`)
        .attr("opacity", 0)
        .style("cursor", "pointer")
        .on("click", (ev: MouseEvent, d) => {
          ev.stopPropagation();
          if (d.children) {
            d._children = d.children as D3Node[];
            d.children = null;
            update(d);
          } else if (d._children) {
            d.children = d._children;
            d._children = null;
            update(d);
          }
          onSelect?.({ node: d.data, generation: d.depth + 1 });
        });

      nEnter
        .append("rect")
        .attr("class", "box")
        .attr("x", (d) => -d._w / 2)
        .attr("y", 0)
        .attr("width", (d) => d._w)
        .attr("height", BOX_H)
        .attr("rx", 7);

      nEnter
        .append("text")
        .attr("class", "name")
        .attr("text-anchor", "middle")
        .attr("x", 0)
        .attr("y", BOX_H / 2 + 4.5)
        .each(function (d) {
          const tsel = d3.select(this);
          tsel.append("tspan").text(d.data.n);
          if (d.data.q) tsel.append("tspan").attr("class", "q").text(" ?");
        });

      nEnter.each(function (d) {
        const gn = d3.select(this);
        let y = BOX_H + 12;
        if (d.data.x) {
          gn.append("text")
            .attr("class", "sub lawald")
            .attr("text-anchor", "middle")
            .attr("y", y)
            .text("✗ लावल्द");
          y += 11;
        }
        if (d.data.note) {
          gn.append("text")
            .attr("class", "sub")
            .attr("text-anchor", "middle")
            .attr("y", y)
            .text(d.data.note);
        }
      });

      const badge = nEnter.append("g").attr("class", "tree-badge");
      badge
        .append("rect")
        .attr("x", -16)
        .attr("y", BOX_H - 8)
        .attr("width", 32)
        .attr("height", 15)
        .attr("rx", 7.5);
      badge
        .append("text")
        .attr("text-anchor", "middle")
        .attr("y", BOX_H + 3.5);

      const nMerge = nEnter.merge(node);
      nMerge
        .transition()
        .duration(DUR)
        .attr("transform", (d) => `translate(${d.x},${d.y})`)
        .attr("opacity", 1);
      nMerge
        .select<SVGGElement>(".tree-badge")
        .style("display", (d) => (d._children ? null : "none"))
        .select("text")
        .text((d) => (d._children ? "+" + hiddenCount(d) : ""));
      nMerge
        .select<SVGRectElement>("rect.box")
        .classed("hit", (d) => hitIds.has(d.id));

      node
        .exit<D3Node>()
        .transition()
        .duration(DUR)
        .attr("transform", `translate(${source.x},${source.y})`)
        .attr("opacity", 0)
        .remove();

      const link = gLink
        .selectAll<SVGPathElement, d3.HierarchyLink<TreeNode>>("path.tree-link")
        .data(links, (d: d3.HierarchyLink<TreeNode>) => (d.target as D3Node).id);

      link
        .enter()
        .append("path")
        .attr(
          "class",
          (d: d3.HierarchyLink<TreeNode>) =>
            "tree-link" +
            (d.source.data.hl && d.target.data.hl ? " hl" : "")
        )
        .attr("d", () => {
          const o = { x: source.x0, y: source.y0 };
          return elbow(o, { x: o.x, y: o.y + BOX_H });
        })
        .merge(link)
        .transition()
        .duration(DUR)
        .attr("d", (d: d3.HierarchyLink<TreeNode>) =>
          elbow(d.source as D3Node, d.target as D3Node)
        );

      link
        .exit<d3.HierarchyLink<TreeNode>>()
        .transition()
        .duration(DUR)
        .attr("d", () => {
          const o = { x: source.x, y: source.y };
          return elbow(o, { x: o.x, y: o.y + BOX_H });
        })
        .remove();

      nodes.forEach((d) => {
        d.x0 = d.x;
        d.y0 = d.y;
      });
    }

    const stageSize = (): [number, number] => [
      host.clientWidth,
      host.clientHeight,
    ];

    function fit() {
      const bbox = (g.node() as SVGGElement).getBBox();
      if (!bbox.width || !bbox.height) return;
      const [W, H] = stageSize();
      const k = Math.min(
        (W - 32) / bbox.width,
        (H - 48) / bbox.height,
        1.3
      );
      const tx = (W - bbox.width * k) / 2 - bbox.x * k;
      const ty = Math.max((H - bbox.height * k) / 2, 20) - bbox.y * k;
      svg
        .transition()
        .duration(reduced ? 0 : 450)
        .call(
          zoom.transform as never,
          d3.zoomIdentity.translate(tx, ty).scale(k)
        );
    }

    function panTo(d: D3Node) {
      const [W, H] = stageSize();
      const t = d3.zoomTransform(svg.node() as SVGSVGElement);
      const k = Math.max(t.k, 0.8);
      svg
        .transition()
        .duration(reduced ? 0 : 450)
        .call(
          zoom.transform as never,
          d3.zoomIdentity.translate(W / 2 - d.x * k, H / 3 - d.y * k).scale(k)
        );
    }

    function exportPrint() {
      gNode
        .selectAll<SVGGElement, D3Node>("g.tree-node")
        .interrupt()
        .attr("transform", (d) => `translate(${d.x},${d.y})`)
        .attr("opacity", 1);
      gLink
        .selectAll<SVGPathElement, d3.HierarchyLink<TreeNode>>("path.tree-link")
        .interrupt()
        .attr("d", (d: d3.HierarchyLink<TreeNode>) =>
          elbow(d.source as D3Node, d.target as D3Node)
        );

      const bbox = (g.node() as SVGGElement).getBBox();
      if (!bbox.width || !bbox.height) return;
      const pad = 24;
      const w = bbox.width + pad * 2;
      const h = bbox.height + pad * 2;

      const clone = g.node()!.cloneNode(true) as SVGGElement;
      clone.setAttribute("transform", `translate(${pad - bbox.x},${pad - bbox.y})`);

      const printSvg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
      printSvg.setAttribute("class", "tree-svg");
      printSvg.setAttribute("viewBox", `0 0 ${w} ${h}`);
      printSvg.setAttribute("width", String(w));
      printSvg.setAttribute("height", String(h));
      printSvg.appendChild(clone);

      let container = document.getElementById("tree-print-root");
      if (!container) {
        container = document.createElement("div");
        container.id = "tree-print-root";
        document.body.appendChild(container);
      }
      container.innerHTML = "";
      container.appendChild(printSvg);

      const cleanup = () => {
        container?.remove();
        window.removeEventListener("afterprint", cleanup);
      };
      window.addEventListener("afterprint", cleanup);
      window.print();
    }

    function search(q: string): number {
      hitIds.clear();
      const query = q.trim();
      if (query.length < 1) {
        update(root);
        return 0;
      }
      const matches: D3Node[] = [];
      root.each((d: D3Node) => {
        if ((d as D3Node).data.n.includes(query)) matches.push(d as D3Node);
      });
      matches.slice(0, 60).forEach((m) => {
        hitIds.add(m.id);
        let a = m.parent as D3Node | null;
        while (a) {
          if (a._children) {
            a.children = a._children;
            a._children = null;
          }
          a = a.parent as D3Node | null;
        }
      });
      update(root);
      if (matches[0]) setTimeout(() => panTo(matches[0]), DUR + 40);
      return matches.length;
    }

    api.current = {
      expandAll: () => {
        expand(root);
        update(root);
        setTimeout(fit, DUR + 60);
      },
      collapseAll: () => {
        root.children?.forEach(collapse);
        update(root);
        setTimeout(fit, DUR + 60);
      },
      fit,
      search,
      exportPrint,
    };

    update(root);
    const fitTimer = setTimeout(() => {
      if (host.clientWidth > 0 && host.clientHeight > 0) fit();
      else setTimeout(fit, 400);
    }, DUR + 120);
    const onResize = () => fit();
    window.addEventListener("resize", onResize);

    return () => {
      clearTimeout(fitTimer);
      window.removeEventListener("resize", onResize);
      svg.remove();
      api.current = null;
    };
  }, [data, onSelect]);

  return <div ref={hostRef} className="h-full w-full" />;
});

export default FamilyTree;
