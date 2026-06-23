# वंशावली Application — Full Build Plan
**Family Tree Platform for Gram Ibrahimabad, District Ballia**

---

## 1. Vision

A full-stack web application that hosts the family vansh-vriksha (lineage tree) for the village of Ibrahimabad, Ballia. The current state is a single self-contained HTML file with a D3.js tree, 299 names, and a local "edit → download updated HTML" workflow.

The goal is to evolve this into a real, persistent, multi-user web application:

- **One administrator** has full authority — every addition, correction, or deletion to the tree is made or approved by the admin.
- **Family members (the public/family audience)** can browse the tree freely and **submit change requests** — e.g. "this name is wrong," "add my son," "mark this person as issueless," "confirm this unconfirmed name."
- The admin reviews a request queue and approves/rejects/edits before anything goes live.
- All data lives in PostgreSQL instead of a JS object baked into an HTML file, so it's persistent, queryable, and safe from accidental loss.

---

## 2. Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 3.x, Spring Security (JWT) |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Frontend | React 18 + TypeScript 5 |
| Tree visualization | D3.js (ported from existing editor into a React component) |
| Styling | Tailwind CSS |
| Hosting (later) | Free-tier friendly — Render/Railway (backend+DB), Vercel/Netlify (frontend) |

This matches your existing skill set (Java 21 + Spring Boot + React + PostgreSQL), so no new tech to learn from scratch.

---

## 3. User Roles

| Role | Capabilities |
|---|---|
| **Admin** | Full CRUD on every person/node. Reviews and approves/rejects all change requests, gallery uploads, comments, and announcements. Manages flags (`hl`, `x`, `q`), notes, photos. Creates/manages all other accounts, including Fund Managers. |
| **कोषाध्यक्ष / Fund Manager (delegated)** | A trusted family member given a login by the admin, scoped **only** to the crowdfunding module — can add/edit/delete `fund_entries` and view the ledger/summary. No access to tree editing, gallery moderation, or any other admin function. |
| **Visitor / Family member** | Views the tree, gallery, events, and boards (read-only). Searches by name. Can submit a **change request** (name + relation, no login required). Cannot edit anything directly. |

There is no concept of "multiple tree editors" — tree moderation remains single-admin. The Fund Manager role is a narrow, purpose-built exception for delegating contribution bookkeeping.

---

## 4. Shared / Reusable Backend Modules

Several v1 features follow the **same two patterns** repeatedly. Rather than building each one from scratch, both should be built once as shared modules and reused everywhere they apply.

### 4.1 Moderation Pattern

`change_requests`, `gallery_posts`, `gallery_comments`, and `announcements` (Section 10) all need the same submit → pending → admin review → approve/reject flow, with the same shape: `status ENUM(PENDING/APPROVED/REJECTED)`, `admin_notes`, `created_at`/`reviewed_at`, submitter name/contact.

- Backend: a shared `@Embeddable ModerationStatus` (status, admin_notes, reviewed_at) embedded into each entity, plus a generic `ModerationService<T>` base providing `approve(id)` / `reject(id, reason)` / `listPending()`, extended per entity.
- Frontend: one reusable `<ModerationInbox<T>>` component, configured per content type (photos, comments, announcements, change requests) instead of four separate admin inbox screens.

### 4.2 File Upload Service

Person photos, gallery images, fund receipts, and document-archive files all need the same thing: "accept a file → store it → return a URL."

- Backend: one `POST /api/uploads` endpoint (auth required for admin-only contexts like the document archive; open for public contexts like gallery/photo requests, with type-based folder routing) backed by a single `FileStorageService` interface — Cloudinary/S3 in production, local disk in development.
- Frontend: one reusable `<FileUploadField>` component used by `AddFundEntryForm`, `UploadPhotoForm`, `AdminEditPanel` (person photo), and `DocumentArchive`.

Building these two as shared modules first (Phase 1-2) means every later feature plugs into existing infrastructure instead of reinventing it.

---

## 5. Core Data Model (mapped from the existing HTML editor)

The current `DATA` object in `vanshawali-editor_2.html` uses this shape per node:

```js
{ n: 'नाम', hl: 1, x: 1, q: 1, note: '...', k: [ ...children... ] }
```

Where:
- `n` — name (Devanagari)
- `hl` — 1 if part of "मुख्य वंश-रेखा" (your direct line) — shown highlighted/red
- `x` — 1 if "लावल्द" (no descendants / issueless)
- `q` — 1 if "नाम अपुष्ट" (unconfirmed name) — shown yellow with `?`
- `note` — free-text annotation (alias, etc.)
- `k` — array of children

### PostgreSQL Schema

**`persons`** — every individual in the tree
| Column | Type | Notes |
|---|---|---|
| id | BIGSERIAL PK | |
| parent_id | BIGINT, FK → persons.id, nullable | NULL for root ancestor |
| name | TEXT | Devanagari name |
| alias_note | TEXT, nullable | e.g. "उर्फ़ नाम" |
| is_direct_line | BOOLEAN default false | maps to `hl` |
| is_issueless | BOOLEAN default false | maps to `x` (लावल्द) |
| is_unconfirmed | BOOLEAN default false | maps to `q` |
| sibling_order | INT | preserves birth-order display |
| generation | INT | computed/cached depth from root |
| birth_date | DATE, nullable | birthdays + "On This Day" |
| death_date | DATE, nullable | बरसी (death anniversary) reminders |
| date_is_approximate | BOOLEAN default false | true if only the year/era is known (older generations) |
| photo_url | TEXT, nullable | profile photo (via Section 4.2) |
| sheet_name | TEXT, nullable | only set on root nodes — see Section 8 (multi-sheet, v2) |
| created_at, updated_at | TIMESTAMP | |

**`change_requests`** — moderation queue (Section 4.1 pattern)
| Column | Type | Notes |
|---|---|---|
| id | BIGSERIAL PK | |
| target_person_id | BIGINT, FK → persons.id, nullable | NULL when proposing a brand-new person |
| request_type | ENUM | `RENAME`, `ADD_CHILD`, `MARK_ISSUELESS`, `CONFIRM_NAME`, `ADD_NOTE`, `DELETE`, `OTHER` |
| proposed_data | JSONB | flexible payload (new name, note text, parent id for new child, etc.) |
| requester_name | TEXT | who submitted it |
| requester_contact | TEXT, nullable | phone/email, optional |
| status | ENUM | `PENDING`, `APPROVED`, `REJECTED` |
| admin_notes | TEXT, nullable | why approved/rejected |
| created_at, reviewed_at | TIMESTAMP | |

**`app_users`**
| Column | Type | Notes |
|---|---|---|
| id | BIGSERIAL PK | |
| username | TEXT unique | |
| password_hash | TEXT | BCrypt |
| display_name | TEXT | |
| role | ENUM | `ADMIN`, `FUND_MANAGER` |
| created_by | BIGINT, FK → app_users.id, nullable | which admin created this account |
| created_at | TIMESTAMP | |

> Only `ADMIN` accounts can create other `app_users`. A `FUND_MANAGER` account's JWT carries `role: FUND_MANAGER`, restricting access to only `/api/fund/*` endpoints (Section 10.6).

**`audit_log`** (optional, Phase 2+)
| Column | Type | Notes |
|---|---|---|
| id | BIGSERIAL PK | |
| person_id | BIGINT | |
| action | TEXT | what changed |
| changed_by | TEXT | "admin" or request id |
| diff | JSONB | before/after |
| created_at | TIMESTAMP | |

> **Migration**: Phase 1 includes a one-time script that parses the existing `DATA` object from `vanshawali-editor_2.html` and inserts all 299 people into `persons`, preserving hierarchy via `parent_id` and sibling order.

---

## 6. Core Backend API (Spring Boot)

### Public endpoints (no auth)
- `GET /api/tree` — full tree as nested JSON (or `GET /api/persons/{id}/subtree`)
- `GET /api/persons/{id}` — single person detail
- `GET /api/search?q=...` — search by name (Devanagari + transliteration-aware if possible)
- `POST /api/requests` — submit a change request

### Admin endpoints (JWT-protected)
- `POST /api/auth/login` — admin login
- `POST /api/persons` — add new person directly
- `PUT /api/persons/{id}` — edit name/flags/note directly
- `DELETE /api/persons/{id}` — remove a person (with cascade rules for children)
- `PATCH /api/persons/{id}/flags` — toggle `hl` / `x` / `q`
- `GET /api/requests?status=PENDING` — view request queue (Moderation pattern, Section 4.1)
- `POST /api/requests/{id}/approve` / `POST /api/requests/{id}/reject`

---

## 7. Core Frontend (React + TypeScript)

Reuse the existing D3 rendering logic (`elbow`, `update`, `collapse`, `expandAll`, `fit`, `panTo`, etc.) ported into a `<FamilyTree />` component driven by API data instead of an inline `DATA` constant.

### Pages / Components
- **`TreeView`** — main D3 canvas: pan/zoom, expand/collapse, search highlighting (ported from existing editor)
- **`SearchBar`** — search by name, jumps/pans to match
- **`PersonPanel`** — slide-up detail panel on tap: name, note, flags, photo, "request a change" button (for visitors)
- **`RequestChangeForm`** — modal: pick request type, enter proposed value + your name/relation
- **`AdminLogin`** — simple login form
- **`AdminEditPanel`** — same as `PersonPanel` but with direct edit/add/delete controls (only visible when authenticated)
- **`AdminRequestInbox`** — `<ModerationInbox<ChangeRequest>>` instance (Section 4.1)

### Legend (carried over from existing editor)
- Red highlight = मुख्य वंश-रेखा (direct line)
- ✗ = लावल्द (issueless)
- Yellow + ? = नाम अपुष्ट (unconfirmed)
- +N = hidden descendants (tap to expand)

---

## 8. Decisions (Resolved)

- **Change requests**: every visitor submission includes a lightweight "who are you" form — name + relation to a person in the tree. Not fully anonymous.
- **Photos**: person photos (`photo_url` on `persons`) are included from **v1**.
- **PDF / print export**: included — admin can export the full tree (or a subtree) as a PDF for physical family records.
- **Multi-sheet support**: the schema (`parent_id`-based hierarchy + `sheet_name`) naturally supports multiple root nodes/branches for the future. The column exists from v1 (zero cost), but no UI/logic is built around it until there's an actual second sheet to add — see Section 11 (v2).

---

## 9. Public Photo Gallery (गांव की तस्वीरें)

A public gallery where any visitor can upload photos of the village, festivals, family functions, etc. As with tree change requests, **the admin has sole authority** over what appears publicly — every upload goes into the moderation queue (Section 4.1) before it's visible.

**`gallery_posts`**
| Column | Type | Notes |
|---|---|---|
| id | BIGSERIAL PK | |
| image_url | TEXT | via Section 4.2 upload service |
| caption | TEXT, nullable | |
| category | ENUM | `FESTIVAL`, `FUNCTION`, `VILLAGE`, `FAMILY`, `OTHER` |
| uploader_name | TEXT | |
| uploader_contact | TEXT, nullable | |
| like_count | INT default 0 | simple reaction counter, not moderated |
| status | ENUM | `PENDING`, `APPROVED`, `REJECTED` |
| admin_notes | TEXT, nullable | |
| created_at, reviewed_at | TIMESTAMP | |

**`gallery_comments`** — also moderated (Section 4.1)
| Column | Type | Notes |
|---|---|---|
| id | BIGSERIAL PK | |
| gallery_post_id | BIGINT, FK → gallery_posts.id | |
| commenter_name | TEXT | |
| comment_text | TEXT | |
| status | ENUM | `PENDING`, `APPROVED`, `REJECTED` |
| created_at | TIMESTAMP | |

### API Endpoints

**Public**
- `GET /api/gallery` — list approved posts (filterable by `category`)
- `POST /api/gallery` — upload photo + caption + category + uploader name (defaults to `PENDING`)
- `POST /api/gallery/{id}/like` — increment `like_count` (no moderation)
- `POST /api/gallery/{id}/comments` — submit a comment (defaults to `PENDING`)

**Admin (JWT-protected)**
- `GET /api/gallery?status=PENDING` / `/api/gallery/comments?status=PENDING` — moderation queues
- `POST /api/gallery/{id}/approve` · `/reject` (and same for comments)
- `DELETE /api/gallery/{id}` — remove a previously approved post

### Frontend
- **`GalleryView`** — public photo grid, filterable by category, lightbox view with like button + comment thread
- **`UploadPhotoForm`** — uses `<FileUploadField>` (Section 4.2)
- **`AdminGalleryInbox`** — `<ModerationInbox>` instance covering both photos and comments
- Navigation: top-level tab alongside the tree — "वंशवृक्ष" (Tree) | "गांव की तस्वीरें" (Village Gallery)

---

## 10. v1 Additional Features

### 10.1 बरसी / Anniversary Reminders

Using `birth_date` and `death_date` on `persons`:
- **`GET /api/anniversaries/upcoming`** — people with a birthday or बरसी in the next N days (matching month+day regardless of year)
- Admin dashboard widget: "इस सप्ताह की तिथियाँ"
- Future: WhatsApp/email reminders (v2)

### 10.2 "How Are We Related?" Calculator + Generation Gap

Given two person IDs, find their relationship by walking up the tree:

1. Build the **ancestor path** (root → person) for both people.
2. Find the **Lowest Common Ancestor (LCA)** — the deepest node present in both paths.
3. **Generation difference** = `|depth(person1) - depth(person2)|` relative to the LCA.
4. Translate the path lengths from the LCA into a Hindi kinship description:
   - Same generation, LCA = grandparent → "cousin" (भाई/बहन, चाचा/मामा के बच्चे, etc.)
   - One generation apart → uncle/nephew-style relation (चाचा, भतीजा, etc.)
   - Direct ancestor/descendant → सीधी वंश-रेखा (direct line), with generation count ("4 पीढ़ी ऊपर")

API: **`GET /api/relation?from={id}&to={id}`** →
```json
{
  "commonAncestor": "नाम",
  "generationGap": 2,
  "relationLabel": "आपके पिता के चाचा के पुत्र (दूर के भाई)",
  "pathFrom": ["व्यक्ति A", "...", "नाम"],
  "pathTo": ["व्यक्ति B", "...", "नाम"]
}
```

Frontend: **`RelationFinder`** — pick two people via search, shows the relation description, generation gap, and highlights both paths on the tree simultaneously. This is the standout "algorithm" feature for your portfolio — a real graph/tree LCA problem with a practical UI.

### 10.3 "On This Day" + Historical Notes (आज का इतिहास)

**`historical_notes`**
| Column | Type | Notes |
|---|---|---|
| id | BIGSERIAL PK | |
| note_month | INT (1-12) | recurring date, year-independent |
| note_day | INT (1-31) | |
| title | TEXT | |
| description | TEXT, nullable | |
| related_person_id | BIGINT, FK → persons.id, nullable | |
| photo_url | TEXT, nullable | via Section 4.2 |
| created_at | TIMESTAMP | admin-only creation |

- **`GET /api/on-this-day`** combines birthdays (`persons.birth_date`), बरसी (`persons.death_date`), and any `historical_notes` matching today's month/day
- Frontend: **`OnThisDay`** widget on the home page, e.g. "आज, वर्ष XXXX में... जन्म हुआ था व्यक्ति का"

### 10.4 Community Events Calendar

**`community_events`**
| Column | Type | Notes |
|---|---|---|
| id | BIGSERIAL PK | |
| title | TEXT | e.g. "होली मिलन समारोह" or "गाँव की सड़क मरम्मत" |
| description | TEXT, nullable | |
| event_date | DATE | |
| event_type | ENUM | `WEDDING`, `FESTIVAL`, `PUJA`, `REUNION`, `VILLAGE_PROJECT`, `OTHER` |
| location | TEXT, nullable | |
| created_at | TIMESTAMP | |

- Admin-only create/edit/delete (`POST/PUT/DELETE /api/events`)
- Everyone can view: `GET /api/events` (filterable by type)
- Frontend: **`EventsCalendar`** — month/list view covering both family functions and village-wide causes

### 10.5 Achievements / Announcements Board

**`announcements`** — moderated (Section 4.1)
| Column | Type | Notes |
|---|---|---|
| id | BIGSERIAL PK | |
| person_id | BIGINT, FK → persons.id, nullable | who the announcement is about |
| title | TEXT | e.g. "नई नौकरी — सॉफ्टवेयर इंजीनियर" |
| description | TEXT, nullable | |
| announcement_type | ENUM | `JOB`, `DEGREE`, `MARRIAGE`, `BIRTH`, `OTHER` |
| submitter_name | TEXT | |
| submitter_contact | TEXT, nullable | |
| status | ENUM | `PENDING`, `APPROVED`, `REJECTED` |
| created_at, reviewed_at | TIMESTAMP | |

- `POST /api/announcements` (public, defaults `PENDING`), `GET /api/announcements` (approved only, public), admin approve/reject
- Frontend: **`AnnouncementsBoard`** (public feed) + **`AnnouncementsForm`** + `<ModerationInbox>` instance

### 10.6 Local Crowdfunding for Public Causes (सार्वजनिक चंदा)

A transparent ledger for **village-wide and family contributions alike** — temple renovation, road repair, water pump installation, weddings, deaths, festivals, school building funds, etc.

**`fund_entries`**
| Column | Type | Notes |
|---|---|---|
| id | BIGSERIAL PK | |
| name | TEXT | contributor's (or payee's, for expenses) name |
| amount | NUMERIC(10,2) | |
| date | DATE | |
| mode | ENUM | `CASH`, `UPI`, `BANK_TRANSFER`, `CHEQUE`, `OTHER` |
| note | TEXT, nullable | e.g. "मंदिर मरम्मत हेतु", "सड़क मरम्मत चंदा" |
| entry_type | ENUM, default `CONTRIBUTION` | `CONTRIBUTION` (money in) or `EXPENSE` (money out) |
| related_event_id | BIGINT, FK → community_events.id, nullable | optional link to the cause/event |
| receipt_url | TEXT, nullable | via Section 4.2, mainly for `EXPENSE` entries |
| created_at | TIMESTAMP | |

### API Endpoints

**Admin or Fund Manager (JWT-protected, roles `ADMIN`/`FUND_MANAGER`)**
- `GET /api/fund/entries` — filterable by `entry_type`, `related_event_id`, date range
- `POST /api/fund/entries` · `PUT /api/fund/entries/{id}` · `DELETE /api/fund/entries/{id}`
- `GET /api/fund/summary` — total contributions, total expenses, balance (overall and per-event)

**Admin-only**
- `POST /api/users` / `GET /api/users` / `DELETE /api/users/{id}` — manage Fund Manager accounts

**Public**
- `GET /api/fund/summary` — read-only, so every villager can see the balance is transparent

### Frontend
- **`FundLedger`** — table: Name | Amount | Date | Mode | Note | Receipt (📎), running balance, filterable
- **`FundSummaryCard`** — "कुल योगदान: ₹X · कुल खर्च: ₹Y · शेष: ₹Z"
- **`AddFundEntryForm`** — uses `<FileUploadField>` (Section 4.2) for optional receipt photo

### 10.7 Hindi/English UI Toggle

- `react-i18next` with `en.json` / `hi.json` for **UI chrome only** — buttons, labels, navigation, form text
- **Family data itself (names, notes) stays in Devanagari regardless of UI language**
- Toggle persisted in `localStorage`, available in the header on every page

---

## 11. v2 / Future Roadmap

These are deliberately **deferred** from v1 — either because they need real usage data first, add infrastructure cost before it's justified, or are genuinely separate products built on top of the same foundation.

- **Audio/Video Oral History (`media_recordings`)** — admin-only uploads of elders' stories, attached to person nodes. Deferred because audio/video files are far heavier than images; better to assess gallery storage costs in production first before adding bigger media.
- **PWA Support** — manifest + service worker + offline tree caching. Genuinely useful for Ballia's connectivity, but is polish on top of a working v1, not core to it.
- **Multi-Sheet Tree UI** — `sheet_name` exists in the schema from v1 at zero cost, but tab/navigation UI for multiple tree branches is only built once a second sheet actually exists.
- **Document Archive (`documents`)** — private, admin-only storage for land records/certificates, linkable to person nodes. Deferred because it's private (no benefit to visitors/family members) and tangential to the core genealogy/community focus — a personal Drive folder serves the same purpose for now.
- **NRI / Migrant Family Connect** — premium tier for family members living outside the village: live event feeds, priority notifications, remote contributions to the fund via UPI.
- **Village Directory + Local Service Marketplace** — searchable directory of shops/tradespeople, with paid premium listings.
- **"Vanshawali-as-a-Service"** — white-labeling the whole platform (tree + gallery + events + crowdfunding) for other villages, as a subscription product — the long-term monetization play, structurally similar to the Vidyasetu model.

---

## 12. Build Phases (v1)

| Phase | Scope |
|---|---|
| **Phase 1 — Foundation** | PostgreSQL schema (`persons`, `change_requests`, `app_users`, `audit_log`), Spring Boot skeleton, **shared Moderation pattern + File Upload service** (Section 4), migration script importing all 299 people from the existing HTML's `DATA` object |
| **Phase 2 — Core API & Auth** | `/api/tree`, `/api/persons/{id}`, `/api/search`, admin JWT auth |
| **Phase 3 — Frontend Tree + i18n** | Port D3 tree to React `TreeView`, search bar, person detail panel with photo, Hindi/English toggle from the start |
| **Phase 4 — Admin Direct Edit** | `AdminEditPanel` → direct CRUD via `/api/persons`, including birth/death dates and photo upload (via shared upload service) |
| **Phase 5 — Change Requests** | `RequestChangeForm` (name + relation field), `/api/requests`, `AdminRequestInbox` (`<ModerationInbox>`) |
| **Phase 6 — Public Gallery + Comments** | `gallery_posts`, `gallery_comments`, like counter, `GalleryView`, `UploadPhotoForm`, `AdminGalleryInbox` |
| **Phase 7 — Relationship & History Tools** | `RelationFinder` (LCA + generation-gap calculator), बरसी/birthday reminders, `OnThisDay` widget + `historical_notes` |
| **Phase 8 — Community Boards** | `community_events` + `EventsCalendar`, `announcements` board (`<ModerationInbox>`), `fund_entries` crowdfunding ledger + `FundLedger`/`FundSummaryCard`/`AddFundEntryForm` |
| **Phase 9 — Export & Deploy** | PDF/print export of tree, deploy backend+DB+frontend |

**Phase 10+ (v2)** — see Section 11: oral history media, PWA, multi-sheet UI, and future product directions.

---

*This plan is derived from analysis of the existing `vanshawali-editor_2.html` (299 names, D3-based tree, edit mode, HTML export). v1 scope covers: PostgreSQL persistence, Spring Boot + React rebuild, single-admin authority with a delegated Fund Manager role, shared moderation and file-upload infrastructure, a visitor change-request system, public photo gallery with comments, relationship/generation-gap calculator, बरसी and birthday reminders with "On This Day" history, a community events calendar (family + village-wide causes), achievements board, local crowdfunding ledger for public causes, and a Hindi/English UI toggle. v2 covers oral history media, PWA support, multi-sheet tree UI, a private document archive, and future community/monetization products.*
