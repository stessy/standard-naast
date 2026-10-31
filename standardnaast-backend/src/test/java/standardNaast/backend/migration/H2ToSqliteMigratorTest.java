package standardNaast.backend.migration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import standardNaast.backend.security.InitialAdminSetup;
import org.springframework.boot.builder.SpringApplicationBuilder;
import standardNaast.backend.StandardNaastApplication;
import standardNaast.backend.repository.PersonRepository;
import standardNaast.backend.repository.SeasonRepository;
import standardNaast.backend.repository.AppUserRepository;
import standardNaast.backend.repository.AccountingRepository;
import standardNaast.backend.repository.MatchRepository;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class H2ToSqliteMigratorTest {
    @TempDir
    Path work;
    private Path backup;
    private Path database;

    @BeforeEach
    void setUp() throws Exception {
        backup = work.resolve("legacy-backup.sql");
        database = work.resolve("standardnaast.sqlite");
    }

    @Test
    void importsLegacySqlBackupWithRelationsTypesAndHistoricalDataWithoutChangingSource() throws Exception {
        Files.writeString(backup, validBackup());
        String sourceHash = sha256(backup);

        var result = H2ToSqliteMigrator.importBackup(backup, database, null);

        assertThat(sha256(backup)).isEqualTo(sourceHash);
        assertThat(result.totalRows()).isEqualTo(8);
        assertThat(result.rowsPerTable()).containsEntry("SAISON", 1L)
                .containsEntry("SAISON_EQUIPE", 1L)
                .containsEntry("PERSONNES", 1L)
                .containsEntry("BENEVOLAT", 1L)
                .containsEntry("ACCOUNTING", 1L)
                .containsEntry("APP_USERS", 1L)
                .containsEntry("APP_USER_ROLES", 1L);
        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + database);
             var statement = connection.createStatement()) {
            try (ResultSet row = statement.executeQuery(
                    "SELECT DATE_DEBUT, EUROPEENS, MONTANT_COTISATION FROM SAISON WHERE SAISON_ID='2025-2026'")) {
                assertThat(row.next()).isTrue();
                assertThat(row.getDate("DATE_DEBUT", java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")))
                        .toLocalDate()).isEqualTo(java.time.LocalDate.of(2025, 7, 1));
                assertThat(row.getInt("EUROPEENS")).isEqualTo(1);
                assertThat(row.getBigDecimal("MONTANT_COTISATION")).isEqualByComparingTo("12345.67");
            }
            try (ResultSet row = statement.executeQuery(
                    "SELECT PERSONNE_ID, DATE_BENEVOLAT, MONTANT FROM BENEVOLAT")) {
                assertThat(row.next()).isTrue();
                assertThat(row.getLong("PERSONNE_ID")).isEqualTo(73);
                assertThat(row.getDate("DATE_BENEVOLAT", java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")))
                        .toLocalDate()).isEqualTo(java.time.LocalDate.of(2025, 8, 3));
                assertThat(row.getBigDecimal("MONTANT")).isEqualByComparingTo("15.25");
            }
            assertThat(InitialAdminSetup.hasUsers(connection)).isTrue();
            try (ResultSet row = statement.executeQuery("PRAGMA foreign_key_check")) {
                assertThat(row.next()).isFalse();
            }
            statement.executeUpdate("INSERT INTO EQUIPE (NOM_EQUIPE) VALUES ('Equipe suivante')");
            try (ResultSet row = statement.executeQuery(
                    "SELECT EQUIPE_ID FROM EQUIPE WHERE NOM_EQUIPE='Equipe suivante'")) {
                assertThat(row.next()).isTrue();
                assertThat(row.getLong(1)).isEqualTo(901);
            }
        }
    }

    @Test
    void importedDatesAndCredentialsRemainReadableThroughHibernateAndApiAfterRestart() throws Exception {
        String hash = new BCryptPasswordEncoder().encode("password-test-123");
        Files.writeString(backup, validBackup().replace("bcrypt-value", hash) + matchBackup());
        H2ToSqliteMigrator.importBackup(backup, database, null);
        var mapper = JsonMapper.builder().build();
        for (int launch = 0; launch < 2; launch++) {
            try (var context = new SpringApplicationBuilder(StandardNaastApplication.class)
                    .profiles("desktop")
                    .run("--app.desktop.data-dir=" + work, "--server.port=0");
                 var client = HttpClient.newHttpClient()) {
                assertThat(context.getBean(PersonRepository.class).findById(73L).orElseThrow().getBirthdate())
                        .isEqualTo(java.time.LocalDate.of(1980, 2, 29));
                var season = context.getBean(SeasonRepository.class).findById("2025-2026").orElseThrow();
                assertThat(season.getDateStart()).isEqualTo(java.time.LocalDate.of(2025, 7, 1));
                assertThat(season.getDateEnd()).isEqualTo(java.time.LocalDate.of(2026, 6, 30));
                assertThat(context.getBean(AccountingRepository.class).findById(13L).orElseThrow().getDate())
                        .isEqualTo(java.time.LocalDate.of(2025, 8, 4));
                assertThat(context.getBean(MatchRepository.class).findById(101L).orElseThrow().getDateMatch())
                        .isEqualTo(java.time.LocalDateTime.of(2025, 10, 26, 18, 30, 15, 123_000_000));
                var user = context.getBean(AppUserRepository.class).findById(88L).orElseThrow();
                assertThat(user.getCreatedAt()).isEqualTo(java.time.LocalDateTime.of(2025, 8, 1, 10, 11, 12));
                assertThat(user.getUpdatedAt()).isEqualTo(user.getCreatedAt());
                assertThat(user.getPassword()).isEqualTo(hash);
                int port = ((WebServerApplicationContext) context).getWebServer().getPort();
                String url = "http://127.0.0.1:" + port;
                var unauthorized = client.send(HttpRequest.newBuilder(URI.create(url + "/api/members"))
                        .timeout(Duration.ofSeconds(10)).GET().build(), HttpResponse.BodyHandlers.ofString());
                assertThat(unauthorized.statusCode()).isEqualTo(401);
                var login = client.send(HttpRequest.newBuilder(URI.create(url + "/api/auth/login"))
                        .timeout(Duration.ofSeconds(10)).header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("""
                                {"username":"ancien-admin","password":"password-test-123"}
                                """)).build(), HttpResponse.BodyHandlers.ofString());
                assertThat(login.statusCode()).isEqualTo(200);
                String token = mapper.readTree(login.body()).get("accessToken").asText();
                assertThat(mapper.readTree(authenticatedGet(client, url, "/api/members/73", token))
                        .get("birthdate").asText()).isEqualTo("1980-02-29");
                assertThat(mapper.readTree(authenticatedGet(client, url, "/api/matches/101", token))
                        .get("dateMatch").asText()).isEqualTo("2025-10-26T18:30:15.123");
                assertThat(mapper.readTree(authenticatedGet(client, url, "/api/accountings/13", token))
                        .get("date").asText()).isEqualTo("2025-08-04");
            }
        }
    }

    @Test
    void importsAnActualH2ScriptExportWithoutAnExtension() throws Exception {
        try (var connection = DriverManager.getConnection("jdbc:h2:mem:" + java.util.UUID.randomUUID(), "sa", "");
             var reader = new java.io.StringReader(validBackup().replace("DECIMAL(0)", "DECIMAL")
                     .replace("NUMBER(19,2)", "NUMERIC(19,2)"));
             var statement = connection.createStatement()) {
            org.h2.tools.RunScript.execute(connection, reader);
            StringBuilder sql = new StringBuilder();
            try (var rows = statement.executeQuery("SCRIPT")) {
                while (rows.next()) sql.append(rows.getString(1)).append('\n');
            }
            backup = work.resolve("backup_without_extension");
            Files.writeString(backup, sql);
        }
        String hash = sha256(backup);
        assertThat(H2ToSqliteMigrator.importBackup(backup, database, null).totalRows()).isEqualTo(8);
        assertThat(sha256(backup)).isEqualTo(hash);
        assertThat(stagingFiles()).isEmpty();
    }

    @Test
    void refusesInvalidBooleansAndFractionalIdentifiers() throws Exception {
        Files.writeString(backup, validBackup().replace("EUROPEENS BOOLEAN", "EUROPEENS INTEGER")
                .replace(", TRUE, 12345.67", ", 2, 12345.67"));
        assertThatThrownBy(() -> H2ToSqliteMigrator.importBackup(backup, database, null))
                .isInstanceOf(java.io.IOException.class).hasMessageContaining("Valeur booleenne invalide");
        assertThat(database).doesNotExist();
        assertThat(stagingFiles()).isEmpty();

        Files.writeString(backup, """
                CREATE CACHED TABLE PUBLIC.EQUIPE (
                    EQUIPE_ID DECIMAL(19,2) PRIMARY KEY, NOM_EQUIPE VARCHAR(150) NOT NULL
                );
                INSERT INTO PUBLIC.EQUIPE VALUES (900.5, 'Equipe legacy');
                """);
        assertThatThrownBy(() -> H2ToSqliteMigrator.importBackup(backup, database, null))
                .isInstanceOf(java.io.IOException.class).hasMessageContaining("Valeur entiere non representable");
        assertThat(database).doesNotExist();
        assertThat(stagingFiles()).isEmpty();
    }

    @Test
    void retainsKnownHistoricalTablesOutsideTheCurrentOrmModel() throws Exception {
        Files.writeString(backup, validBackup() + matchBackup() + """
                CREATE CACHED TABLE PUBLIC.TYPE_COMPETITION (
                    TYPE_COMPETITION_ID BIGINT PRIMARY KEY,
                    TYPE_COMPETITION_VALUE VARCHAR(100) NOT NULL
                );
                CREATE CACHED TABLE PUBLIC.TYPE_MATCH (
                    TYPE_MATCH_ID BIGINT PRIMARY KEY,
                    TYPE_COMPETITION_ID BIGINT NOT NULL REFERENCES PUBLIC.TYPE_COMPETITION(TYPE_COMPETITION_ID),
                    DENOMINATION_MATCH VARCHAR(100) NOT NULL
                );
                INSERT INTO PUBLIC.TYPE_COMPETITION VALUES (51, 'Coupe historique');
                INSERT INTO PUBLIC.TYPE_MATCH VALUES (61, 51, 'Ancien match');
                CREATE CACHED TABLE PUBLIC.TYPE_PERSONNE (
                    TYPE_PERSONNE_ID BIGINT PRIMARY KEY,
                    DENOMINATION_TYPE_PERSONNE VARCHAR(100) NOT NULL
                );
                CREATE CACHED TABLE PUBLIC.BLOC (
                    BLOC_ID BIGINT PRIMARY KEY,
                    BLOC_VALUE VARCHAR(50) NOT NULL,
                    CLUB VARCHAR(200) NOT NULL
                );
                CREATE CACHED TABLE PUBLIC.PRIX_PLACE (
                    PRIX_PLACE_ID BIGINT PRIMARY KEY,
                    BLOC_ID VARCHAR(100) NOT NULL,
                    TYPE_PERSONNE_ID BIGINT NOT NULL REFERENCES PUBLIC.TYPE_PERSONNE(TYPE_PERSONNE_ID),
                    TYPE_MATCH_ID BIGINT NOT NULL REFERENCES PUBLIC.TYPE_MATCH(TYPE_MATCH_ID),
                    MONTANT DECIMAL(19,2) NOT NULL,
                    SAISON VARCHAR(10) NOT NULL REFERENCES PUBLIC.SAISON(SAISON_ID)
                );
                CREATE CACHED TABLE PUBLIC.COMMANDE_PLACE (
                    COMMANDE_PLACE_ID BIGINT PRIMARY KEY,
                    PERSONNE_ID BIGINT NOT NULL REFERENCES PUBLIC.PERSONNES(PERSONNE_ID),
                    MATCH_ID BIGINT NOT NULL REFERENCES PUBLIC.MATCH(MATCH_ID),
                    DATE_COMMANDE TIMESTAMP NOT NULL,
                    NOMBRE_PLACE INT NOT NULL,
                    BLOC_ID BIGINT REFERENCES PUBLIC.BLOC(BLOC_ID),
                    PLACE_COMMANDEE INT NOT NULL
                );
                INSERT INTO PUBLIC.TYPE_PERSONNE VALUES (71, 'Adulte');
                INSERT INTO PUBLIC.BLOC VALUES (1, 'A1', 'Standard');
                INSERT INTO PUBLIC.PRIX_PLACE VALUES (700, 'A1', 71, 61, 19.95, '2025-2026');
                INSERT INTO PUBLIC.COMMANDE_PLACE VALUES (800, 73, 101,
                    TIMESTAMP '2025-10-25 12:00:00', 2, 1, 1);
                """);
        var result = H2ToSqliteMigrator.importBackup(backup, database, null);
        assertThat(result.rowsPerTable()).containsEntry("TYPE_COMPETITION", 1L).containsEntry("TYPE_MATCH", 1L)
                .containsEntry("TYPE_PERSONNE", 1L).containsEntry("PRIX_PLACE", 1L).containsEntry("COMMANDE_PLACE", 1L);
        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + database);
             var statement = connection.createStatement()) {
            try (var rows = statement.executeQuery("SELECT DENOMINATION_MATCH FROM TYPE_MATCH WHERE TYPE_MATCH_ID=61")) {
                assertThat(rows.next()).isTrue();
                assertThat(rows.getString(1)).isEqualTo("Ancien match");
            }
            try (var rows = statement.executeQuery("SELECT PERSONNE_ID, MATCH_ID, NOMBRE_PLACE FROM COMMANDE_PLACE")) {
                assertThat(rows.next()).isTrue();
                assertThat(rows.getLong("PERSONNE_ID")).isEqualTo(73);
                assertThat(rows.getLong("MATCH_ID")).isEqualTo(101);
                assertThat(rows.getInt("NOMBRE_PLACE")).isEqualTo(2);
            }
            try (var rows = statement.executeQuery("SELECT MONTANT FROM PRIX_PLACE WHERE PRIX_PLACE_ID=700")) {
                assertThat(rows.next()).isTrue();
                assertThat(rows.getBigDecimal(1)).isEqualByComparingTo("19.95");
            }
        }
        assertThat(stagingFiles()).isEmpty();
    }

    @Test
    void rejectsUnsafeSqlWithoutExecutingItOrPublishingAStagingDatabase() throws Exception {
        Files.writeString(backup, """
                CREATE ALIAS EXEC FOR "java.lang.Runtime.getRuntime";
                CALL EXEC();
                """);
        String sourceHash = sha256(backup);

        assertThatThrownBy(() -> H2ToSqliteMigrator.importBackup(backup, database, null))
                .isInstanceOf(java.io.IOException.class)
                .hasMessageContaining("Instruction SQL H2 refusee");

        assertThat(sha256(backup)).isEqualTo(sourceHash);
        assertThat(database).doesNotExist();
        assertThat(stagingFiles()).isEmpty();
    }

    @Test
    void archivesOnlyMissingPersonCotisationsAndTravelsWithAllValuesPreserved() throws Exception {
        Files.writeString(backup, validBackup() + matchBackup() + orphanMembershipBackup());
        String hash = sha256(backup);
        var result = H2ToSqliteMigrator.importBackup(backup, database, null);
        assertThat(result.rowsPerTable()).containsEntry("PERSONNES_COTISATIONS", 1L)
                .containsEntry("PERSONNES_COTISATIONS_IMPORT_ARCHIVE", 1L)
                .containsEntry("PERSONNE_MATCH", 1L).containsEntry("PERSONNE_MATCH_IMPORT_ARCHIVE", 1L);
        assertThat(result.totalRows()).isEqualTo(13);
        assertThat(sha256(backup)).isEqualTo(hash);
        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + database);
             var statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys=ON");
            try (var rows = statement.executeQuery("SELECT * FROM PERSONNES_COTISATIONS_IMPORT_ARCHIVE")) {
                assertThat(rows.next()).isTrue();
                assertThat(rows.getLong("ID")).isEqualTo(10151);
                assertThat(rows.getLong("PERSONNE_ID")).isEqualTo(1999965);
                assertThat(rows.getString("SEASON_ID")).isEqualTo("2025-2026");
                assertThat(rows.getString("ANNEE_COTISATION")).isEqualTo("2025");
                assertThat(rows.getBoolean("CARTE_MEMBRE_ENVOYEE")).isTrue();
                assertThat(rows.getString("IMPORT_REASON")).isEqualTo("PERSONNE_ABSENTE");
            }
            try (var rows = statement.executeQuery("SELECT * FROM PERSONNE_MATCH_IMPORT_ARCHIVE")) {
                assertThat(rows.next()).isTrue();
                assertThat(rows.getLong("ID")).isEqualTo(609850);
                assertThat(rows.getLong("PERSONNE_ID")).isEqualTo(7903);
                assertThat(rows.getLong("MATCH_ID")).isEqualTo(101);
                assertThat(rows.getLong("CAR_TRAVEL_AMOUNT")).isEqualTo(25);
                assertThat(rows.getObject("PRIX_LOCOMOTION_ID")).isNull();
                assertThat(rows.getString("IMPORT_REASON")).isEqualTo("PERSONNE_ABSENTE");
            }
            try (var rows = statement.executeQuery("PRAGMA foreign_key_check")) {
                assertThat(rows.next()).isFalse();
            }
            assertThatThrownBy(() -> statement.executeUpdate("""
                    INSERT INTO PERSONNES_COTISATIONS (PERSONNE_ID, SEASON_ID) VALUES (1999965, '2025-2026')
                    """)).isInstanceOf(java.sql.SQLException.class);
        }
        try (var context = new SpringApplicationBuilder(StandardNaastApplication.class).profiles("desktop")
                .run("--app.desktop.data-dir=" + work, "--server.port=0")) {
            var cotisations = context.getBean(standardNaast.backend.repository.PersonCotisationRepository.class);
            assertThat(cotisations.count()).isEqualTo(1);
            assertThat(cotisations.findById(10151L)).isEmpty();
            assertThat(cotisations.findById(2L).orElseThrow().getDatePaiement())
                    .isEqualTo(java.time.LocalDate.of(2025, 8, 1));
        }
        assertThat(stagingFiles()).isEmpty();
    }

    @Test
    void stillRejectsMissingSeasonOrMatchWhenPersonExists() throws Exception {
        Files.writeString(backup, validBackup() + matchBackup()
                + orphanMembershipBackup().replace("(2, 73, '2025-2026'", "(2, 73, '1900-1901'"));
        assertThatThrownBy(() -> H2ToSqliteMigrator.importBackup(backup, database, null))
                .isInstanceOf(java.io.IOException.class).hasMessageContaining("relation invalide");
        assertThat(database).doesNotExist();
        assertThat(stagingFiles()).isEmpty();
    }

    private static String orphanMembershipBackup() {
        return """
                SET REFERENTIAL_INTEGRITY FALSE;
                CREATE CACHED TABLE PUBLIC.PERSONNES_COTISATIONS (
                    ID BIGINT PRIMARY KEY,
                    PERSONNE_ID BIGINT NOT NULL REFERENCES PUBLIC.PERSONNES(PERSONNE_ID),
                    SEASON_ID VARCHAR(10) NOT NULL REFERENCES PUBLIC.SAISON(SAISON_ID),
                    DATE_PAIEMENT TIMESTAMP,
                    CARTE_MEMBRE_ENVOYEE BOOLEAN NOT NULL,
                    ANNEE_COTISATION VARCHAR(4)
                );
                CREATE CACHED TABLE PUBLIC.PERSONNE_MATCH (
                    ID BIGINT PRIMARY KEY,
                    PERSONNE_ID BIGINT NOT NULL REFERENCES PUBLIC.PERSONNES(PERSONNE_ID),
                    MATCH_ID BIGINT NOT NULL REFERENCES PUBLIC.MATCH(MATCH_ID),
                    CAR_TRAVEL_AMOUNT BIGINT NOT NULL,
                    PRIX_LOCOMOTION_ID BIGINT
                );
                INSERT INTO PUBLIC.PERSONNES_COTISATIONS VALUES
                    (2, 73, '2025-2026', TIMESTAMP '2025-08-01 00:00:00', FALSE, '2025'),
                    (10151, 1999965, '2025-2026', TIMESTAMP '2025-08-02 00:00:00', TRUE, '2025');
                INSERT INTO PUBLIC.PERSONNE_MATCH VALUES (3, 73, 101, 15, NULL), (609850, 7903, 101, 25, NULL);
                """;
    }

    @Test
    void preservesHistoricalOrphansWithoutInventingParentsOrDisablingApplicationConstraints() throws Exception {
        Files.writeString(backup, validBackup() + archiveOrphansBackup());
        String hash = sha256(backup);
        var imported = H2ToSqliteMigrator.importBackup(backup, database, null);
        assertThat(imported.rowsPerTable()).containsEntry("PRIX_PLACE", 1L).containsEntry("COMMANDE_PLACE", 1L);
        assertThat(sha256(backup)).isEqualTo(hash);
        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + database);
             var statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys=ON");
            try (var rows = statement.executeQuery("SELECT TYPE_PERSONNE_ID, TYPE_MATCH_ID, SAISON FROM PRIX_PLACE")) {
                assertThat(rows.next()).isTrue();
                assertThat(rows.getLong("TYPE_PERSONNE_ID")).isEqualTo(999);
                assertThat(rows.getLong("TYPE_MATCH_ID")).isEqualTo(998);
                assertThat(rows.getString("SAISON")).isEqualTo("1900-1901");
            }
            for (String table : java.util.List.of("TYPE_PERSONNE", "TYPE_COMPETITION")) {
                try (var rows = statement.executeQuery("SELECT COUNT(*) FROM " + table)) {
                    assertThat(rows.next()).isTrue();
                    assertThat(rows.getLong(1)).isZero();
                }
            }
            try (var rows = statement.executeQuery("SELECT PERSONNE_ID, MATCH_ID, BLOC_ID FROM COMMANDE_PLACE")) {
                assertThat(rows.next()).isTrue();
                assertThat(rows.getLong("PERSONNE_ID")).isEqualTo(996);
                assertThat(rows.getLong("MATCH_ID")).isEqualTo(995);
                assertThat(rows.getLong("BLOC_ID")).isEqualTo(994);
            }
            for (String table : java.util.List.of("TYPE_MATCH", "PRIX_PLACE", "COMMANDE_PLACE")) {
                try (var rows = statement.executeQuery("PRAGMA foreign_key_list(" + table + ")")) {
                    assertThat(rows.next()).isFalse();
                }
            }
            try (var rows = statement.executeQuery("PRAGMA foreign_key_check")) {
                assertThat(rows.next()).isFalse();
            }
            assertThatThrownBy(() -> statement.executeUpdate(
                    "INSERT INTO BENEVOLAT (PERSONNE_ID) VALUES (9999)")).isInstanceOf(java.sql.SQLException.class);
        }
        assertThat(stagingFiles()).isEmpty();
    }

    @Test
    void stillRejectsBrokenForeignKeysExplicitlyDeclaredInAnArchiveBackup() throws Exception {
        Files.writeString(backup, "SET REFERENTIAL_INTEGRITY FALSE;\n" + validBackup() + """
                CREATE CACHED TABLE PUBLIC.TYPE_PERSONNE (
                    TYPE_PERSONNE_ID BIGINT PRIMARY KEY,
                    DENOMINATION_TYPE_PERSONNE VARCHAR(100) NOT NULL
                );
                """ + archiveOrphansBackup().replace("TYPE_PERSONNE_ID BIGINT NOT NULL,",
                        "TYPE_PERSONNE_ID BIGINT NOT NULL REFERENCES PUBLIC.TYPE_PERSONNE(TYPE_PERSONNE_ID),"));
        assertThatThrownBy(() -> H2ToSqliteMigrator.importBackup(backup, database, null))
                .isInstanceOf(java.io.IOException.class)
                .hasMessageContaining("relation invalide declaree")
                .hasMessageContaining("PRIX_PLACE.TYPE_PERSONNE_ID");
        assertThat(database).doesNotExist();
        assertThat(stagingFiles()).isEmpty();
    }

    @Test
    void archiveSchemaUpgradeRetainsExistingRowsWithForeignKeysEnabled() throws Exception {
        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + database);
             var statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys=ON");
            executeSchemaScript(statement, "001-schema.sql");
            executeSchemaScript(statement, "002-legacy-tables.sql");
            statement.executeUpdate("INSERT INTO SAISON (SAISON_ID, DATE_DEBUT, DATE_FIN) VALUES ('2025-2026', 0, 0)");
            statement.executeUpdate("INSERT INTO TYPE_COMPETITION VALUES (51, 'Coupe')");
            statement.executeUpdate("INSERT INTO TYPE_MATCH VALUES (61, 51, 'Match')");
            statement.executeUpdate("INSERT INTO TYPE_PERSONNE (TYPE_PERSONNE_ID, DENOMINATION_TYPE_PERSONNE) VALUES (71, 'Adulte')");
            statement.executeUpdate("""
                    INSERT INTO PRIX_PLACE (PRIX_PLACE_ID, BLOC_ID, TYPE_PERSONNE_ID, TYPE_MATCH_ID, MONTANT, SAISON)
                    VALUES (700, 'A1', 71, 61, 19.95, '2025-2026')
                    """);
            connection.setAutoCommit(false);
            executeSchemaScript(statement, "003-legacy-archive-relations.sql");
            connection.commit();
            try (var rows = statement.executeQuery("""
                    SELECT p.PRIX_PLACE_ID, p.MONTANT, m.DENOMINATION_MATCH
                    FROM PRIX_PLACE p JOIN TYPE_MATCH m ON p.TYPE_MATCH_ID=m.TYPE_MATCH_ID
                    """)) {
                assertThat(rows.next()).isTrue();
                assertThat(rows.getLong(1)).isEqualTo(700);
                assertThat(rows.getBigDecimal(2)).isEqualByComparingTo("19.95");
                assertThat(rows.getString(3)).isEqualTo("Match");
            }
            try (var rows = statement.executeQuery("PRAGMA foreign_key_check")) {
                assertThat(rows.next()).isFalse();
            }
        }
    }

    private static void executeSchemaScript(java.sql.Statement statement, String name) throws Exception {
        try (var input = H2ToSqliteMigratorTest.class.getResourceAsStream("/db/changelog/sqlite/" + name)) {
            assertThat(input).isNotNull();
            for (String sql : new String(input.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8).split(";")) {
                if (!sql.isBlank()) statement.execute(sql);
            }
        }
    }

    private static String archiveOrphansBackup() {
        return """
                CREATE CACHED TABLE PUBLIC.TYPE_MATCH (
                    TYPE_MATCH_ID BIGINT PRIMARY KEY,
                    TYPE_COMPETITION_ID BIGINT NOT NULL,
                    DENOMINATION_MATCH VARCHAR(100) NOT NULL
                );
                CREATE CACHED TABLE PUBLIC.PRIX_PLACE (
                    PRIX_PLACE_ID BIGINT PRIMARY KEY,
                    BLOC_ID VARCHAR(100) NOT NULL,
                    TYPE_PERSONNE_ID BIGINT NOT NULL,
                    TYPE_MATCH_ID BIGINT NOT NULL,
                    MONTANT NUMERIC NOT NULL,
                    SAISON VARCHAR(10) NOT NULL
                );
                CREATE CACHED TABLE PUBLIC.COMMANDE_PLACE (
                    COMMANDE_PLACE_ID BIGINT PRIMARY KEY,
                    PERSONNE_ID BIGINT NOT NULL,
                    MATCH_ID BIGINT NOT NULL,
                    DATE_COMMANDE TIMESTAMP NOT NULL,
                    NOMBRE_PLACE INT NOT NULL,
                    BLOC_ID BIGINT,
                    PLACE_COMMANDEE INT NOT NULL
                );
                INSERT INTO PUBLIC.TYPE_MATCH VALUES (61, 997, 'Archive');
                INSERT INTO PUBLIC.PRIX_PLACE VALUES (700, 'A1', 999, 998, 19.95, '1900-1901');
                INSERT INTO PUBLIC.COMMANDE_PLACE VALUES (800, 996, 995, TIMESTAMP '2025-01-01 12:00:00', 2, 994, 1);
                """;
    }

    @Test
    void rejectsExternalResourceFunctionsEvenInsideAnInsertExpression() throws Exception {
        Files.writeString(backup, """
                CREATE CACHED TABLE TEST (ID INTEGER PRIMARY KEY);
                INSERT INTO TEST VALUES (CSVREAD('file:///private/should-not-be-read.csv'));
                """);

        assertThatThrownBy(() -> H2ToSqliteMigrator.importBackup(backup, database, null))
                .isInstanceOf(java.io.IOException.class)
                .hasMessageContaining("instruction externe");
        assertThat(database).doesNotExist();
        assertThat(stagingFiles()).isEmpty();
    }

    @Test
    void rejectsQuotedExternalFunctionsBeforeExecutingTheBackup() throws Exception {
        Files.writeString(backup, """
                CREATE CACHED TABLE PUBLIC.EQUIPE (EQUIPE_ID BIGINT PRIMARY KEY, NOM_EQUIPE VARCHAR(150));
                INSERT INTO PUBLIC.EQUIPE VALUES (1, "FILE_READ"('/private/not-to-be-read'));
                """);
        assertThatThrownBy(() -> H2ToSqliteMigrator.importBackup(backup, database, null))
                .isInstanceOf(java.io.IOException.class)
                .hasMessageContaining("instruction externe");
        assertThat(database).doesNotExist();
        assertThat(stagingFiles()).isEmpty();
    }

    @Test
    void refusesLossOfMonetaryPrecisionInsteadOfPublishingRoundedData() throws Exception {
        Files.writeString(backup, validBackup().replace("12345.67", "12345678901234567.89"));
        assertThatThrownBy(() -> H2ToSqliteMigrator.importBackup(backup, database, null))
                .isInstanceOf(java.io.IOException.class)
                .hasMessageContaining("Valeur alteree")
                .hasMessageContaining("MONTANT_COTISATION");
        assertThat(database).doesNotExist();
        assertThat(stagingFiles()).isEmpty();
    }

    @Test
    void rejectsUnknownLegacyTablesAndInvalidForeignKeysAtomically() throws Exception {
        Files.writeString(backup, """
                CREATE CACHED TABLE PUBLIC.SAISON (
                    SAISON_ID VARCHAR(10) PRIMARY KEY,
                    DATE_DEBUT DATE NOT NULL,
                    DATE_FIN DATE NOT NULL
                );
                CREATE CACHED TABLE PUBLIC.HISTORIQUE_LEGACY (ID INTEGER PRIMARY KEY, OLD_VALUE VARCHAR(80));
                INSERT INTO PUBLIC.SAISON VALUES ('2025', DATE '2025-07-01', DATE '2026-06-30');
                INSERT INTO PUBLIC.HISTORIQUE_LEGACY VALUES (1, 'historique');
                """);

        assertThatThrownBy(() -> H2ToSqliteMigrator.importBackup(backup, database, null))
                .isInstanceOf(java.io.IOException.class)
                .hasMessageContaining("Table H2 non prise en charge");
        assertThat(database).doesNotExist();
        assertThat(stagingFiles()).isEmpty();

        Files.writeString(backup, """
                SET REFERENTIAL_INTEGRITY FALSE;
                CREATE CACHED TABLE PUBLIC.SAISON (
                    SAISON_ID VARCHAR(10) PRIMARY KEY,
                    DATE_DEBUT DATE NOT NULL,
                    DATE_FIN DATE NOT NULL
                );
                CREATE CACHED TABLE PUBLIC.EQUIPE (
                    EQUIPE_ID INTEGER PRIMARY KEY,
                    NOM_EQUIPE VARCHAR(150) NOT NULL
                );
                CREATE CACHED TABLE PUBLIC.SAISON_EQUIPE (
                    SAISON_EQUIPE_ID INTEGER PRIMARY KEY,
                    SAISON_ID VARCHAR(10) NOT NULL REFERENCES PUBLIC.SAISON(SAISON_ID),
                    EQUIPE_ID BIGINT NOT NULL REFERENCES PUBLIC.EQUIPE(EQUIPE_ID)
                );
                INSERT INTO PUBLIC.SAISON VALUES ('2025', DATE '2025-07-01', DATE '2026-06-30');
                INSERT INTO PUBLIC.SAISON_EQUIPE VALUES (4, '2025', 999);
                """);

        assertThatThrownBy(() -> H2ToSqliteMigrator.importBackup(backup, database, null))
                .isInstanceOf(java.io.IOException.class)
                .hasMessageContaining("relation invalide");
        assertThat(database).doesNotExist();
        assertThat(stagingFiles()).isEmpty();
    }

    @Test
    void protectsOccupiedDestinationAndTreatsFirstLaunchCancellationAsCancellation() throws Exception {
        Files.writeString(backup, validBackup());
        Files.writeString(database, "existing database must not be changed");
        String existing = sha256(database);

        assertThatThrownBy(() -> H2ToSqliteMigrator.importBackup(backup, database, null))
                .isInstanceOf(java.io.IOException.class)
                .hasMessageContaining("existe deja");
        assertThat(sha256(database)).isEqualTo(existing);

    }

    @Test
    void failsForUnmappedColumnsRatherThanDiscardingThem() throws Exception {
        Files.writeString(backup, """
                CREATE CACHED TABLE PUBLIC.EQUIPE (
                    EQUIPE_ID INTEGER PRIMARY KEY,
                    NOM_EQUIPE VARCHAR(150) NOT NULL,
                    ARCHIVE_METIER VARCHAR(80)
                );
                INSERT INTO PUBLIC.EQUIPE VALUES (7, 'Equipe', 'valeur historique');
                """);

        assertThatThrownBy(() -> H2ToSqliteMigrator.importBackup(backup, database, null))
                .isInstanceOf(java.io.IOException.class)
                .hasMessageContaining("Colonne H2 non prise en charge");
        assertThat(database).doesNotExist();
        assertThat(stagingFiles()).isEmpty();
    }

    @Test
    void rejectsLegacyColumnTypeChangesBeforeCopying() throws Exception {
        Files.writeString(backup, """
                CREATE CACHED TABLE PUBLIC.EQUIPE (
                    EQUIPE_ID INTEGER PRIMARY KEY,
                    NOM_EQUIPE INTEGER NOT NULL
                );
                INSERT INTO PUBLIC.EQUIPE VALUES (7, 42);
                """);

        assertThatThrownBy(() -> H2ToSqliteMigrator.importBackup(backup, database, null))
                .isInstanceOf(java.io.IOException.class)
                .hasMessageContaining("Types incompatibles");
        assertThat(database).doesNotExist();
        assertThat(stagingFiles()).isEmpty();
    }

    @Test
    void convertsHistoricalNumericPricePositionsToTextWithoutLosingValues() throws Exception {
        Files.writeString(backup, validBackup() + pricePositionsBackup("NUMERIC(38,8)") + """
                INSERT INTO PUBLIC.PRICES VALUES (501, '2025-2026', 100, 'A1', 'CHAMPIONSHIP', 'ABONNEMENT',
                    12.00000000, 34.00000000);
                INSERT INTO PUBLIC.PRICES VALUES (502, '2025-2026', 100, 'A1', 'CHAMPIONSHIP', 'ABONNEMENT',
                    NULL, NULL);
                INSERT INTO PUBLIC.PRICES VALUES (503, '2025-2026', 100, 'A1', 'CHAMPIONSHIP', 'ABONNEMENT',
                    123456789012345678901234567890, 1.12345678);
                """);
        String hash = sha256(backup);
        H2ToSqliteMigrator.importBackup(backup, database, null);
        assertThat(sha256(backup)).isEqualTo(hash);
        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + database);
             var statement = connection.createStatement();
             var rows = statement.executeQuery("SELECT RANG, PLACE, typeof(RANG), typeof(PLACE) FROM PRICES ORDER BY ID")) {
            assertThat(rows.next()).isTrue();
            assertThat(rows.getString("RANG")).isEqualTo("12");
            assertThat(rows.getString("PLACE")).isEqualTo("34");
            assertThat(rows.getString(3)).isEqualTo("text");
            assertThat(rows.getString(4)).isEqualTo("text");
            assertThat(rows.next()).isTrue();
            assertThat(rows.getString("RANG")).isNull();
            assertThat(rows.getString("PLACE")).isNull();
            assertThat(rows.next()).isTrue();
            assertThat(rows.getString("RANG")).isEqualTo("123456789012345678901234567890");
            assertThat(rows.getString("PLACE")).isEqualTo("1.12345678");
            assertThat(rows.next()).isFalse();
        }
        assertThat(stagingFiles()).isEmpty();
    }

    @Test
    void retainsAlreadyTextualPricePositionsIncludingLeadingZeroes() throws Exception {
        Files.writeString(backup, validBackup() + pricePositionsBackup("VARCHAR(50)") + """
                INSERT INTO PUBLIC.PRICES VALUES (501, '2025-2026', 100, 'A1', 'CHAMPIONSHIP', 'ABONNEMENT',
                    '0012', '34B');
                """);
        H2ToSqliteMigrator.importBackup(backup, database, null);
        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + database);
             var statement = connection.createStatement();
             var rows = statement.executeQuery("SELECT RANG, PLACE FROM PRICES WHERE ID=501")) {
            assertThat(rows.next()).isTrue();
            assertThat(rows.getString("RANG")).isEqualTo("0012");
            assertThat(rows.getString("PLACE")).isEqualTo("34B");
        }
        assertThat(stagingFiles()).isEmpty();
    }

    private static String pricePositionsBackup(String type) {
        return """
                CREATE CACHED TABLE PUBLIC.PRICES (
                    ID BIGINT PRIMARY KEY,
                    SAISON_ID VARCHAR(10) NOT NULL REFERENCES PUBLIC.SAISON(SAISON_ID),
                    PRIX BIGINT NOT NULL,
                    BLOC VARCHAR(50) NOT NULL,
                    TYPE_COMPETITION VARCHAR(50) NOT NULL,
                    PRICE_TYPE VARCHAR(20) NOT NULL,
                    RANG %s,
                    PLACE %s
                );
                """.formatted(type, type);
    }

    private String validBackup() {
        return """
                -- H2 2.3.232
                SET LOCK_MODE 3;
                CREATE CACHED TABLE PUBLIC.SAISON (
                    SAISON_ID VARCHAR(10) NOT NULL PRIMARY KEY,
                    DATE_DEBUT DATE NOT NULL,
                    DATE_FIN DATE NOT NULL,
                    EUROPEENS BOOLEAN,
                    MONTANT_COTISATION DECIMAL(19,2)
                );
                CREATE CACHED TABLE PUBLIC.EQUIPE (
                    EQUIPE_ID DECIMAL(0) NOT NULL PRIMARY KEY,
                    NOM_EQUIPE VARCHAR(150) NOT NULL
                );
                CREATE CACHED TABLE PUBLIC.SAISON_EQUIPE (
                    SAISON_EQUIPE_ID DECIMAL(0) NOT NULL PRIMARY KEY,
                    SAISON_ID VARCHAR(10) NOT NULL REFERENCES PUBLIC.SAISON(SAISON_ID),
                    EQUIPE_ID BIGINT NOT NULL REFERENCES PUBLIC.EQUIPE(EQUIPE_ID)
                );
                CREATE CACHED TABLE PUBLIC.PERSONNES (
                    PERSONNE_ID DECIMAL(0) NOT NULL PRIMARY KEY,
                    NOM VARCHAR(100) NOT NULL,
                    PRENOM VARCHAR(100),
                    DATE_NAISSANCE DATE,
                    NUMERO_MEMBRE BIGINT,
                    ROBOTICKET_ID VARCHAR(50)
                );
                CREATE CACHED TABLE PUBLIC.BENEVOLAT (
                    BENEVOLAT_ID DECIMAL(0) NOT NULL PRIMARY KEY,
                    PERSONNE_ID BIGINT NOT NULL REFERENCES PUBLIC.PERSONNES(PERSONNE_ID),
                    MONTANT NUMBER(19,2),
                    DATE_BENEVOLAT DATE
                );
                CREATE CACHED TABLE PUBLIC.ACCOUNTING (
                    ID DECIMAL(0) NOT NULL PRIMARY KEY,
                    ACCOUNTING_DATE DATE,
                    DESCRIPTION VARCHAR(256),
                    TYPE VARCHAR(5),
                    AMOUNT DECIMAL(19,2)
                );
                CREATE CACHED TABLE PUBLIC.APP_USERS (
                    USER_ID DECIMAL(0) NOT NULL PRIMARY KEY,
                    USERNAME VARCHAR(50) NOT NULL,
                    PASSWORD VARCHAR(255) NOT NULL,
                    ENABLED BOOLEAN NOT NULL,
                    CREATED_AT TIMESTAMP,
                    UPDATED_AT TIMESTAMP
                );
                CREATE CACHED TABLE PUBLIC.APP_USER_ROLES (
                    USER_ID BIGINT NOT NULL REFERENCES PUBLIC.APP_USERS(USER_ID),
                    ROLE VARCHAR(50) NOT NULL,
                    PRIMARY KEY (USER_ID, ROLE)
                );
                INSERT INTO PUBLIC.SAISON VALUES ('2025-2026', DATE '2025-07-01', DATE '2026-06-30', TRUE, 12345.67);
                INSERT INTO PUBLIC.EQUIPE VALUES (900, 'Equipe legacy');
                INSERT INTO PUBLIC.SAISON_EQUIPE VALUES (41, '2025-2026', 900);
                INSERT INTO PUBLIC.PERSONNES VALUES (73, 'Historique', 'Membre', DATE '1980-02-29', 7001, 'ticket-old');
                INSERT INTO PUBLIC.BENEVOLAT VALUES (5, 73, 15.25, DATE '2025-08-03');
                INSERT INTO PUBLIC.ACCOUNTING VALUES (13, DATE '2025-08-04', 'Historique comptable', 'ENTRY', 15.25);
                INSERT INTO PUBLIC.APP_USERS VALUES (88, 'ancien-admin', 'bcrypt-value', TRUE,
                    TIMESTAMP '2025-08-01 10:11:12', TIMESTAMP '2025-08-01 10:11:12');
                INSERT INTO PUBLIC.APP_USER_ROLES VALUES (88, 'ROLE_ADMIN');
                """;
    }

    private java.util.List<Path> stagingFiles() throws Exception {
        try (var files = Files.list(work)) {
            return files.filter(file -> file.getFileName().toString().startsWith(".standardnaast-")).toList();
        }
    }

    private static String sha256(Path file) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(file)));
    }

    private static String authenticatedGet(HttpClient client, String url, String path, String token) throws Exception {
        var response = client.send(HttpRequest.newBuilder(URI.create(url + path)).timeout(Duration.ofSeconds(10))
                .header("Authorization", "Bearer " + token).GET().build(), HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        return response.body();
    }

    private static String matchBackup() {
        return """
                CREATE CACHED TABLE PUBLIC.MATCH (
                    MATCH_ID BIGINT PRIMARY KEY,
                    SAISON_ID VARCHAR(10) NOT NULL REFERENCES PUBLIC.SAISON(SAISON_ID),
                    OPPONENT BIGINT NOT NULL REFERENCES PUBLIC.EQUIPE(EQUIPE_ID),
                    SAISON_EQUIPE_ID BIGINT REFERENCES PUBLIC.SAISON_EQUIPE(SAISON_EQUIPE_ID),
                    DATE_MATCH TIMESTAMP,
                    PLACE VARCHAR(50) NOT NULL,
                    COMPETITION_TYPE VARCHAR(16) NOT NULL
                );
                INSERT INTO PUBLIC.MATCH VALUES (101, '2025-2026', 900, 41,
                    TIMESTAMP '2025-10-26 18:30:15.123', 'HOME', 'CHAMPIONSHIP');
                """;
    }
}
