DO $$
DECLARE
    item RECORD;
    has_invalid_values BOOLEAN;
BEGIN
    FOR item IN
        SELECT expected.table_name, expected.column_name, actual.data_type
        FROM (VALUES
            ('abonnement', 'paye'),
            ('personnes', 'etudiant'),
            ('personnes', 'red_card'),
            ('saison', 'europeens'),
            ('personnes_cotisations', 'carte_membre_envoyee')
        ) AS expected(table_name, column_name)
        JOIN information_schema.columns actual
          ON actual.table_schema = 'public'
         AND actual.table_name = expected.table_name
         AND actual.column_name = expected.column_name
        WHERE actual.data_type <> 'boolean'
    LOOP
        IF item.data_type NOT IN ('numeric', 'smallint', 'integer', 'bigint', 'real', 'double precision') THEN
            RAISE EXCEPTION 'Cannot convert public.%.% from % to boolean.',
                item.table_name, item.column_name, item.data_type;
        END IF;

        EXECUTE format(
            'SELECT EXISTS (SELECT 1 FROM public.%I WHERE %I IS NOT NULL AND %I NOT IN (0, 1))',
            item.table_name, item.column_name, item.column_name)
        INTO has_invalid_values;
        IF has_invalid_values THEN
            RAISE EXCEPTION 'Cannot convert public.%.% to boolean: only 0 and 1 are supported.',
                item.table_name, item.column_name;
        END IF;

        EXECUTE format(
            'ALTER TABLE public.%I ALTER COLUMN %I TYPE BOOLEAN '
            || 'USING CASE WHEN %I IS NULL THEN NULL WHEN %I = 1 THEN TRUE ELSE FALSE END',
            item.table_name, item.column_name, item.column_name, item.column_name);
    END LOOP;
END
$$;
