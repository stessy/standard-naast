package standardNaast.backend.migration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

class H2ToPostgresMigratorTest {

    @TempDir
    Path tempDir;

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
            try (Connection conn = DriverManager.getConnection(url, "sa", "")) {
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC'")) {
                    int count = 0;
                    while (rs.next()) {
                        count++;
                    }
                    assertThat(count).isGreaterThan(0);
                }
            }
        }
    }
}
