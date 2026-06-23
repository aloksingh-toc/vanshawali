CREATE TABLE gallery_posts (
    id              BIGSERIAL PRIMARY KEY,
    photo_url       TEXT NOT NULL,
    caption         TEXT,
    uploader_name   TEXT NOT NULL,
    uploader_contact TEXT,
    status          VARCHAR(10) NOT NULL DEFAULT 'PENDING',
    admin_notes     TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    reviewed_at     TIMESTAMP
);

CREATE INDEX idx_gallery_posts_status ON gallery_posts(status);

CREATE TABLE gallery_comments (
    id              BIGSERIAL PRIMARY KEY,
    post_id         BIGINT NOT NULL REFERENCES gallery_posts(id) ON DELETE CASCADE,
    commenter_name  TEXT NOT NULL,
    body            TEXT NOT NULL,
    status          VARCHAR(10) NOT NULL DEFAULT 'PENDING',
    admin_notes     TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    reviewed_at     TIMESTAMP
);

CREATE INDEX idx_gallery_comments_post_id ON gallery_comments(post_id);
CREATE INDEX idx_gallery_comments_status ON gallery_comments(status);
