# वंशावली — Final Project Scope
**Family Tree & Community Platform for Gram Ibrahimabad, District Ballia**

---

## Overview

वंशावली is a full-stack web application that turns a 299-person family tree (currently a single HTML/D3 file) into a persistent, multi-user platform — combining a genealogy explorer with community tools for the village: a public gallery, events calendar, achievements board, and a transparent crowdfunding ledger.

**Tech Stack**: Java 21 + Spring Boot 3 (backend, REST + JWT) · PostgreSQL (database) · React 18 + TypeScript 5 (frontend) · D3.js (tree visualization) · Tailwind CSS

**Authority Model**: Single admin holds full editorial control over the tree and all moderated content. Visitors can browse everything and submit requests/uploads/posts, which go into an admin review queue. A delegated **Fund Manager** role exists solely for the crowdfunding ledger.

---

## Modules to Build

| # | Module | Purpose |
|---|---|---|
| 1 | **Core Tree Engine** | Persons table, hierarchy, D3 tree view, search, flags (direct line / issueless / unconfirmed) |
| 2 | **Auth & Roles** | Admin login, delegated Fund Manager accounts, JWT-based access control |
| 3 | **Shared Moderation Service** | Reusable approve/reject queue used by change requests, gallery posts, comments, and announcements |
| 4 | **Shared File Upload Service** | Reusable file storage for photos, receipts, documents (Cloudinary/S3 or local disk) |
| 5 | **Change Request System** | Visitor-submitted corrections/additions to the tree, admin-reviewed |
| 6 | **Public Photo Gallery** | Village/festival/family photo uploads with likes and comments, admin-moderated |
| 7 | **Relationship Finder** | "How are we related?" — LCA-based relation + generation-gap calculator |
| 8 | **Anniversary & "On This Day"** | बरसी/birthday reminders + historical notes feed |
| 9 | **Community Events Calendar** | Weddings, festivals, पूजा, reunions, and village-wide projects |
| 10 | **Achievements / Announcements Board** | Jobs, degrees, marriages, births — admin-moderated public feed |
| 11 | **Crowdfunding Ledger (सार्वजनिक चंदा)** | Transparent contribution/expense tracking with receipts, for family & village causes |
| 12 | **i18n (Hindi/English Toggle)** | UI language switch; family data always stays in Devanagari |

---

## Feature List — v1

### Tree & Search
- Interactive pan/zoom family tree (ported from existing D3 editor)
- Search by name with highlight/jump
- Person detail panel: name, alias/note, flags (हाइलाइट / लावल्द / अपुष्ट), photo, birth/death dates
- Legend: direct line (red), issueless (✗), unconfirmed (yellow + ?), collapsed branches (+N)

### Editing & Moderation
- Admin: direct add/edit/delete of any person, toggle flags, set photo/dates
- Visitors: submit change requests (rename, add child, mark issueless, confirm name, add note) with name + relation
- Admin review queue: approve / reject / edit-before-approve

### Public Photo Gallery
- Upload photos (festival / function / village / family categories)
- Like button + moderated comments
- Admin moderation queue for photos and comments

### Relationship Finder
- Pick any two people → shortest-path relation description in Hindi
- Generation-gap count
- Highlights both ancestry paths on the tree

### Dates, Reminders & History
- Birthday and बरसी (death anniversary) upcoming-dates widget
- "On This Day" — combines birthdays, बरसी, and admin-added historical notes for today's date

### Community Boards
- Events calendar (weddings, festivals, पूजा, reunions, village projects) — admin-posted, all view
- Achievements/announcements board (jobs, degrees, marriages, births) — visitor-submitted, admin-moderated

### Crowdfunding Ledger (सार्वजनिक चंदा)
- Log contributions and expenses: name, amount, date, mode (cash/UPI/bank/cheque), note
- Optional receipt photo upload for expenses
- Running balance + per-event totals, publicly visible summary
- Delegated Fund Manager login for day-to-day entry

### Language
- Hindi/English toggle for all UI chrome (navigation, buttons, forms) — family data stays in Devanagari

---

## v2 / Future Roadmap (not in current build)

- Audio/video oral history recordings attached to person profiles
- PWA support (offline tree browsing, installable app)
- Multi-sheet tree support (additional family branches)
- Document archive (private admin storage for land records/certificates, linkable to people)
- NRI/migrant family connect (remote contributions, event feeds, priority notifications)
- Village directory & local service marketplace
- White-label "Vanshawali-as-a-Service" for other villages (long-term monetization)

---

## Build Order (9 Phases)

1. **Foundation** — full schema, shared moderation + upload services, migration of existing 299 people
2. **Core API & Auth** — tree/search endpoints, admin JWT
3. **Frontend Tree + i18n** — React tree view, search, language toggle
4. **Admin Direct Edit** — full CRUD on persons, photos, dates
5. **Change Requests** — visitor submission flow + admin inbox
6. **Public Gallery + Comments** — uploads, likes, moderation
7. **Relationship & History Tools** — relation finder, anniversaries, "On This Day"
8. **Community Boards** — events calendar, announcements, crowdfunding ledger
9. **Archive, Export & Deploy** — PDF export, deployment

---

*Full technical details — database schemas, API endpoints, and component breakdowns for every module — are documented in `VANSHAWALI_APP_PLAN.md`. This document is the high-level scope summary.*
