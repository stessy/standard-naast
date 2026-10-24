DO $$
DECLARE
    item RECORD;
    column_item RECORD;
    normalized_name TEXT;
BEGIN
    -- Limit normalization to application objects; never merge two case variants.
    FOR item IN
        SELECT c.oid, c.relname, c.relkind
        FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE n.nspname = 'public'
          AND (
              (c.relkind = 'r' AND lower(c.relname) = ANY (ARRAY[
                  'saison', 'equipe', 'cotisations', 'accounting', 'personnes',
                  'saison_equipe', 'prices', 'travel_price', 'benevolat',
                  'personnes_cotisations', 'match', 'personne_match', 'abonnement',
                  'bloc', 'commande_place', 'prix_place', 'type_competition',
                  'type_match', 'type_personne', 'app_users', 'app_user_roles'
              ]))
              OR
              (c.relkind = 'S' AND lower(c.relname) = ANY (ARRAY[
                  'personne_match_seq', 'match_seq', 'benevolat_seq',
                  'personne_cotisation_seq', 'team_season_seq', 'person_seq',
                  'accounting_seq', 'prices_seq', 'team_seq', 'travel_price_seq',
                  'abonnement_seq', 'user_seq'
              ]))
          )
        ORDER BY c.relname
    LOOP
        normalized_name := lower(item.relname);
        IF item.relname <> normalized_name THEN
            IF EXISTS (
                SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace
                WHERE n.nspname = 'public' AND c.relname = normalized_name
            ) THEN
                RAISE EXCEPTION 'Conflicting application objects: public.% and public.%. Back up and reconcile the schema before retrying.',
                    item.relname, normalized_name;
            END IF;
            IF item.relkind = 'S' THEN
                EXECUTE format('ALTER SEQUENCE public.%I RENAME TO %I', item.relname, normalized_name);
            ELSE
                EXECUTE format('ALTER TABLE public.%I RENAME TO %I', item.relname, normalized_name);
            END IF;
        END IF;

        IF item.relkind = 'r' THEN
            FOR column_item IN
                SELECT attname FROM pg_attribute
                WHERE attrelid = item.oid AND attnum > 0 AND NOT attisdropped
                  AND attname <> lower(attname)
            LOOP
                IF EXISTS (
                    SELECT 1 FROM pg_attribute
                    WHERE attrelid = item.oid AND attnum > 0 AND NOT attisdropped
                      AND attname = lower(column_item.attname)
                ) THEN
                    RAISE EXCEPTION 'Conflicting columns in public.%: % and %.',
                        normalized_name, column_item.attname, lower(column_item.attname);
                END IF;
                EXECUTE format('ALTER TABLE public.%I RENAME COLUMN %I TO %I',
                    normalized_name, column_item.attname, lower(column_item.attname));
            END LOOP;
        END IF;
    END LOOP;
END
$$;
