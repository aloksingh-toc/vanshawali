// Parses the DATA object from the original HTML editor and writes a typed
// TS module with the full 299-person tree. Run from the repo root:
//   node vanshawali-web/scripts/extract-tree.mjs
import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const here = path.dirname(fileURLToPath(import.meta.url));
const repoRoot = path.resolve(here, "..", "..");
const htmlPath = path.join(repoRoot, "vanshawali-editor_2.html");
const outPath = path.join(here, "..", "src", "data", "tree.ts");

const html = fs.readFileSync(htmlPath, "utf8");

const marker = html.indexOf("const DATA =");
if (marker === -1) throw new Error("DATA object not found in HTML");

const start = html.indexOf("{", marker);
let depth = 0;
let end = -1;
for (let j = start; j < html.length; j++) {
  const c = html[j];
  if (c === "{") depth++;
  else if (c === "}") {
    depth--;
    if (depth === 0) {
      end = j;
      break;
    }
  }
}
if (end === -1) throw new Error("Could not find end of DATA object");

const objectLiteral = html.slice(start, end + 1);
// The literal is a plain object (no function calls) — safe to evaluate.
const DATA = eval("(" + objectLiteral + ")");
const root = DATA.s1;

let count = 0;
const visit = (n) => {
  count++;
  (n.k || []).forEach(visit);
};
visit(root);

const banner = `// AUTO-GENERATED from vanshawali-editor_2.html (${count} people).
// Do not edit by hand — regenerate with: node scripts/extract-tree.mjs

export interface TreeNode {
  n: string;
  hl?: 1; // मुख्य वंश-रेखा (direct line)
  x?: 1; // लावल्द (issueless)
  q?: 1; // नाम अपुष्ट (unconfirmed)
  u?: 1; // पुष्टि बाकी (pending)
  note?: string;
  k?: TreeNode[];
}

`;

const body = `export const TREE: TreeNode = ${JSON.stringify(root, null, 2)};\n\nexport const TOTAL_PEOPLE = ${count};\n`;

fs.mkdirSync(path.dirname(outPath), { recursive: true });
fs.writeFileSync(outPath, banner + body, "utf8");
console.log(`Wrote ${count} people to ${path.relative(repoRoot, outPath)}`);
