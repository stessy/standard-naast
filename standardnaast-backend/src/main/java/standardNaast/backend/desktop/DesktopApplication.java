package standardNaast.backend.desktop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import standardNaast.backend.StandardNaastApplication;
import standardNaast.backend.security.InitialAdminSetup;

import javax.sql.DataSource;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Locale;
import java.util.Properties;
import javax.imageio.ImageIO;

public final class DesktopApplication {
    public static final int PORT = 8080;

    private DesktopApplication() {}

    static BufferedImage loadIcon() throws IOException {
        try (var input = DesktopApplication.class.getResourceAsStream("/desktop/club-icon.png")) {
            if (input == null) {
                throw new IOException("Icone de l'application introuvable.");
            }
            BufferedImage image = ImageIO.read(input);
            if (image == null) {
                throw new IOException("Icone de l'application invalide.");
            }
            return image;
        }
    }

    public static Path dataDirectory(String os, Path home, String localAppData) {
        if (os.toLowerCase(Locale.ROOT).contains("mac")) {
            return home.resolve("Library/Application Support/Standard Naast");
        }
        if (os.toLowerCase(Locale.ROOT).contains("win")) {
            return (localAppData == null || localAppData.isBlank()
                    ? home.resolve("AppData/Local") : Path.of(localAppData)).resolve("Standard Naast");
        }
        return home.resolve(".local/share/standardnaast");
    }

    public static void launch(String[] args) {
        Path directory = Arrays.stream(args).filter(arg -> arg.startsWith("--desktop-data-dir="))
                .map(arg -> Path.of(arg.substring(arg.indexOf('=') + 1))).findFirst()
                .orElseGet(() -> dataDirectory(System.getProperty("os.name"),
                        Path.of(System.getProperty("user.home")), System.getenv("LOCALAPPDATA")))
                .toAbsolutePath().normalize();
        boolean stop = Arrays.asList(args).contains("--desktop-stop");
        boolean noBrowser = Arrays.asList(args).contains("--desktop-no-browser");
        try {
            Files.createDirectories(directory);
            if (Files.getFileStore(directory).supportsFileAttributeView("posix")) {
                Files.setPosixFilePermissions(directory,
                        java.nio.file.attribute.PosixFilePermissions.fromString("rwx------"));
            }
            System.setProperty("LOG_DIR", directory.resolve("logs").toString());
            System.setProperty("java.awt.headless", Boolean.toString(noBrowser));
            if (stop) {
                stopExisting(directory);
                return;
            }
            var channel = FileChannel.open(directory.resolve("desktop.lock"),
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            FileLock lock = channel.tryLock();
            if (lock == null) {
                channel.close();
                Properties running = readProperties(directory.resolve("desktop.properties"));
                if (!noBrowser) {
                    browse(URI.create(running.getProperty("url")));
                }
                return;
            }
            var session = new Session(directory, channel, lock);
            Runtime.getRuntime().addShutdownHook(new Thread(session::close, "desktop-cleanup"));
            try {
                Files.deleteIfExists(directory.resolve("desktop.properties"));
                System.setProperty("app.desktop.data-dir", directory.toString().replace('\\', '/'));
                System.setProperty("app.jwt.secret", secret(directory.resolve("jwt.secret")));
                var application = new SpringApplication(StandardNaastApplication.class);
                application.addListeners(event -> {
                    if (event instanceof ApplicationReadyEvent ready) {
                        try {
                            session.ready(ready, noBrowser);
                        } catch (IOException | java.sql.SQLException | AWTException e) {
                            throw new IllegalStateException("Initialisation desktop impossible", e);
                        }
                    }
                });
                var arguments = new ArrayList<>(Arrays.asList(args));
                arguments.add("--spring.profiles.active=desktop");
                arguments.add("--server.address=127.0.0.1");
                arguments.add("--server.port=" + PORT);
                application.run(arguments.toArray(String[]::new));
            } catch (RuntimeException e) {
                session.close();
                throw e;
            }
        } catch (IOException | InterruptedException | RuntimeException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            org.slf4j.LoggerFactory.getLogger(DesktopApplication.class)
                    .error("Lancement desktop impossible. Donnees : {}", directory, e);
            if (!noBrowser && !GraphicsEnvironment.isHeadless()) {
                String detail = e.getMessage();
                for (Throwable cause = e; cause != null; cause = cause.getCause()) {
                    if (cause instanceof org.springframework.boot.web.server.PortInUseException) {
                        detail = "Le port " + PORT + " est deja utilise. Fermez le programme qui l'utilise, puis relancez Standard Naast.";
                        break;
                    }
                }
                JOptionPane.showMessageDialog(null,
                        "Impossible de lancer Standard Naast.\n" + detail
                                + "\nConsultez les journaux dans : " + directory.resolve("logs"),
                        "Standard Naast", JOptionPane.ERROR_MESSAGE);
            }
            throw new IllegalStateException("Lancement desktop impossible", e);
        }
    }

    private static Properties readProperties(Path file) throws IOException {
        var properties = new Properties();
        try (var input = Files.newInputStream(file)) {
            properties.load(input);
        }
        if (properties.getProperty("url") == null || properties.getProperty("token") == null) {
            throw new IOException("Instance en cours de demarrage ou fichier d'instance incomplet.");
        }
        return properties;
    }

    private static void stopExisting(Path directory) throws IOException, InterruptedException {
        Properties running = readProperties(directory.resolve("desktop.properties"));
        var request = HttpRequest.newBuilder(URI.create(running.getProperty("url") + "api/desktop/stop"))
                .timeout(Duration.ofSeconds(10))
                .header("X-Desktop-Token", running.getProperty("token"))
                .POST(HttpRequest.BodyPublishers.noBody()).build();
        try (var client = HttpClient.newHttpClient()) {
            var response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 202) {
                throw new IOException("Arret refuse : HTTP " + response.statusCode());
            }
        }
    }

    private static String secret(Path file) throws IOException {
        if (Files.exists(file)) {
            String value = Files.readString(file).strip();
            if (value.length() < 64) {
                throw new IOException("Secret JWT invalide : " + file);
            }
            return value;
        }
        String value = randomToken();
        Files.writeString(file, value, StandardOpenOption.CREATE_NEW);
        if (Files.getFileStore(file).supportsFileAttributeView("posix")) {
            Files.setPosixFilePermissions(file, java.nio.file.attribute.PosixFilePermissions.fromString("rw-------"));
        }
        return value;
    }

    private static String randomToken() {
        byte[] bytes = new byte[48];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static void browse(URI uri) throws IOException {
        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            throw new IOException("Ouverture du navigateur indisponible. Adresse : " + uri);
        }
        Desktop.getDesktop().browse(uri);
    }

    private static final class Session {
        private final Path directory;
        private final FileChannel channel;
        private final FileLock lock;
        private TrayIcon tray;
        private boolean closed;

        private Session(Path directory, FileChannel channel, FileLock lock) {
            this.directory = directory;
            this.channel = channel;
            this.lock = lock;
        }

        private void ready(ApplicationReadyEvent event, boolean noBrowser) throws IOException, java.sql.SQLException, AWTException {
            var context = event.getApplicationContext();
            int port = ((WebServerApplicationContext) context).getWebServer().getPort();
            URI uri = URI.create("http://127.0.0.1:" + port + "/");
            String token = randomToken();
            context.getBean(DesktopControl.class).initialize(token);
            boolean provisioned = true;
            if (!noBrowser) {
                try (var connection = context.getBean(DataSource.class).getConnection()) {
                    provisioned = InitialAdminSetup.hasUsers(connection) || provision(connection);
                }
            }
            if (!provisioned) {
                context.close();
                close();
                return;
            }
            var properties = new Properties();
            properties.setProperty("url", uri.toString());
            properties.setProperty("token", token);
            try (var output = Files.newOutputStream(directory.resolve("desktop.properties"))) {
                properties.store(output, "Standard Naast - instance locale");
            }
            if (Files.getFileStore(directory).supportsFileAttributeView("posix")) {
                Files.setPosixFilePermissions(directory.resolve("desktop.properties"),
                        java.nio.file.attribute.PosixFilePermissions.fromString("rw-------"));
            }
            org.slf4j.LoggerFactory.getLogger(DesktopApplication.class).info("DESKTOP_READY {}", uri);
            if (!noBrowser) {
                if (SystemTray.isSupported()) {
                    var image = loadIcon();
                    var menu = new PopupMenu();
                    var open = new MenuItem("Ouvrir Standard Naast");
                    open.addActionListener(action -> {
                        try {
                            browse(uri);
                        } catch (IOException e) {
                            org.slf4j.LoggerFactory.getLogger(DesktopApplication.class).error("Navigateur indisponible : {}", uri, e);
                            JOptionPane.showMessageDialog(null, "Ouvrez cette adresse : " + uri);
                        }
                    });
                    menu.add(open);
                    var quit = new MenuItem("Quitter Standard Naast");
                    quit.addActionListener(action -> new Thread(() -> {
                        context.close();
                        close();
                    }, "desktop-stop").start());
                    menu.add(quit);
                    tray = new TrayIcon(image, "Standard Naast", menu);
                    tray.setImageAutoSize(true);
                    SystemTray.getSystemTray().add(tray);
                }
                browse(uri);
            }
            context.addApplicationListener(eventToHandle -> {
                if (eventToHandle instanceof org.springframework.context.event.ContextClosedEvent) {
                    close();
                }
            });
        }

        private boolean provision(java.sql.Connection connection) throws java.sql.SQLException {
            while (true) {
                var username = new JTextField(20);
                var password = new JPasswordField(20);
                var panel = new JPanel(new GridLayout(0, 1, 4, 4));
                panel.add(new JLabel("Creation du premier administrateur"));
                panel.add(new JLabel("Identifiant (3 a 50 caracteres)"));
                panel.add(username);
                panel.add(new JLabel("Mot de passe (8 caracteres minimum, 72 octets maximum)"));
                panel.add(password);
                if (JOptionPane.showConfirmDialog(null, panel, "Standard Naast",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
                    return false;
                }
                char[] characters = password.getPassword();
                try {
                    InitialAdminSetup.createAdmin(connection, username.getText(), new String(characters));
                    return true;
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Identifiants invalides", JOptionPane.WARNING_MESSAGE);
                } finally {
                    Arrays.fill(characters, '\0');
                    password.setText("");
                }
            }
        }

        private synchronized void close() {
            if (closed) {
                return;
            }
            closed = true;
            if (tray != null) {
                SystemTray.getSystemTray().remove(tray);
            }
            try {
                Files.deleteIfExists(directory.resolve("desktop.properties"));
                lock.release();
                channel.close();
            } catch (IOException e) {
                org.slf4j.LoggerFactory.getLogger(DesktopApplication.class).error("Nettoyage de l'instance desktop impossible", e);
            }
        }
    }
}
