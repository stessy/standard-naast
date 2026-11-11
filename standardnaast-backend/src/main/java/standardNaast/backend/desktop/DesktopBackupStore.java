package standardNaast.backend.desktop;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

final class DesktopBackupStore {
    record Settings(String backupDirectory, String logsDirectory) {}
    record Version(String id, Instant created, List<String> parents, Path file) {
        @Override public String toString() { return created + " - " + file.getFileName(); }
    }
    interface RestoreDecision {
        // null keeps the local data; cancellation must throw CancellationException.
        Version choose(List<Version> versions, boolean dirty) throws IOException;
    }

    private final Path directory;
    private final Properties state;
    private Settings settings;
    private List<String> parents = List.of();

    DesktopBackupStore(Path directory) throws IOException {
        this.directory = directory.toAbsolutePath().normalize();
        Properties config = read(directory.resolve("storage-settings.properties"));
        settings = new Settings(config.getProperty("backup-dir", ""),
                config.getProperty("logs-dir", directory.resolve("logs").toString()));
        state = read(directory.resolve("backup-state.properties"));
        String savedParents = state.getProperty("parents", state.getProperty("base", ""));
        parents = savedParents.isEmpty() ? List.of() : List.of(savedParents.split(","));
    }

    Path database() { return directory.resolve("standardnaast.sqlite"); }
    Settings settings() { return settings; }
    String base() { return state.getProperty("base", ""); }

    synchronized Settings saveSettings(String backups, String logs) throws IOException {
        Path backupPath = checkedDirectory(backups, true);
        Path logsPath = checkedDirectory(logs.isBlank() ? directory.resolve("logs").toString() : logs, false);
        if (backupPath != null) versions(backupPath);
        var config = new Properties();
        config.setProperty("backup-dir", backupPath == null ? "" : backupPath.toString());
        config.setProperty("logs-dir", logsPath.toString());
        write(directory.resolve("storage-settings.properties"), config);
        Settings updated = new Settings(config.getProperty("backup-dir"), config.getProperty("logs-dir"));
        if (!updated.backupDirectory().equals(settings.backupDirectory())) {
            state.setProperty("base", "");
            parents = List.of();
            state.setProperty("parents", "");
            state.setProperty("source", updated.backupDirectory());
            saveState();
        }
        settings = updated;
        return settings;
    }

    private Path checkedDirectory(String value, boolean backup) throws IOException {
        if (value.isBlank() && backup) return null;
        Path path = Path.of(value);
        if (!path.isAbsolute()) throw new IllegalArgumentException("Selectionnez un repertoire absolu.");
        path = path.toAbsolutePath().normalize();
        if (backup && (path.startsWith(directory) || directory.startsWith(path))) {
            throw new IllegalArgumentException("Le repertoire partage doit etre distinct du repertoire de donnees locales.");
        }
        if (!backup) Files.createDirectories(path);
        requireWritable(path);
        return path;
    }

    static void requireWritable(Path path) throws IOException {
        // Do not recreate an unavailable cloud/mounted directory during shutdown.
        if (!Files.isDirectory(path)) throw new IOException("Repertoire indisponible : " + path);
        Path probe = Files.createTempFile(path, ".standardnaast-write-", ".partial");
        Files.delete(probe);
    }

    synchronized boolean dirty() throws IOException {
        return Files.exists(database()) && (Boolean.parseBoolean(state.getProperty("active", "false"))
                || !hash(database()).equals(state.getProperty("clean-hash", ""))
                || (Files.exists(Path.of(database() + "-wal")) && Files.size(Path.of(database() + "-wal")) > 0)
                || Files.exists(Path.of(database() + "-journal")));
    }

    synchronized void prepare(RestoreDecision decision) throws IOException {
        parents = base().isEmpty() ? List.of() : List.of(base());
        List<Version> versions = versions();
        List<Version> heads = heads(versions);
        if (!versions.isEmpty() && heads.isEmpty()) throw new IOException("Filiation des sauvegardes invalide.");
        if (heads.isEmpty()) return;
        boolean localDirty = dirty();
        boolean conflict = heads.size() > 1;
        Version selected = heads.getFirst();
        boolean same = selected.id().equals(base())
                && settings.backupDirectory().equals(state.getProperty("source", ""));
        if (conflict || (localDirty && (!same || !Files.exists(database())))) {
            selected = decision.choose(heads, localDirty);
            if (selected == null) {
                if (!Files.exists(database())) throw new java.util.concurrent.CancellationException("Aucune base locale a conserver.");
                return;
            }
            if (!heads.contains(selected)) throw new IOException("Sauvegarde selectionnee inconnue.");
        } else if (same && Files.exists(database())) {
            return;
        }
        restore(selected);
        state.setProperty("base", selected.id());
        state.setProperty("source", settings.backupDirectory());
        state.setProperty("active", "false");
        state.setProperty("clean-hash", hash(database()));
        parents = conflict ? heads.stream().map(Version::id).toList() : List.of(selected.id());
        state.setProperty("parents", String.join(",", parents));
        saveState();
    }

    synchronized void markActive() throws IOException {
        if (!state.getProperty("parents", "").isEmpty()) {
            parents = List.of(state.getProperty("parents").split(","));
        }
        state.setProperty("active", "true");
        saveState();
    }

    synchronized void preflight() throws IOException {
        if (!settings.backupDirectory().isBlank()) {
            requireWritable(Path.of(settings.backupDirectory()));
            versions();
        }
    }

    synchronized void finish() throws IOException {
        if (!Files.exists(database())) return;
        if (!settings.backupDirectory().isBlank()) {
            preflight();
            Path shared = Path.of(settings.backupDirectory());
            String id = UUID.randomUUID().toString();
            Instant created = Instant.now();
            Path snapshot = Files.createTempFile(directory, ".snapshot-", ".sqlite");
            Path upload = null;
            try {
                upload = Files.createTempFile(shared, ".standardnaast-", ".partial");
                Files.delete(snapshot);
                snapshot(database(), snapshot);
                var manifest = new Properties();
                manifest.setProperty("format", "1");
                manifest.setProperty("id", id);
                manifest.setProperty("created", created.toString());
                manifest.setProperty("parents", String.join(",", parents));
                manifest.setProperty("sha256", hash(snapshot));
                try (var zip = new ZipOutputStream(Files.newOutputStream(upload))) {
                    zip.putNextEntry(new ZipEntry("manifest.properties"));
                    manifest.store(zip, "Standard Naast SQLite backup");
                    zip.closeEntry();
                    zip.putNextEntry(new ZipEntry("standardnaast.sqlite"));
                    Files.copy(snapshot, zip);
                    zip.closeEntry();
                }
                validate(upload, null);
                Path published = shared.resolve("standardnaast-" + created.toEpochMilli() + "-" + id + ".snbak");
                Files.move(upload, published, StandardCopyOption.ATOMIC_MOVE);
                state.setProperty("base", id);
                state.setProperty("source", settings.backupDirectory());
                state.setProperty("parents", id);
                parents = List.of(id);
            } finally {
                Files.deleteIfExists(snapshot);
                if (upload != null) Files.deleteIfExists(upload);
            }
        }
        checkpoint();
        state.setProperty("clean-hash", hash(database()));
        state.setProperty("active", "false");
        saveState();
    }

    private void checkpoint() throws IOException {
        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + database());
             var sql = connection.createStatement();
             var rows = sql.executeQuery("PRAGMA wal_checkpoint(TRUNCATE)")) {
            if (rows.next() && rows.getInt(1) != 0) {
                throw new IOException("La base est encore utilisee. Sauvegarde locale non confirmee.");
            }
        } catch (SQLException e) {
            throw new IOException("Finalisation SQLite impossible.", e);
        }
    }

    synchronized List<Version> versions() throws IOException {
        if (settings.backupDirectory().isBlank()) return List.of();
        return versions(Path.of(settings.backupDirectory()));
    }

    private List<Version> versions(Path shared) throws IOException {
        if (!Files.isDirectory(shared)) throw new IOException("Repertoire de sauvegarde indisponible : " + shared);
        var result = new ArrayList<Version>();
        try (var files = Files.list(shared)) {
            for (Path path : files.filter(p -> p.getFileName().toString().startsWith("standardnaast-")
                    && p.getFileName().toString().endsWith(".snbak")).toList()) {
                Version version = validate(path, null);
                Version duplicate = result.stream().filter(v -> v.id().equals(version.id())).findFirst().orElse(null);
                if (duplicate == null) result.add(version);
                else if (!duplicate.created().equals(version.created()) || !duplicate.parents().equals(version.parents())) {
                    throw new IOException("Identifiant de sauvegarde duplique avec des metadonnees differentes : " + path);
                }
            }
        }
        result.sort(Comparator.comparing(Version::created).thenComparing(Version::id).reversed());
        return List.copyOf(result);
    }

    List<Version> heads(List<Version> versions) {
        var referenced = versions.stream().flatMap(v -> v.parents().stream()).collect(java.util.stream.Collectors.toSet());
        return versions.stream().filter(v -> !referenced.contains(v.id())).toList();
    }

    private Version validate(Path archive, Path destination) throws IOException {
        Path extracted = destination == null ? Files.createTempFile(directory, ".verify-", ".sqlite") : destination;
        try (var zip = new ZipFile(archive.toFile())) {
            ZipEntry metadata = zip.getEntry("manifest.properties");
            ZipEntry data = zip.getEntry("standardnaast.sqlite");
            if (metadata == null || data == null || zip.size() != 2) throw new IOException("Contenu incomplet.");
            var manifest = new Properties();
            try (var input = zip.getInputStream(metadata)) { manifest.load(input); }
            if (!"1".equals(manifest.getProperty("format"))) throw new IOException("Format non pris en charge.");
            String id = UUID.fromString(manifest.getProperty("id")).toString();
            Instant created = Instant.parse(manifest.getProperty("created"));
            List<String> parentIds = manifest.getProperty("parents", "").isEmpty() ? List.of()
                    : List.of(manifest.getProperty("parents").split(","));
            for (String parent : parentIds) UUID.fromString(parent);
            if (parentIds.contains(id)) throw new IOException("Parent de sauvegarde invalide.");
            try (var input = zip.getInputStream(data)) {
                Files.copy(input, extracted, StandardCopyOption.REPLACE_EXISTING);
            }
            if (!hash(extracted).equals(manifest.getProperty("sha256"))) throw new IOException("Somme de controle incorrecte.");
            integrity(extracted);
            return new Version(id, created, parentIds, archive);
        } catch (IOException | IllegalArgumentException | NullPointerException | java.time.DateTimeException e) {
            throw new IOException("Sauvegarde invalide : " + archive + ". " + e.getMessage(), e);
        } finally {
            if (destination == null) Files.deleteIfExists(extracted);
        }
    }

    private void restore(Version version) throws IOException {
        Path staging = Files.createTempFile(directory, ".restore-", ".sqlite");
        try {
            validate(version.file(), staging);
            if (Files.exists(database())) {
                Path recovery = directory.resolve("recovery");
                Files.createDirectories(recovery);
                snapshot(database(), recovery.resolve("before-restore-" + Instant.now().toEpochMilli()
                        + "-" + UUID.randomUUID() + ".sqlite"));
            }
            // All SQLite connections are closed here, before Spring/Liquibase starts.
            for (String suffix : List.of("-wal", "-shm", "-journal")) {
                Files.deleteIfExists(Path.of(database() + suffix));
            }
            Files.move(staging, database(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(staging);
        }
    }

    static void snapshot(Path database, Path target) throws IOException {
        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + database);
             var sql = connection.createStatement()) {
            sql.execute("PRAGMA busy_timeout=10000");
            sql.execute("VACUUM INTO '" + target.toString().replace("'", "''") + "'");
        } catch (SQLException e) {
            throw new IOException("Sauvegarde SQLite impossible. La base locale est conservee.", e);
        }
        integrity(target);
    }

    private static void integrity(Path file) throws IOException {
        var config = new org.sqlite.SQLiteConfig();
        config.setReadOnly(true);
        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + file, config.toProperties());
             var sql = connection.createStatement()) {
            try (var rows = sql.executeQuery("PRAGMA integrity_check")) {
                if (!rows.next() || !"ok".equals(rows.getString(1)) || rows.next()) {
                    throw new IOException("Integrite SQLite incorrecte.");
                }
            }
            try (var rows = sql.executeQuery("PRAGMA foreign_key_check")) {
                if (rows.next()) throw new IOException("References SQLite invalides.");
            }
            try (var rows = sql.executeQuery("""
                    SELECT COUNT(*) FROM sqlite_master WHERE type='table'
                    AND upper(name) IN ('APP_USERS', 'DATABASECHANGELOG')
                    """)) {
                if (!rows.next() || rows.getInt(1) != 2) throw new IOException("Base Standard Naast attendue.");
            }
        } catch (SQLException e) {
            throw new IOException("Verification SQLite impossible.", e);
        }
    }

    private static String hash(Path file) throws IOException {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            try (var input = Files.newInputStream(file)) {
                byte[] bytes = new byte[65536];
                int length;
                while ((length = input.read(bytes)) != -1) digest.update(bytes, 0, length);
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponible.", e);
        }
    }

    private static Properties read(Path file) throws IOException {
        var properties = new Properties();
        if (Files.exists(file)) {
            try (var input = Files.newInputStream(file)) { properties.load(input); }
        }
        return properties;
    }

    private void saveState() throws IOException { write(directory.resolve("backup-state.properties"), state); }

    private static void write(Path file, Properties properties) throws IOException {
        Path temp = Files.createTempFile(file.getParent(), ".settings-", ".partial");
        try {
            try (var output = Files.newOutputStream(temp)) { properties.store(output, "Standard Naast - local"); }
            Files.move(temp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temp);
        }
    }
}
