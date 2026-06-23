CREATE TABLE community_events (
    id              BIGSERIAL PRIMARY KEY,
    title           TEXT NOT NULL,
    description     TEXT,
    event_date      DATE NOT NULL,
    event_type      VARCHAR(20) NOT NULL,
    location        TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_community_events_date ON community_events(event_date);
CREATE INDEX idx_community_events_type ON community_events(event_type);

CREATE TABLE announcements (
    id                  BIGSERIAL PRIMARY KEY,
    person_id           BIGINT REFERENCES persons(id) ON DELETE SET NULL,
    title               TEXT NOT NULL,
    description         TEXT,
    announcement_type   VARCHAR(20) NOT NULL,
    submitter_name      TEXT NOT NULL,
    submitter_contact   TEXT,
    status              VARCHAR(10) NOT NULL DEFAULT 'PENDING',
    admin_notes         TEXT,
    created_at          TIMESTAMP NOT NULL DEFAULT now(),
    reviewed_at         TIMESTAMP
);

CREATE INDEX idx_announcements_status ON announcements(status);

CREATE TABLE fund_entries (
    id                  BIGSERIAL PRIMARY KEY,
    name                TEXT NOT NULL,
    amount              NUMERIC(10,2) NOT NULL,
    entry_date          DATE NOT NULL,
    mode                VARCHAR(20) NOT NULL,
    note                TEXT,
    entry_type          VARCHAR(15) NOT NULL DEFAULT 'CONTRIBUTION',
    related_event_id    BIGINT REFERENCES community_events(id) ON DELETE SET NULL,
    receipt_url         TEXT,
    created_at          TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_fund_entries_type ON fund_entries(entry_type);
CREATE INDEX idx_fund_entries_event ON fund_entries(related_event_id);
