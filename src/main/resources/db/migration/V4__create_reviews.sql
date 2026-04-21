CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    rating INTEGER NOT NULL,
    comment TEXT,
    created_at TIMESTAMP NOT NULL,
    author_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    target_user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_reviews_target_user ON reviews(target_user_id);
CREATE INDEX idx_reviews_author ON reviews(author_id);
CREATE INDEX idx_reviews_created_at ON reviews(created_at DESC);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_reviews_rating'
    ) THEN
        ALTER TABLE reviews
            ADD CONSTRAINT chk_reviews_rating CHECK (rating >= 1 AND rating <= 5);
    END IF;
END;
$$;
