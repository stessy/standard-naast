package standardNaast.backend.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Local desktop provisioning command. Credentials are read from stdin, never command arguments.
 */
public final class InitialAdminSetup {
    private InitialAdminSetup() {}

    public static void run(boolean create) {
        try (Connection connection = DriverManager.getConnection(
                System.getenv("SPRING_DATASOURCE_URL"),
                System.getenv("SPRING_DATASOURCE_USERNAME"),
                System.getenv("SPRING_DATASOURCE_PASSWORD"))) {
            if (!create) {
                System.out.println(hasUsers(connection) ? "ADMIN_SETUP:EXISTS" : "ADMIN_SETUP:REQUIRED");
                return;
            }
            var input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            String username = input.readLine();
            String password = input.readLine();
            createAdmin(connection, username, password);
            System.out.println("ADMIN_SETUP:CREATED");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Impossible d'initialiser le compte administrateur (" + e.getClass().getSimpleName() + ").");
            System.exit(1);
        }
    }

    static boolean hasUsers(Connection connection) throws SQLException {
        try (var statement = connection.createStatement();
             var rows = statement.executeQuery("SELECT EXISTS (SELECT 1 FROM public.app_users)")) {
            rows.next();
            return rows.getBoolean(1);
        }
    }

    static void createAdmin(Connection connection, String username, String password) throws SQLException {
        if (username == null || username.isBlank() || username.strip().length() < 3
                || username.strip().length() > 50 || username.contains("\n") || username.contains("\r")) {
            throw new IllegalArgumentException("L'identifiant doit comporter entre 3 et 50 caracteres.");
        }
        if (password == null || password.length() < 8
                || password.getBytes(StandardCharsets.UTF_8).length > 72
                || password.contains("\n") || password.contains("\r")) {
            throw new IllegalArgumentException("Le mot de passe doit comporter au moins 8 caracteres et au plus 72 octets UTF-8.");
        }
        String hash = new BCryptPasswordEncoder().encode(password);
        connection.setAutoCommit(false);
        try {
            try (var statement = connection.createStatement()) {
                statement.execute("LOCK TABLE public.app_users IN EXCLUSIVE MODE");
            }
            if (hasUsers(connection)) {
                throw new IllegalStateException("Un compte existe deja. Aucun compte n'a ete modifie.");
            }
            long id;
            try (var statement = connection.prepareStatement("""
                    INSERT INTO public.app_users
                        (user_id, username, password, enabled, created_at, updated_at)
                    VALUES (nextval('public.user_seq'), ?, ?, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                    RETURNING user_id
                    """)) {
                statement.setString(1, username.strip());
                statement.setString(2, hash);
                try (var rows = statement.executeQuery()) {
                    rows.next();
                    id = rows.getLong(1);
                }
            }
            try (var statement = connection.prepareStatement(
                    "INSERT INTO public.app_user_roles (user_id, role) VALUES (?, 'ROLE_ADMIN')")) {
                statement.setLong(1, id);
                statement.executeUpdate();
            }
            connection.commit();
        } catch (SQLException | RuntimeException e) {
            connection.rollback();
            throw e;
        }
    }
}
