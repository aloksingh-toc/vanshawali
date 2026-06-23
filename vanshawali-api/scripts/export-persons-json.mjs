// One-time export: parses the DATA object from the original HTML editor and
// writes it as plain nested JSON for the Spring Boot PersonSeeder to import.
// Run from the repo root: node vanshawali-api/scripts/export-persons-json.mjs
import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const here = path.dirname(fileURLToPath(import.meta.url));
const repoRoot = path.resolve(here, "..", "..");
const htmlPath = path.join(repoRoot, "vanshawali-editor_2.html");
const outPath = path.join(here, "..", "src", "main", "resources", "data", "tree-seed.json");

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
const DATA = eval("(" + objectLiteral + ")");
const root = DATA.s1;

let count = 0;
const visit = (n) => {
  count++;
  (n.k || []).forEach(visit);
};
visit(root);

fs.mkdirSync(path.dirname(outPath), { recursive: true });
fs.writeFileSync(outPath, JSON.stringify(root, null, 2), "utf8");
console.log(`Wrote ${count} people to ${path.relative(repoRoot, outPath)}`);
