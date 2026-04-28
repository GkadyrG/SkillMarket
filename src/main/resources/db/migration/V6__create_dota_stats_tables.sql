CREATE TABLE dota_recent_matches (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    match_id BIGINT NOT NULL UNIQUE,
    hero_id INTEGER NOT NULL,
    kills INTEGER NOT NULL DEFAULT 0,
    deaths INTEGER NOT NULL DEFAULT 0,
    assists INTEGER NOT NULL DEFAULT 0,
    duration_seconds INTEGER NOT NULL DEFAULT 0,
    start_time TIMESTAMP NOT NULL,
    player_slot INTEGER NOT NULL,
    radiant_win BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_dota_recent_matches_account_id_start_time
    ON dota_recent_matches(account_id, start_time DESC);

CREATE TABLE dota_player_hero_stats (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    hero_id INTEGER NOT NULL,
    games BIGINT NOT NULL DEFAULT 0,
    wins BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_dota_player_hero_stats_account_hero UNIQUE (account_id, hero_id)
);

CREATE INDEX idx_dota_player_hero_stats_account_games
    ON dota_player_hero_stats(account_id, games DESC);
