CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_users_role ON users(role);

CREATE TABLE user_profiles (
    id BIGSERIAL PRIMARY KEY,
    nickname VARCHAR(60) NOT NULL,
    rank VARCHAR(40),
    region VARCHAR(40),
    play_time VARCHAR(40),
    about TEXT,
    preferred_roles_text VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_profiles_nickname ON user_profiles(nickname);
CREATE INDEX idx_profiles_rank ON user_profiles(rank);
CREATE INDEX idx_profiles_region ON user_profiles(region);

CREATE TABLE dota_accounts (
    id BIGSERIAL PRIMARY KEY,
    steam_id VARCHAR(32) NOT NULL,
    account_id VARCHAR(32),
    avatar_url VARCHAR(512),
    profile_url VARCHAR(512),
    mmr INTEGER,
    last_sync_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_dota_accounts_steam_id ON dota_accounts(steam_id);

CREATE TABLE heroes (
    id BIGSERIAL PRIMARY KEY,
    dota_hero_id INTEGER NOT NULL UNIQUE,
    name VARCHAR(80) NOT NULL,
    image_url VARCHAR(512)
);

CREATE TABLE user_profile_favorite_heroes (
    profile_id BIGINT NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    hero_id BIGINT NOT NULL REFERENCES heroes(id) ON DELETE CASCADE,
    PRIMARY KEY (profile_id, hero_id)
);

CREATE TABLE party_posts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    required_rank VARCHAR(40),
    role_needed VARCHAR(40),
    region VARCHAR(40),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    author_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_party_posts_status ON party_posts(status);

CREATE TABLE party_applications (
    id BIGSERIAL PRIMARY KEY,
    message TEXT,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    post_id BIGINT NOT NULL REFERENCES party_posts(id) ON DELETE CASCADE,
    applicant_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_party_applications_status ON party_applications(status);

INSERT INTO users (id, username, email, password_hash, role, enabled, created_at, updated_at)
VALUES
    (1, 'admin', 'admin@dotalink.local', '$2b$12$zEAE0qLZkjX.DsLRnbdd2eY8.RtpOev6d1UJ5QUwhi3Wls1/d2KTq', 'ROLE_ADMIN', TRUE, NOW(), NOW()),
    (2, 'demo', 'demo@dotalink.local', '$2b$12$zdPakMx6nOx7K3Xrgoggx.uR6MMtgdEX5sK1HqGXNOFOAy6bSOpTG', 'ROLE_USER', TRUE, NOW(), NOW());

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

INSERT INTO user_profiles (nickname, rank, region, play_time, about, preferred_roles_text, created_at, updated_at, user_id)
VALUES
    ('CaptainAdmin', 'Immortal', 'EU', 'Evening', 'Administrator profile', 'support,offlane', NOW(), NOW(), 1),
    ('DemoPlayer', 'Archon', 'EU', 'Weekend', 'Demo user profile', 'carry,mid', NOW(), NOW(), 2);

INSERT INTO heroes (dota_hero_id, name, image_url)
VALUES
    (1, 'Anti-Mage', 'https://cdn.cloudflare.steamstatic.com/apps/dota2/images/dota_react/heroes/antimage.png'),
    (74, 'Invoker', 'https://cdn.cloudflare.steamstatic.com/apps/dota2/images/dota_react/heroes/invoker.png'),
    (81, 'Chaos Knight', 'https://cdn.cloudflare.steamstatic.com/apps/dota2/images/dota_react/heroes/chaos_knight.png'),
    (106, 'Ember Spirit', 'https://cdn.cloudflare.steamstatic.com/apps/dota2/images/dota_react/heroes/ember_spirit.png');

INSERT INTO user_profile_favorite_heroes (profile_id, hero_id)
VALUES
    (1, 2),
    (2, 1),
    (2, 4);

INSERT INTO party_posts (title, description, required_rank, role_needed, region, status, created_at, updated_at, author_id)
VALUES
    ('Need pos4 for ranked', 'Evening games, Discord required', 'Legend', 'Support', 'EU', 'OPEN', NOW(), NOW(), 2),
    ('Turbo chill stack', 'Fun games only', 'Any', 'Any', 'US East', 'OPEN', NOW(), NOW(), 1);

INSERT INTO party_applications (message, status, created_at, post_id, applicant_id)
VALUES
    ('Can join after 19:00', 'NEW', NOW(), 1, 1);
