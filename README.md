# Vanshawali (वंशावली)

A full-stack family-tree + community platform for a village family lineage — built as a persistent, multi-user evolution of a static D3.js tree editor.

> **Privacy note:** This is a public repository. The real family-tree data (names, relationships, dates) is intentionally excluded — it lives only in a local, gitignored seed file. Everything in this repo is code/scaffolding, not personal data.

## Features

- **Interactive family tree** — D3.js-powered, pannable/zoomable lineage visualization with highlighted direct-line, unconfirmed-name, and issueless flags
- **Relation finder** — given two people, computes their common ancestor and relationship label
- **Change request workflow** — visitors can suggest corrections/additions; an admin reviews and approves/rejects from a moderation inbox
- **Community features** — photo gallery with comments, announcements board, events calendar, crowdfunding ledger with a scoped Fund Manager role
- **Admin tools** — historical notes ("on this day"), user management, full CRUD on tree nodes
- **JWT-based auth** with role-based access (Admin / Fund Manager / Visitor)

## Tech Stack

| Layer | Stack |
|---|---|
| Backend | Java 21 · Spring Boot 3.5 · Spring Security (JWT) · Spring Data JPA |
| Database | PostgreSQL · Flyway migrations |
| Frontend | React 19 · TypeScript 5 · Vite · Tailwind CSS · D3.js |
| Routing | React Router 7 |

## Project Structure

```
vanshawali-api/    Spring Boot backend (REST API, JWT auth, PostgreSQL)
vanshawali-web/     React + Vite frontend (PWA-ready, mobile-first UI)
```

## Getting Started

### Backend

```bash
cd vanshawali-api
./mvnw spring-boot:run
```

Configure via environment variables (defaults are dev-only placeholders, **not safe for production**):

| Variable | Purpose |
|---|---|
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | PostgreSQL connection |
| `JWT_SECRET` | JWT signing secret (set a real 32+ byte secret) |
| `ADMIN_USERNAME`, `ADMIN_PASSWORD`, `ADMIN_DISPLAY_NAME` | Seeded admin account |

The API runs on `http://localhost:8080` by default.

### Frontend

```bash
cd vanshawali-web
npm install
npm run dev
```

The app runs on `http://localhost:5173` by default and expects the API at `http://localhost:8080`.

### Build

```bash
cd vanshawali-web
npm run build
```

## License

Private family project — not licensed for reuse.
