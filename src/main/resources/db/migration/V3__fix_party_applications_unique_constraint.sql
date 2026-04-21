DO $$
DECLARE
    constraint_name text;
    index_name text;
BEGIN
    FOR constraint_name IN
        SELECT c.conname
        FROM pg_constraint c
        JOIN pg_class t ON t.oid = c.conrelid
        JOIN pg_namespace n ON n.oid = t.relnamespace
        WHERE n.nspname = current_schema()
          AND t.relname = 'party_applications'
          AND c.contype = 'u'
          AND array_length(c.conkey, 1) = 1
          AND (
              SELECT a.attname
              FROM pg_attribute a
              WHERE a.attrelid = t.oid
                AND a.attnum = c.conkey[1]
          ) = 'applicant_id'
    LOOP
        EXECUTE format('ALTER TABLE party_applications DROP CONSTRAINT %I', constraint_name);
    END LOOP;

    FOR index_name IN
        SELECT i.indexname
        FROM pg_indexes i
        WHERE i.schemaname = current_schema()
          AND i.tablename = 'party_applications'
          AND i.indexdef LIKE 'CREATE UNIQUE INDEX%'
          AND i.indexdef LIKE '%(applicant_id)%'
    LOOP
        EXECUTE format('DROP INDEX IF EXISTS %I', index_name);
    END LOOP;
END;
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uk_party_applications_post_applicant'
    ) THEN
        ALTER TABLE party_applications
            ADD CONSTRAINT uk_party_applications_post_applicant
                UNIQUE (post_id, applicant_id);
    END IF;
END;
$$;
