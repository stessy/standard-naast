package standardNaast.backend.liquibase;

import liquibase.integration.spring.SpringLiquibase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import standardNaast.backend.migration.H2ToPostgresMigrator;

import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.util.UUID;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@EnabledIfEnvironmentVariable(named = "MIGRATION_TEST_PG_URL", matches = ".+")
class ImportedDatabaseLiquibaseTest {

    @TempDir
    Path tempDir;

    private String database;
    private String url;
    private String user;

    @BeforeEach
    void createDatabase() throws Exception {
        database = "naast_test_" + UUID.randomUUID().toString().replace("-", "");
        user = System.getProperty("user.name");
        String adminUrl = System.getenv("MIGRATION_TEST_PG_URL");
        try (Connection connection = DriverManager.getConnection(adminUrl, user, "");
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE DATABASE " + database);
        }
        url = adminUrl.substring(0, adminUrl.lastIndexOf('/') + 1) + database;
    }

    @AfterEach
    void dropDatabase() throws Exception {
        if (database != null) {
            try (Connection connection = DriverManager.getConnection(System.getenv("MIGRATION_TEST_PG_URL"), user, "");
                 Statement statement = connection.createStatement()) {
                statement.execute("DROP DATABASE " + database + " WITH (FORCE)");
            }
        }
    }

    @Test
    void initializesEmptyDatabaseAndDoesNotReplayChanges() throws Exception {
        update();
        assertThat(scalar("SELECT count(*) FROM databasechangelog")).isEqualTo(8);
        update();
        assertThat(scalar("SELECT count(*) FROM databasechangelog")).isEqualTo(8);
        assertThat(scalar("SELECT count(*) FROM information_schema.columns "
                + "WHERE table_name = 'personnes' AND column_name = 'roboticket_id'")).isEqualTo(1);
    }

    @Test
    void importsH2ThenAppliesLiquibaseTwiceWithoutLosingDataOrSequences() throws Exception {
        importH2();
        assertThat(scalar("SELECT count(*) FROM \"SAISON\"")).isEqualTo(1);
        long sequenceValue = scalar("SELECT last_value FROM \"PERSON_SEQ\"");
        update();
        assertImportedData(sequenceValue);
        update();
        assertImportedData(sequenceValue);
        assertThat(scalar("SELECT nextval('person_seq')")).isEqualTo(sequenceValue + 50);
    }

    @Test
    void repairsAlreadyMigratedSequenceIncrementsWithoutResettingCounters() throws Exception {
        update();
        Map<String, Integer> increments = expectedSequenceIncrements();
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            for (var sequence : increments.entrySet()) {
                statement.execute("ALTER SEQUENCE public." + sequence.getKey()
                        + " INCREMENT BY " + (sequence.getValue() == 1 ? 50 : 1));
                statement.execute("SELECT setval('public." + sequence.getKey() + "', 20000000, true)");
            }
            statement.execute("DELETE FROM databasechangelog WHERE id = '019-align-sequence-increments'");
        }
        update();
        update();
        for (var sequence : increments.entrySet()) {
            assertThat(scalar("SELECT last_value FROM public." + sequence.getKey())).isEqualTo(20000000);
            assertThat(scalar("SELECT nextval('public." + sequence.getKey() + "')"))
                    .isEqualTo(20000000L + sequence.getValue());
        }
    }

    private Map<String, Integer> expectedSequenceIncrements() {
        return Map.ofEntries(
                Map.entry("abonnement_seq", 1), Map.entry("accounting_seq", 1),
                Map.entry("benevolat_seq", 1), Map.entry("personne_match_seq", 1),
                Map.entry("prices_seq", 1), Map.entry("travel_price_seq", 1),
                Map.entry("user_seq", 1), Map.entry("match_seq", 50),
                Map.entry("person_seq", 50), Map.entry("personne_cotisation_seq", 50),
                Map.entry("team_season_seq", 50), Map.entry("team_seq", 50));
    }

    @Test
    void rejectsCaseCollisionsAndRollsBackAllRenames() throws Exception {
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE \"ACCOUNTING\" (\"ID\" BIGINT)");
            statement.execute("CREATE TABLE \"SAISON\" (\"ID\" BIGINT)");
            statement.execute("CREATE TABLE saison (id BIGINT)");
            statement.execute("INSERT INTO \"SAISON\" VALUES (7)");
            statement.execute("INSERT INTO saison VALUES (8)");
        }
        assertThatThrownBy(this::update).hasStackTraceContaining("Conflicting application objects");
        assertThat(scalar("SELECT \"ID\" FROM \"SAISON\"")).isEqualTo(7);
        assertThat(scalar("SELECT id FROM saison")).isEqualTo(8);
        assertThat(scalar("SELECT count(*) FROM pg_class WHERE relname = 'ACCOUNTING'")).isEqualTo(1);
        assertThat(scalar("SELECT count(*) FROM databasechangelog")).isZero();
    }

    @ParameterizedTest
    @ValueSource(strings = {"42.5", "9223372036854775808", "-9223372036854775809", "'NaN'", "'Infinity'"})
    void rejectsLossyIntegerConversionsWithoutChangingValuesOrConstraints(String value) throws Exception {
        importH2();
        update();
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE abonnement ALTER COLUMN acompte TYPE NUMERIC");
            statement.execute("INSERT INTO abonnement (abonnement_id, saison, reduction, paye, acompte) "
                    + "VALUES (1, '2026', 0, false, " + value + ")");
            statement.execute("DELETE FROM databasechangelog WHERE id = '018-restore-imported-integer-types'");
        }
        assertThatThrownBy(this::update).hasStackTraceContaining("No rounding is permitted");
        assertThat(scalar("SELECT count(*) FROM abonnement WHERE acompte = " + value)).isEqualTo(1);
        assertThat(scalar("SELECT count(*) FROM information_schema.columns "
                + "WHERE table_name = 'abonnement' AND column_name = 'acompte' AND data_type = 'numeric'")).isEqualTo(1);
        assertThat(scalar("SELECT count(*) FROM databasechangelog WHERE id = '018-restore-imported-integer-types'"))
                .isZero();
        assertThat(scalar("SELECT count(*) FROM pg_constraint "
                + "WHERE conrelid = 'personnes_cotisations'::regclass AND contype = 'f'")).isEqualTo(1);
    }

    @ParameterizedTest
    @CsvSource({"2147483647,true", "-2147483648,true", "2147483648,false", "-2147483649,false", "1.5,false"})
    void enforcesInteger32BitBounds(String value, boolean valid) throws Exception {
        importH2();
        update();
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE type_personne ALTER COLUMN age_minimum TYPE NUMERIC");
            statement.execute("INSERT INTO type_personne (type_personne_id, denomination_type_personne, age_minimum) "
                    + "VALUES (1, 'test', " + value + ")");
            statement.execute("DELETE FROM databasechangelog WHERE id = '018-restore-imported-integer-types'");
        }
        if (valid) {
            update();
            assertThat(scalar("SELECT count(*) FROM information_schema.columns WHERE table_name = 'type_personne' "
                    + "AND column_name = 'age_minimum' AND data_type = 'integer'")).isEqualTo(1);
        } else {
            assertThatThrownBy(this::update).hasStackTraceContaining("No rounding is permitted");
            assertThat(scalar("SELECT count(*) FROM databasechangelog "
                    + "WHERE id = '018-restore-imported-integer-types'")).isZero();
        }
        assertThat(scalar("SELECT count(*) FROM type_personne WHERE age_minimum = " + value)).isEqualTo(1);
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "MIGRATION_NATIVE_BINARY", matches = ".+")
    void startsNativeApplicationTwiceAfterH2Import() throws Exception {
        importH2(true);
        long sequenceValue = scalar("SELECT last_value FROM \"PERSON_SEQ\"");
        for (int attempt = 0; attempt < 2; attempt++) {
            Path log = tempDir.resolve("native-" + attempt + ".log");
            ProcessBuilder builder = new ProcessBuilder(System.getenv("MIGRATION_NATIVE_BINARY"),
                    "--spring.profiles.active=prod", "--server.port=0");
            builder.environment().put("SPRING_DATASOURCE_URL", url);
            builder.environment().put("SPRING_DATASOURCE_USERNAME", user);
            builder.environment().put("SPRING_DATASOURCE_PASSWORD", "");
            builder.directory(tempDir.toFile()).redirectErrorStream(true).redirectOutput(log.toFile());
            Process process = builder.start();
            try {
                long deadline = System.nanoTime() + Duration.ofSeconds(45).toNanos();
                while (process.isAlive() && System.nanoTime() < deadline
                        && !Files.readString(log).contains("Started StandardNaastApplication")) {
                    Thread.sleep(200);
                }
                assertThat(Files.readString(log)).contains("Started StandardNaastApplication");
                assertImportedData(sequenceValue);
                assertThat(scalar("SELECT user_id FROM app_users WHERE username = 'migration-user'")).isEqualTo(1000);
                assertThat(scalar("SELECT count(*) FROM app_user_roles WHERE user_id = 1000 AND role = 'ROLE_USER'"))
                        .isEqualTo(1);
                assertThat(scalar("SELECT count(*) FROM personnes WHERE roboticket_id = 'kept-ticket'")).isEqualTo(1);
            } finally {
                process.destroy();
                if (!process.waitFor(15, TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                    assertThat(process.waitFor(15, TimeUnit.SECONDS)).isTrue();
                }
            }
        }
    }

    private void importH2() throws Exception {
        importH2(false);
    }

    private void importH2(boolean includeExistingEvolutions) throws Exception {
        String sourceUrl = "jdbc:h2:mem:" + database + ";DB_CLOSE_DELAY=-1;NON_KEYWORDS=VALUE,USER,ROLE";
        try (Connection source = DriverManager.getConnection(sourceUrl, "sa", "");
             Statement statement = source.createStatement()) {
            String schema = new ClassPathResource("db/changelog/liquibaseChangelog/000_initial_schema.sql")
                    .getContentAsString(StandardCharsets.UTF_8);
            // Legacy H2 schemas use unconstrained NUMERIC for Java integer properties too.
            statement.execute(schema.replaceAll("\\bBIGINT\\b|\\bINT\\b", "NUMERIC"));
            for (var sequence : expectedSequenceIncrements().entrySet()) {
                if (!sequence.getKey().equals("user_seq")) {
                    statement.execute("ALTER SEQUENCE " + sequence.getKey()
                            + " INCREMENT BY " + (sequence.getValue() == 1 ? 50 : 1));
                }
            }
            statement.execute("ALTER TABLE PERSONNES_COTISATIONS ADD CONSTRAINT TEST_IMPORTED_FK "
                    + "FOREIGN KEY (PERSONNE_ID) REFERENCES PERSONNES(PERSONNE_ID)");
            statement.execute("INSERT INTO SAISON (SAISON_ID, DATE_DEBUT, DATE_FIN) "
                    + "VALUES ('2026', DATE '2026-07-01', DATE '2027-06-30')");
            statement.execute("INSERT INTO PERSONNES (PERSONNE_ID, NOM, NUMERO_MEMBRE) "
                    + "VALUES (42, 'Migration test', 9223372036854775807)");
            statement.execute("INSERT INTO ABONNEMENT (ABONNEMENT_ID, PERSONNE_ID, SAISON, REDUCTION, PAYE, ACOMPTE) "
                    + "VALUES (2, 42, '2026', 0, false, -9223372036854775808)");
        }
        if (includeExistingEvolutions) {
            SpringLiquibase sourceLiquibase = new SpringLiquibase();
            sourceLiquibase.setDataSource(new DriverManagerDataSource(sourceUrl, "sa", ""));
            sourceLiquibase.setChangeLog("db/changelog/db.changelog-master.xml");
            sourceLiquibase.afterPropertiesSet();
            try (Connection source = DriverManager.getConnection(sourceUrl, "sa", "");
                 Statement statement = source.createStatement()) {
                statement.execute("INSERT INTO APP_USERS (USER_ID, USERNAME, PASSWORD, PERSONNE_ID) "
                        + "VALUES (1000, 'migration-user', 'test-only', 42)");
                statement.execute("INSERT INTO APP_USER_ROLES VALUES (1000, 'ROLE_USER')");
                statement.execute("UPDATE PERSONNES SET ROBOTICKET_ID = 'kept-ticket' WHERE PERSONNE_ID = 42");
                statement.execute("ALTER SEQUENCE USER_SEQ INCREMENT BY 50");
            }
        }
        var result = new H2ToPostgresMigrator().migrate(
                new H2ToPostgresMigrator.MigrationConfig(sourceUrl)
                        .setH2Password("").setPgUrl(url).setPgUser(user).setPgPassword(""));
        assertThat(result.isSuccess()).as(result.toString()).isTrue();
        assertThat(result.getWarnings()).isEmpty();
        assertThat(result.getTotalRowsMigrated()).isEqualTo(includeExistingEvolutions ? 5 : 3);
    }

    private void assertImportedData(long sequenceValue) throws Exception {
        assertThat(scalar("SELECT count(*) FROM saison WHERE saison_id = '2026'")).isEqualTo(1);
        assertThat(scalar("SELECT personne_id FROM personnes WHERE nom = 'Migration test'")).isEqualTo(42);
        assertThat(scalar("SELECT numero_membre FROM personnes WHERE personne_id = 42")).isEqualTo(Long.MAX_VALUE);
        assertThat(scalar("SELECT acompte FROM abonnement WHERE abonnement_id = 2")).isEqualTo(Long.MIN_VALUE);
        assertThat(scalar("SELECT last_value FROM person_seq")).isEqualTo(sequenceValue);
        assertThat(scalar("SELECT count(*) FROM databasechangelog")).isEqualTo(8);
        for (var sequence : expectedSequenceIncrements().entrySet()) {
            assertThat(scalar("SELECT increment_by FROM pg_sequences WHERE schemaname = 'public' "
                    + "AND sequencename = '" + sequence.getKey() + "'"))
                    .as(sequence.getKey()).isEqualTo(sequence.getValue().longValue());
        }
        assertThat(scalar("SELECT count(*) FROM databasechangelog "
                + "WHERE id = '000-initial-schema' AND exectype = 'EXECUTED'")).isEqualTo(1);
        assertThat(scalar("SELECT count(*) FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace "
                + "WHERE n.nspname = 'public' AND c.relkind IN ('r', 'S') AND c.relname <> lower(c.relname)")).isZero();
        assertThat(scalar("SELECT count(*) FROM information_schema.columns "
                + "WHERE table_name = 'personnes' AND column_name = 'roboticket_id'")).isEqualTo(1);
        assertThat(scalar("SELECT count(*) FROM pg_constraint "
                + "WHERE conrelid = 'personnes_cotisations'::regclass AND contype = 'f'")).isEqualTo(1);
        assertThat(scalar("SELECT count(*) FROM information_schema.columns "
                + "WHERE table_name = 'abonnement' AND column_name = 'personne_id' AND data_type = 'bigint'"))
                .isEqualTo(1);
    }

    private void update() throws Exception {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(new DriverManagerDataSource(url, user, ""));
        liquibase.setChangeLog("db/changelog/db.changelog-master.xml");
        liquibase.afterPropertiesSet();
    }

    private Connection connect() throws Exception {
        return DriverManager.getConnection(url, user, "");
    }

    private long scalar(String sql) throws Exception {
        try (Connection connection = connect(); Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            assertThat(rs.next()).isTrue();
            return rs.getLong(1);
        }
    }
}
