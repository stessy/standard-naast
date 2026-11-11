package standardNaast.backend.desktop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class DesktopBackupStoreTest {
    @TempDir Path root;

    private DesktopBackupStore store(String machine, Path backups) throws Exception {
        Path local = Files.createDirectories(root.resolve(machine));
        var store = new DesktopBackupStore(local);
        store.saveSettings(backups.toString(), local.resolve("custom-logs").toString());
        return store;
    }

    private void database(DesktopBackupStore store, String value) throws Exception {
        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + store.database());
             var sql = connection.createStatement()) {
            sql.execute("CREATE TABLE IF NOT EXISTS APP_USERS (ID INTEGER PRIMARY KEY, NAME TEXT)");
            sql.execute("CREATE TABLE IF NOT EXISTS DATABASECHANGELOG (ID TEXT)");
            sql.execute("DELETE FROM APP_USERS");
            sql.execute("INSERT INTO APP_USERS VALUES (1, '" + value + "')");
        }
    }

    private String value(DesktopBackupStore store) throws Exception {
        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + store.database());
             var sql = connection.createStatement();
             var rows = sql.executeQuery("SELECT NAME FROM APP_USERS")) {
            assertThat(rows.next()).isTrue();
            return rows.getString(1);
        }
    }

    @Test
    void savesAndRestoresAcrossMachinesAndPersistsLocalSettings() throws Exception {
        Path shared = Files.createDirectory(root.resolve("drive"));
        var mac = store("mac", shared);
        database(mac, "first");
        mac.prepare((versions, dirty) -> versions.getFirst());
        mac.markActive();
        mac.finish();
        assertThat(mac.versions()).hasSize(1);
        var windows = store("windows", shared);
        windows.prepare((versions, dirty) -> {
            fail("A fresh machine must restore without a question");
            return null;
        });
        assertThat(value(windows)).isEqualTo("first");
        var loaded = new DesktopBackupStore(root.resolve("windows"));
        assertThat(loaded.settings().backupDirectory()).isEqualTo(shared.toString());
        assertThat(loaded.settings().logsDirectory()).endsWith("custom-logs");
        windows.markActive();
        database(windows, "second");
        new DesktopBackupStore(root.resolve("windows")).finish();
        assertThat(windows.versions()).hasSize(2);
        mac.prepare((versions, dirty) -> {
            fail("Clean local data must restore automatically");
            return null;
        });
        assertThat(value(mac)).isEqualTo("second");
    }

    @Test
    void preservesUnsavedLocalDataBeforeAnExplicitRestore() throws Exception {
        Path shared = Files.createDirectory(root.resolve("drive"));
        var a = store("a", shared);
        database(a, "initial");
        a.prepare((versions, dirty) -> null);
        a.markActive();
        a.finish();
        a.prepare((versions, dirty) -> null);
        a.markActive();
        database(a, "unsaved");
        var b = store("b", shared);
        b.prepare((versions, dirty) -> null);
        b.markActive();
        database(b, "remote");
        b.finish();
        var restarted = new DesktopBackupStore(root.resolve("a"));
        restarted.prepare((versions, dirty) -> {
            assertThat(dirty).isTrue();
            return versions.getFirst();
        });
        assertThat(value(restarted)).isEqualTo("remote");
        try (var files = Files.list(root.resolve("a/recovery"))) {
            assertThat(files.toList()).hasSize(1);
        }
    }

    @Test
    void simultaneousOrOfflineSessionsKeepBothBranchesAndRequireAChoice() throws Exception {
        Path shared = Files.createDirectory(root.resolve("drive"));
        var a = store("a", shared);
        database(a, "initial");
        a.prepare((versions, dirty) -> null);
        a.markActive();
        a.finish();
        a.prepare((versions, dirty) -> null);
        a.markActive();
        var b = store("b", shared);
        b.prepare((versions, dirty) -> null);
        b.markActive();
        database(a, "branch-a");
        database(b, "branch-b");
        a.finish();
        b.finish();
        assertThat(a.heads(a.versions())).hasSize(2);
        b.prepare((versions, dirty) -> {
            assertThat(versions).hasSize(2);
            return versions.stream().filter(v -> !v.id().equals(b.base())).findFirst().orElseThrow();
        });
        assertThat(value(b)).isEqualTo("branch-a");
        b.markActive();
        b.finish();
        assertThat(b.heads(b.versions())).hasSize(1);
    }

    @Test
    void corruptBackupsBlockRestoreWithoutReplacingLocalData() throws Exception {
        Path shared = Files.createDirectory(root.resolve("drive"));
        var a = store("a", shared);
        database(a, "safe");
        Files.writeString(shared.resolve("standardnaast-broken.snbak"), "not a backup");
        assertThatThrownBy(() -> a.prepare((versions, dirty) -> null))
                .isInstanceOf(java.io.IOException.class).hasMessageContaining("invalide");
        assertThat(value(a)).isEqualTo("safe");
        try (var files = Files.list(shared)) {
            assertThat(files.toList()).hasSize(1);
        }
    }

    @Test
    void failedPublicationLeavesTheSessionDirtyAndDoesNotClaimSuccess() throws Exception {
        Path shared = Files.createDirectory(root.resolve("drive"));
        var a = store("a", shared);
        database(a, "safe");
        a.prepare((versions, dirty) -> null);
        a.markActive();
        Files.delete(shared);
        Files.writeString(shared, "unavailable");
        assertThatThrownBy(a::finish).isInstanceOf(java.io.IOException.class);
        assertThat(new DesktopBackupStore(root.resolve("a")).dirty()).isTrue();
        assertThat(value(a)).isEqualTo("safe");
    }

    @Test
    void cancelledConflictDoesNotAlterDatabaseOrCreateAnActiveMarker() throws Exception {
        Path shared = Files.createDirectory(root.resolve("drive"));
        var a = store("a", shared);
        database(a, "source");
        a.prepare((versions, dirty) -> null);
        a.markActive();
        a.finish();
        var b = store("b", shared);
        database(b, "local");
        assertThatThrownBy(() -> b.prepare((versions, dirty) -> {
            throw new java.util.concurrent.CancellationException();
        })).isInstanceOf(java.util.concurrent.CancellationException.class);
        assertThat(value(b)).isEqualTo("local");
    }

    @Test
    void ignoresIncompleteCloudUploadsAndValidatesDirectoryInput() throws Exception {
        Path shared = Files.createDirectory(root.resolve("drive"));
        var a = store("a", shared);
        Files.writeString(shared.resolve(".upload.partial"), "incomplete");
        assertThat(a.versions()).isEmpty();
        assertThatThrownBy(() -> a.saveSettings("relative/path", ""))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> a.saveSettings(root.resolve("a").toString(), ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void snapshotIncludesCommittedWalData() throws Exception {
        Path shared = Files.createDirectory(root.resolve("drive"));
        var a = store("a", shared);
        database(a, "initial");
        a.prepare((versions, dirty) -> null);
        a.markActive();
        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + a.database());
             var sql = connection.createStatement()) {
            sql.execute("PRAGMA journal_mode=WAL");
            sql.execute("UPDATE APP_USERS SET NAME='wal-data'");
            a.finish();
        }
        var b = store("b", shared);
        b.prepare((versions, dirty) -> null);
        assertThat(value(b)).isEqualTo("wal-data");
    }
}
