ALTER TABLE dota_recent_matches
    DROP CONSTRAINT IF EXISTS dota_recent_matches_match_id_key;

DROP INDEX IF EXISTS idx_dota_recent_matches_match_id;

CREATE UNIQUE INDEX IF NOT EXISTS uq_dota_recent_matches_account_match
    ON dota_recent_matches(account_id, match_id);
