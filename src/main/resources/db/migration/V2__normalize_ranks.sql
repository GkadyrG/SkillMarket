UPDATE user_profiles
SET rank = CASE
    WHEN rank IS NULL OR TRIM(rank) = '' THEN NULL
    WHEN LOWER(rank) = 'herald' THEN 'Herald'
    WHEN LOWER(rank) = 'guardian' THEN 'Guardian'
    WHEN LOWER(rank) = 'crusader' THEN 'Crusader'
    WHEN LOWER(rank) = 'archon' THEN 'Archon'
    WHEN LOWER(rank) = 'legend' THEN 'Legend'
    WHEN LOWER(rank) = 'ancient' THEN 'Ancient'
    WHEN LOWER(rank) = 'divine' THEN 'Divine'
    WHEN LOWER(rank) = 'immortal' THEN 'Immortal'
    ELSE NULL
END;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_user_profiles_rank'
    ) THEN
        ALTER TABLE user_profiles
            ADD CONSTRAINT chk_user_profiles_rank
                CHECK (rank IS NULL OR rank IN (
                    'Herald', 'Guardian', 'Crusader', 'Archon',
                    'Legend', 'Ancient', 'Divine', 'Immortal'
                ));
    END IF;
END;
$$;
