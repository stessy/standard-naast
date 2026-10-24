DO $$
DECLARE
    item RECORD;
    constraint_item RECORD;
    invalid_value BOOLEAN;
    saved_constraints JSONB := '[]'::jsonb;
BEGIN
    CREATE TEMP TABLE naast_integer_conversions (
        table_name TEXT,
        column_name TEXT,
        target_type TEXT,
        minimum_value NUMERIC,
        maximum_value NUMERIC
    ) ON COMMIT DROP;

    INSERT INTO naast_integer_conversions
    SELECT expected.table_name, expected.column_name, expected.target_type,
           CASE WHEN expected.target_type = 'bigint' THEN -9223372036854775808::numeric ELSE -2147483648::numeric END,
           CASE WHEN expected.target_type = 'bigint' THEN 9223372036854775807::numeric ELSE 2147483647::numeric END
    FROM (VALUES
        ('equipe', 'equipe_id', 'bigint'),
        ('cotisations', 'annee_cotisation2', 'bigint'),
        ('accounting', 'id', 'bigint'),
        ('personnes', 'personne_id', 'bigint'),
        ('personnes', 'numero_membre', 'bigint'),
        ('saison_equipe', 'saison_equipe_id', 'bigint'),
        ('saison_equipe', 'equipe_id', 'bigint'),
        ('prices', 'id', 'bigint'),
        ('prices', 'prix', 'bigint'),
        ('prices', 'tribune', 'integer'),
        ('travel_price', 'id', 'bigint'),
        ('benevolat', 'benevolat_id', 'bigint'),
        ('benevolat', 'personne_id', 'bigint'),
        ('personnes_cotisations', 'id', 'bigint'),
        ('personnes_cotisations', 'personne_id', 'bigint'),
        ('match', 'match_id', 'bigint'),
        ('match', 'opponent', 'bigint'),
        ('match', 'saison_equipe_id', 'bigint'),
        ('personne_match', 'id', 'bigint'),
        ('personne_match', 'personne_id', 'bigint'),
        ('personne_match', 'match_id', 'bigint'),
        ('personne_match', 'car_travel_amount', 'bigint'),
        ('personne_match', 'prix_locomotion_id', 'bigint'),
        ('abonnement', 'abonnement_id', 'bigint'),
        ('abonnement', 'personne_id', 'bigint'),
        ('abonnement', 'prices_id', 'bigint'),
        ('abonnement', 'reduction', 'bigint'),
        ('abonnement', 'acompte', 'bigint'),
        ('abonnement', 'bloc_id', 'bigint'),
        ('bloc', 'bloc_id', 'bigint'),
        ('bloc', 'equipe_id', 'bigint'),
        ('commande_place', 'commande_place_id', 'bigint'),
        ('commande_place', 'personne_id', 'bigint'),
        ('commande_place', 'match_id', 'bigint'),
        ('commande_place', 'nombre_place', 'integer'),
        ('commande_place', 'bloc_id', 'bigint'),
        ('commande_place', 'place_commandee', 'integer'),
        ('prix_place', 'prix_place_id', 'bigint'),
        ('prix_place', 'type_personne_id', 'bigint'),
        ('prix_place', 'type_match_id', 'bigint'),
        ('type_competition', 'type_competition_id', 'bigint'),
        ('type_match', 'type_match_id', 'bigint'),
        ('type_match', 'type_competition_id', 'bigint'),
        ('type_personne', 'type_personne_id', 'bigint'),
        ('type_personne', 'age_minimum', 'integer'),
        ('type_personne', 'age_maximum', 'integer'),
        ('type_personne', 'etudiant', 'integer'),
        ('type_personne', 'membre', 'integer'),
        ('type_personne', 'tarif', 'integer'),
        ('app_users', 'user_id', 'bigint'),
        ('app_users', 'personne_id', 'bigint'),
        ('app_user_roles', 'user_id', 'bigint')
    ) AS expected(table_name, column_name, target_type)
    JOIN information_schema.columns actual
      ON actual.table_schema = 'public'
     AND actual.table_name = expected.table_name
     AND actual.column_name = expected.column_name
     AND actual.data_type = 'numeric';

    -- Block writes until every value has been checked and every FK restored.
    FOR item IN SELECT DISTINCT table_name FROM naast_integer_conversions ORDER BY table_name LOOP
        EXECUTE format('LOCK TABLE public.%I IN SHARE ROW EXCLUSIVE MODE', item.table_name);
    END LOOP;

    FOR item IN SELECT * FROM naast_integer_conversions LOOP
        EXECUTE format(
            'SELECT EXISTS (SELECT 1 FROM public.%I WHERE %I IS NOT NULL AND '
            || '(%I <> trunc(%I) OR %I < $1 OR %I > $2))',
            item.table_name, item.column_name, item.column_name, item.column_name,
            item.column_name, item.column_name)
        INTO invalid_value USING item.minimum_value, item.maximum_value;
        IF invalid_value THEN
            RAISE EXCEPTION 'Cannot convert public.%.% to %: fractional or out-of-range values. No rounding is permitted.',
                item.table_name, item.column_name, item.target_type;
        END IF;
    END LOOP;

    FOR constraint_item IN
        SELECT c.conname, n.nspname, t.relname, pg_get_constraintdef(c.oid) AS definition
        FROM pg_constraint c
        JOIN pg_class t ON t.oid = c.conrelid
        JOIN pg_namespace n ON n.oid = t.relnamespace
        WHERE c.contype = 'f' AND EXISTS (
            SELECT 1 FROM naast_integer_conversions conversion
            JOIN pg_class changed ON changed.relname = conversion.table_name
            JOIN pg_namespace changed_schema ON changed_schema.oid = changed.relnamespace
            JOIN pg_attribute a ON a.attrelid = changed.oid AND a.attname = conversion.column_name
            WHERE changed_schema.nspname = 'public'
              AND ((c.conrelid = changed.oid AND a.attnum = ANY(c.conkey))
                OR (c.confrelid = changed.oid AND a.attnum = ANY(c.confkey)))
        )
    LOOP
        saved_constraints := saved_constraints || jsonb_build_array(jsonb_build_object(
            'schema', constraint_item.nspname, 'table', constraint_item.relname,
            'name', constraint_item.conname, 'definition', constraint_item.definition));
        EXECUTE format('ALTER TABLE %I.%I DROP CONSTRAINT %I',
            constraint_item.nspname, constraint_item.relname, constraint_item.conname);
    END LOOP;

    FOR item IN SELECT * FROM naast_integer_conversions LOOP
        EXECUTE format('ALTER TABLE public.%I ALTER COLUMN %I TYPE %s USING %I::%s',
            item.table_name, item.column_name, item.target_type, item.column_name, item.target_type);
    END LOOP;

    FOR constraint_item IN SELECT value FROM jsonb_array_elements(saved_constraints) LOOP
        EXECUTE format('ALTER TABLE %I.%I ADD CONSTRAINT %I %s',
            constraint_item.value->>'schema', constraint_item.value->>'table',
            constraint_item.value->>'name', constraint_item.value->>'definition');
    END LOOP;
END
$$;
