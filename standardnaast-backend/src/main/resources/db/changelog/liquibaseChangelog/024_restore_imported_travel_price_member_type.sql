DO $$
DECLARE
    column_type TEXT;
    has_invalid_values BOOLEAN;
BEGIN
    SELECT data_type INTO column_type
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'travel_price'
      AND column_name = 'membre';

    IF column_type IS NULL OR column_type = 'boolean' THEN
        RETURN;
    END IF;

    IF column_type NOT IN ('numeric', 'smallint', 'integer', 'bigint', 'real', 'double precision') THEN
        RAISE EXCEPTION 'Cannot convert public.travel_price.membre from % to boolean.',
            column_type;
    END IF;

    SELECT EXISTS (
        SELECT 1
        FROM public.travel_price
        WHERE membre IS NULL OR membre NOT IN (0, 1)
    ) INTO has_invalid_values;
    IF has_invalid_values THEN
        RAISE EXCEPTION 'Cannot convert public.travel_price.membre to boolean: only 0 and 1 are supported.';
    END IF;

    ALTER TABLE public.travel_price
        ALTER COLUMN membre TYPE BOOLEAN
        USING CASE WHEN membre = 1 THEN TRUE ELSE FALSE END;
END
$$;
