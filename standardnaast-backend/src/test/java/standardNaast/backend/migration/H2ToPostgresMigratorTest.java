package standardNaast.backend.migration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class H2ToPostgresMigratorTest {

    @TempDir
    Path tempDir;

    @Test
    void detectsAndRestoresLegacyH2SqlBackup() throws Exception {
        Path backup = tempDir.resolve("backup_without_extension");
        Files.writeString(backup, """
                CREATE SEQUENCE TEST_SEQ START WITH 8;
                CREATE MEMORY TABLE LEGACY_TEST (ID DECIMAL(0), AMOUNT NUMBER);
                INSERT INTO LEGACY_TEST VALUES (7, 12);
                """);

        assertThat(H2ToPostgresMigrator.isH2SqlScript(backup.toString())).isTrue();
        String restoredUrl = new H2ToPostgresMigrator()
                .restoreH2Backup(backup, tempDir.resolve("restored").toString());

        try (Connection restored = DriverManager.getConnection(restoredUrl, "sa", "");
             Statement statement = restored.createStatement();
             ResultSet rows = statement.executeQuery("SELECT ID, AMOUNT FROM LEGACY_TEST")) {
            assertThat(rows.next()).isTrue();
            assertThat(rows.getBigDecimal("ID").intValueExact()).isEqualTo(7);
            assertThat(rows.getBigDecimal("AMOUNT").intValueExact()).isEqualTo(12);
            assertThat(rows.next()).isFalse();
        }
    }

    @Test
    void doesNotDetectH2MvDbAsSqlBackup() throws Exception {
        Path databaseBase = tempDir.resolve("binary_h2");
        try (Connection connection = DriverManager.getConnection(
                "jdbc:h2:file:" + databaseBase + ";DB_CLOSE_DELAY=0", "sa", "");
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE H2_BINARY_TEST (ID INTEGER)");
        }

        assertThat(H2ToPostgresMigrator.isH2SqlScript(databaseBase + ".mv.db")).isFalse();
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "MIGRATION_TEST_PG_URL", matches = ".+")
    void migratesMixedNumericForeignKeysAndQuotedSequencesToPostgres() throws Exception {
        String sourceUrl = "jdbc:h2:mem:fk_migration;DB_CLOSE_DELAY=-1";
        try (Connection source = DriverManager.getConnection(sourceUrl, "sa", "");
             Statement stmt = source.createStatement()) {
            stmt.execute("CREATE SEQUENCE MATCH_SEQ START WITH 50");
            stmt.execute("CREATE TABLE PARENT (ID BIGINT PRIMARY KEY)");
            stmt.execute("CREATE TABLE CHILD (ID NUMERIC PRIMARY KEY REFERENCES PARENT(ID))");
            stmt.execute("CREATE TABLE GRANDCHILD (ID BIGINT PRIMARY KEY REFERENCES CHILD(ID))");
            stmt.execute("INSERT INTO PARENT VALUES (9223372036854775807)");
            stmt.execute("INSERT INTO CHILD VALUES (9223372036854775807)");
            stmt.execute("INSERT INTO GRANDCHILD VALUES (9223372036854775807)");
        }
        String pgUrl = System.getenv("MIGRATION_TEST_PG_URL");
        String pgUser = System.getProperty("user.name");
        H2ToPostgresMigrator.MigrationResult result = new H2ToPostgresMigrator().migrate(
                new H2ToPostgresMigrator.MigrationConfig(sourceUrl)
                        .setH2Password("").setPgUrl(pgUrl).setPgUser(pgUser).setPgPassword(""));

        assertThat(result.isSuccess()).as(result.toString()).isTrue();
        assertThat(result.getWarnings()).isEmpty();
        assertThat(result.getTotalRowsMigrated()).isEqualTo(3);
        try (Connection target = DriverManager.getConnection(pgUrl, pgUser, "");
             Statement stmt = target.createStatement()) {
            try (Connection source = DriverManager.getConnection(sourceUrl, "sa", "")) {
                H2ToPostgresMigrator migrator = new H2ToPostgresMigrator();
                List<?> sequences = ReflectionTestUtils.invokeMethod(migrator, "extractSequences", source, result);
                ReflectionTestUtils.invokeMethod(migrator, "createSequencesInPostgres", target, sequences, result);
            }
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM pg_constraint WHERE contype = 'f'")) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getInt(1)).isEqualTo(2);
            }
            try (ResultSet rs = stmt.executeQuery("SELECT \"ID\" FROM \"GRANDCHILD\"")) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getBigDecimal(1).toBigIntegerExact()).isEqualTo(java.math.BigInteger.valueOf(Long.MAX_VALUE));
            }
            try (ResultSet rs = stmt.executeQuery("SELECT nextval('\"MATCH_SEQ\"')")) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getLong(1)).isEqualTo(51);
            }
        }
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "MIGRATION_TEST_PG_URL", matches = ".+")
    void rollsBackFailedForeignKeyAndSequenceWithoutAbortingNextOperations() throws Exception {
        H2ToPostgresMigrator migrator = new H2ToPostgresMigrator();
        H2ToPostgresMigrator.MigrationResult result = new H2ToPostgresMigrator.MigrationResult();
        try (Connection source = DriverManager.getConnection("jdbc:h2:mem:savepoint_test", "sa", "");
             Connection target = DriverManager.getConnection(
                     System.getenv("MIGRATION_TEST_PG_URL"), System.getProperty("user.name"), "");
             Statement sourceStmt = source.createStatement();
             Statement targetStmt = target.createStatement()) {
            sourceStmt.execute("CREATE TABLE FK_PARENT (ID BIGINT PRIMARY KEY)");
            sourceStmt.execute("CREATE TABLE FK_BAD (ID BIGINT REFERENCES FK_PARENT(ID))");
            sourceStmt.execute("CREATE TABLE FK_GOOD (ID BIGINT REFERENCES FK_PARENT(ID))");
            sourceStmt.execute("CREATE SEQUENCE MISSING_SEQ START WITH 10");
            sourceStmt.execute("CREATE SEQUENCE WORKING_SEQ START WITH 10");
            target.setAutoCommit(false);
            targetStmt.execute("CREATE TABLE \"FK_PARENT\" (\"ID\" BIGINT PRIMARY KEY)");
            targetStmt.execute("CREATE TABLE \"FK_BAD\" (\"ID\" BIGINT)");
            targetStmt.execute("CREATE TABLE \"FK_GOOD\" (\"ID\" BIGINT)");
            targetStmt.execute("INSERT INTO \"FK_BAD\" VALUES (1)");
            targetStmt.execute("CREATE SEQUENCE \"WORKING_SEQ\"");
            List<?> bad = ReflectionTestUtils.invokeMethod(migrator, "extractForeignKeys", source, "FK_BAD");
            List<?> good = ReflectionTestUtils.invokeMethod(migrator, "extractForeignKeys", source, "FK_GOOD");
            ReflectionTestUtils.invokeMethod(migrator, "applyForeignKeysInPostgres", target, bad, result);
            ReflectionTestUtils.invokeMethod(migrator, "applyForeignKeysInPostgres", target, good, result);
            List<?> sequences = ReflectionTestUtils.invokeMethod(migrator, "extractSequences", source, result);
            ReflectionTestUtils.invokeMethod(migrator, "syncPostgresSequences", target, sequences, Map.of(), result);
            target.commit();

            assertThat(result.getWarnings()).hasSize(2)
                    .anyMatch(w -> w.contains("FK ignorée"))
                    .anyMatch(w -> w.contains("MISSING_SEQ"))
                    .noneMatch(w -> w.contains("transaction is aborted"));
            try (ResultSet rs = targetStmt.executeQuery(
                    "SELECT COUNT(*) FROM pg_constraint WHERE conrelid = '\"FK_GOOD\"'::regclass AND contype = 'f'")) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getInt(1)).isEqualTo(1);
            }
            try (ResultSet rs = targetStmt.executeQuery("SELECT nextval('\"WORKING_SEQ\"')")) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getLong(1)).isEqualTo(11);
            }
        }
    }

    @Test
    void mapsH2NumericPrecisionToPostgresWithoutClamping() throws Exception {
        try (Connection source = DriverManager.getConnection("jdbc:h2:mem:numeric_mapping", "sa", "");
             Statement statement = source.createStatement()) {
            statement.execute("CREATE TABLE NUMERIC_TEST ("
                    + "UNBOUNDED NUMERIC, WIDE DECIMAL(100000, 4), "
                    + "BOUNDARY NUMERIC(1000, 3), OVER_LIMIT NUMERIC(1001, 2), "
                    + "PRICE DECIMAL(19, 2))");

            H2ToPostgresMigrator migrator = new H2ToPostgresMigrator();
            List<?> columns = ReflectionTestUtils.invokeMethod(migrator, "extractColumns", source, "NUMERIC_TEST");
            Connection target = mock(Connection.class);
            Statement targetStatement = mock(Statement.class);
            when(target.createStatement()).thenReturn(targetStatement);
            when(targetStatement.execute(anyString())).thenReturn(false);

            ReflectionTestUtils.invokeMethod(migrator, "createTableInPostgres",
                    target, "NUMERIC_TEST", columns, List.of(), false);

            verify(targetStatement).execute("""
                    CREATE TABLE IF NOT EXISTS "NUMERIC_TEST" (
                        "UNBOUNDED" NUMERIC,
                        "WIDE" NUMERIC,
                        "BOUNDARY" NUMERIC(1000, 3),
                        "OVER_LIMIT" NUMERIC,
                        "PRICE" NUMERIC(19, 2))
                    """.stripTrailing());
        }
    }

    @Test
    void testH2IntrospectionAndStructure() throws Exception {
        // Créer une base de données H2 de test
        String h2DbPath = tempDir.resolve("testdb").toAbsolutePath().toString();
        String h2Url = "jdbc:h2:file:" + h2DbPath + ";DB_CLOSE_DELAY=-1";

        try (Connection conn = DriverManager.getConnection(h2Url, "sa", "")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE SEQUENCE TEST_SEQ START WITH 500 INCREMENT BY 1");
                stmt.execute("CREATE TABLE TEST_PARENT (ID BIGINT PRIMARY KEY, NAME VARCHAR(100) NOT NULL)");
                stmt.execute("CREATE TABLE TEST_CHILD (CHILD_ID BIGINT PRIMARY KEY, PARENT_ID BIGINT, INFO VARCHAR(255), FOREIGN KEY (PARENT_ID) REFERENCES TEST_PARENT(ID))");

                stmt.execute("INSERT INTO TEST_PARENT VALUES (1, 'Parent 1')");
                stmt.execute("INSERT INTO TEST_PARENT VALUES (2, 'Parent 2')");
                stmt.execute("INSERT INTO TEST_CHILD VALUES (10, 1, 'Child 1-1')");
                stmt.execute("INSERT INTO TEST_CHILD VALUES (20, 2, 'Child 2-1')");
            }
        }

        // Vérifier que la base H2 existe et contient bien les enregistrements
        try (Connection conn = DriverManager.getConnection(h2Url, "sa", "")) {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM TEST_PARENT")) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getInt(1)).isEqualTo(2);
            }
        }
    }

    @Test
    void testExistingProjectH2DatabaseIfPresent() throws Exception {
        File h2File = new File("data/standardnaast.mv.db");
        if (!h2File.exists()) {
            h2File = new File("../data/standardnaast.mv.db");
        }

        if (h2File.exists()) {
            String path = h2File.getAbsolutePath();
            if (path.endsWith(".mv.db")) {
                path = path.substring(0, path.length() - ".mv.db".length());
            }
            String url = "jdbc:h2:file:" + path + ";DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE;NON_KEYWORDS=VALUE,USER,ROLE";
            Connection conn = null;
            try {
                try {
                    conn = DriverManager.getConnection(url, "sa", "standard");
                } catch (Exception ex) {
                    conn = DriverManager.getConnection(url, "sa", "");
                }
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC'")) {
                    int count = 0;
                    while (rs.next()) {
                        count++;
                    }
                    assertThat(count).isGreaterThan(0);
                }
            } finally {
                if (conn != null) conn.close();
            }
        }
    }
}
