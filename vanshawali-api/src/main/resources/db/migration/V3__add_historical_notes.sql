CREATE TABLE historical_notes (
    id                BIGSERIAL PRIMARY KEY,
    note_month        INT NOT NULL,
    note_day          INT NOT NULL,
    title             TEXT NOT NULL,
    description       TEXT,
    related_person_id BIGINT REFERENCES persons(id) ON DELETE SET NULL,
    photo_url         TEXT,
    created_at        TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_historical_notes_month_day ON historical_notes(note_month, note_day);
