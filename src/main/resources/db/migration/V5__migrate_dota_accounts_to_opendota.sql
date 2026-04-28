DROP INDEX IF EXISTS idx_dota_accounts_steam_id;

ALTER TABLE dota_accounts
    DROP COLUMN IF EXISTS steam_id;

ALTER TABLE dota_accounts
    DROP COLUMN IF EXISTS mmr;

ALTER TABLE dota_accounts
    ALTER COLUMN account_id TYPE BIGINT USING NULLIF(account_id, '')::BIGINT;

ALTER TABLE dota_accounts
    ADD COLUMN IF NOT EXISTS persona_name VARCHAR(255),
    ADD COLUMN IF NOT EXISTS rank_tier INTEGER,
    ADD COLUMN IF NOT EXISTS leaderboard_rank INTEGER;

CREATE UNIQUE INDEX IF NOT EXISTS idx_dota_accounts_account_id ON dota_accounts(account_id)
    WHERE account_id IS NOT NULL;
