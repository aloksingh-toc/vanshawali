CREATE TABLE persons (
    id                  BIGSERIAL PRIMARY KEY,
    parent_id           BIGINT REFERENCES persons(id),
    name                TEXT NOT NULL,
    alias_note          TEXT,
    is_direct_line      BOOLEAN NOT NULL DEFAULT FALSE,
    is_issueless        BOOLEAN NOT NULL DEFAULT FALSE,
    is_unconfirmed      BOOLEAN NOT NULL DEFAULT FALSE,
    is_pending          BOOLEAN NOT NULL DEFAULT FALSE,
    sibling_order       INT NOT NULL DEFAULT 0,
    generation          INT NOT NULL DEFAULT 0,
    birth_date          DATE,
    death_date          DATE,
    date_is_approximate BOOLEAN NOT NULL DEFAULT FALSE,
    photo_url           TEXT,
    sheet_name          TEXT,
    created_at          TIMESTAMP NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_persons_parent_id ON persons(parent_id);
CREATE INDEX idx_persons_name ON persons(name);

CREATE TABLE change_requests (
    id                  BIGSERIAL PRIMARY KEY,
    target_person_id    BIGINT REFERENCES persons(id),
    request_type        VARCHAR(20) NOT NULL,
    proposed_data        JSONB,
    requester_name       TEXT NOT NULL,
    requester_contact    TEXT,
    status               VARCHAR(10) NOT NULL DEFAULT 'PENDING',
    admin_notes          TEXT,
    created_at           TIMESTAMP NOT NULL DEFAULT now(),
    reviewed_at          TIMESTAMP
);

CREATE INDEX idx_change_requests_status ON change_requests(status);

CREATE TABLE app_users (
    id              BIGSERIAL PRIMARY KEY,
    username        TEXT NOT NULL UNIQUE,
    password_hash   TEXT NOT NULL,
    display_name    TEXT,
    role             VARCHAR(20) NOT NULL,
    created_by       BIGINT REFERENCES app_users(id),
    created_at       TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE audit_log (
    id          BIGSERIAL PRIMARY KEY,
    person_id   BIGINT,
    action      TEXT NOT NULL,
    changed_by  TEXT NOT NULL,
    diff        JSONB,
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_log_person_id ON audit_log(person_id);
