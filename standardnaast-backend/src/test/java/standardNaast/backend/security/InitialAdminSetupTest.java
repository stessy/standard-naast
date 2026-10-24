package standardNaast.backend.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class InitialAdminSetupTest {
    @Test
    void rejectsInvalidCredentialsBeforeAccessingDatabase() {
        Connection connection = mock(Connection.class);
        assertThatThrownBy(() -> InitialAdminSetup.createAdmin(connection, "", "test-password-long"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> InitialAdminSetup.createAdmin(connection, "admin", "1234567"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> InitialAdminSetup.createAdmin(connection, "admin", "é".repeat(37)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> InitialAdminSetup.createAdmin(connection, "admin\nother", "test-password-long"))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(connection);
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "MIGRATION_TEST_PG_URL", matches = ".+")
    void createsFirstAdminWithBcryptAndNeverOverwritesExistingAccount() throws Exception {
        String adminUrl = System.getenv("MIGRATION_TEST_PG_URL");
        String user = System.getProperty("user.name");
        String database = "naast_admin_" + UUID.randomUUID().toString().replace("-", "");
        try (Connection admin = DriverManager.getConnection(adminUrl, user, "");
             var adminStatement = admin.createStatement()) {
            adminStatement.execute("CREATE DATABASE " + database);
            try {
                String url = adminUrl.substring(0, adminUrl.lastIndexOf('/') + 1) + database;
                try (Connection connection = DriverManager.getConnection(url, user, "");
                     var statement = connection.createStatement()) {
                    statement.execute("CREATE SEQUENCE user_seq START WITH 1000");
                    statement.execute("""
                            CREATE TABLE app_users (user_id BIGINT PRIMARY KEY, username TEXT UNIQUE NOT NULL,
                                password TEXT NOT NULL, enabled BOOLEAN NOT NULL,
                                created_at TIMESTAMP, updated_at TIMESTAMP)
                            """);
                    statement.execute("CREATE TABLE app_user_roles "
                            + "(user_id BIGINT REFERENCES app_users(user_id), role TEXT NOT NULL)");
                    assertThat(InitialAdminSetup.hasUsers(connection)).isFalse();
                    InitialAdminSetup.createAdmin(connection, "andré", "12345678");
                    assertThat(InitialAdminSetup.hasUsers(connection)).isTrue();
                    try (var rows = statement.executeQuery(
                            "SELECT u.username, u.password, u.enabled, r.role FROM app_users u "
                                    + "JOIN app_user_roles r ON r.user_id = u.user_id")) {
                        assertThat(rows.next()).isTrue();
                        assertThat(rows.getString(1)).isEqualTo("andré");
                        assertThat(new BCryptPasswordEncoder().matches("12345678", rows.getString(2))).isTrue();
                        assertThat(rows.getBoolean(3)).isTrue();
                        assertThat(rows.getString(4)).isEqualTo("ROLE_ADMIN");
                        assertThat(rows.next()).isFalse();
                    }
                    assertThatThrownBy(() -> InitialAdminSetup.createAdmin(connection, "other", "another-test-password"))
                            .isInstanceOf(IllegalStateException.class);
                    try (var rows = statement.executeQuery("SELECT count(*) FROM app_users")) {
                        rows.next();
                        assertThat(rows.getInt(1)).isEqualTo(1);
                    }
                }
            } finally {
                adminStatement.execute("DROP DATABASE " + database + " WITH (FORCE)");
            }
        }
    }
}
