DO $$
DECLARE
    item RECORD;
BEGIN
    FOR item IN
        SELECT expected.table_name, expected.column_name
        FROM (VALUES
            ('saison', 'date_debut'),
            ('saison', 'date_fin'),
            ('saison', 'date_premier_match_championnat'),
            ('personnes', 'date_naissance'),
            ('personnes', 'validite_carte_identite'),
            ('accounting', 'accounting_date'),
            ('benevolat', 'date_benevolat'),
            ('personnes_cotisations', 'date_paiement')
        ) AS expected(table_name, column_name)
        JOIN information_schema.columns actual
          ON actual.table_schema = 'public'
         AND actual.table_name = expected.table_name
         AND actual.column_name = expected.column_name
        WHERE actual.data_type = 'timestamp without time zone'
    LOOP
        EXECUTE format(
            'ALTER TABLE public.%I ALTER COLUMN %I TYPE DATE USING %I::date',
            item.table_name, item.column_name, item.column_name);
    END LOOP;
END
$$;
