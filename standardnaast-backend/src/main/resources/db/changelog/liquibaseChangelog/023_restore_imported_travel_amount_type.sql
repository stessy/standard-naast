DO $$
DECLARE
    column_type TEXT;
BEGIN
    SELECT data_type INTO column_type
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'personne_match'
      AND column_name = 'car_travel_amount';

    IF column_type IS NULL OR column_type = 'bigint' THEN
        RETURN;
    END IF;

    IF column_type <> 'integer' THEN
        RAISE EXCEPTION 'Cannot convert public.personne_match.car_travel_amount from % to bigint.',
            column_type;
    END IF;

    ALTER TABLE public.personne_match
        ALTER COLUMN car_travel_amount TYPE BIGINT
        USING car_travel_amount::bigint;
END
$$;
