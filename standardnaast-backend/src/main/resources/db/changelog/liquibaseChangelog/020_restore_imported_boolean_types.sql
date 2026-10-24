DO $$
DECLARE
    column_type TEXT;
    invalid_value BOOLEAN;
BEGIN
    SELECT data_type INTO column_type
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'abonnement'
      AND column_name = 'paye';

    IF column_type IS NULL OR column_type = 'boolean' THEN
        RETURN;
    END IF;

    IF column_type NOT IN ('numeric', 'smallint', 'integer', 'bigint', 'real', 'double precision') THEN
        RAISE EXCEPTION 'Cannot convert public.abonnement.paye from % to boolean.',
            column_type;
    END IF;

    EXECUTE 'SELECT EXISTS (SELECT 1 FROM public.abonnement '
        || 'WHERE paye IS NULL OR paye NOT IN (0, 1))'
    INTO invalid_value;
    IF invalid_value THEN
        RAISE EXCEPTION 'Cannot convert public.abonnement.paye to boolean: only 0 and 1 are supported.';
    END IF;

    ALTER TABLE public.abonnement
        ALTER COLUMN paye TYPE BOOLEAN
        USING CASE WHEN paye = 1 THEN TRUE ELSE FALSE END;
END
$$;
