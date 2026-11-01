package standardNaast.backend.desktop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DesktopApplicationTest {
    @TempDir
    Path directory;

    @Test
    void nativeApplicationReopenRequestsTheExistingInterface() {
        var desktop = mock(java.awt.Desktop.class);
        var action = mock(Runnable.class);
        when(desktop.isSupported(java.awt.Desktop.Action.APP_EVENT_REOPENED)).thenReturn(true);
        var listener = DesktopApplication.registerReopen(desktop, action);
        verify(desktop).addAppEventListener(listener);
        listener.appReopened(mock(java.awt.desktop.AppReopenedEvent.class));
        verify(action).run();
    }

    @Test
    void platformsWithoutNativeReopenKeepTheSecondLaunchPath() {
        var desktop = mock(java.awt.Desktop.class);
        assertThat(DesktopApplication.registerReopen(desktop, mock(Runnable.class))).isNull();
        verify(desktop, never()).addAppEventListener(any());
    }

    @Test
    void requestsTheDefaultBrowserWithoutDependingOnAnExistingBrowserWindow() throws Exception {
        var desktop = mock(java.awt.Desktop.class);
        when(desktop.isSupported(java.awt.Desktop.Action.BROWSE)).thenReturn(true);
        var url = java.net.URI.create("http://127.0.0.1:8080/");
        DesktopApplication.browse(url, desktop);
        verify(desktop).browse(url);
    }

    @Test
    void reportsBrowserOpeningFailuresWithTheApplicationAddress() throws Exception {
        var desktop = mock(java.awt.Desktop.class);
        var url = java.net.URI.create("http://127.0.0.1:8080/");
        assertThatThrownBy(() -> DesktopApplication.browse(url, desktop))
                .isInstanceOf(java.io.IOException.class).hasMessageContaining(url.toString());
        verify(desktop, never()).browse(url);
        when(desktop.isSupported(java.awt.Desktop.Action.BROWSE)).thenReturn(true);
        doThrow(new java.io.IOException("System browser unavailable")).when(desktop).browse(url);
        assertThatThrownBy(() -> DesktopApplication.browse(url, desktop))
                .isInstanceOf(java.io.IOException.class).hasMessage("System browser unavailable");
    }

    @Test
    void startupMessagesFollowSpringLifecycleEvents() {
        assertThat(DesktopApplication.startupMessage(mock(
                org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent.class)))
                .isEqualTo("Chargement de la configuration...");
        assertThat(DesktopApplication.startupMessage(mock(
                org.springframework.boot.context.event.ApplicationPreparedEvent.class)))
                .isEqualTo("Initialisation de la base SQLite et des services...");
        assertThat(DesktopApplication.startupMessage(mock(
                org.springframework.boot.context.event.ApplicationStartedEvent.class)))
                .isEqualTo("Verification de l'application...");
        assertThat(DesktopApplication.startupMessage(mock(
                org.springframework.boot.context.event.ApplicationReadyEvent.class)))
                .isEqualTo("Application prete...");
    }

    @Test
    void noBrowserModeDoesNotCreateSplashWindows() throws Exception {
        var before = java.awt.Window.getWindows().length;
        try (var splash = StartupSplash.open(true)) {
            splash.update("Test");
            splash.visible(true);
            splash.visible(false);
        }
        assertThat(java.awt.Window.getWindows()).hasSize(before);
    }

    @Test
    void loadsTheClubIconWithTransparency() throws Exception {
        var image = DesktopApplication.loadIcon();
        assertThat(image.getWidth()).isEqualTo(512);
        assertThat(image.getHeight()).isEqualTo(512);
        assertThat(image.getColorModel().hasAlpha()).isTrue();
        assertThat(image.getRGB(0, 0) >>> 24).isZero();
    }

    @Test
    void storesDataOutsideTheApplicationOnEachPlatform() {
        Path home = Path.of("/users/test");
        assertThat(DesktopApplication.dataDirectory("Mac OS X", home, null))
                .isEqualTo(home.resolve("Library/Application Support/Standard Naast"));
        assertThat(DesktopApplication.dataDirectory("Windows 11", home, "/local-data"))
                .isEqualTo(Path.of("/local-data/Standard Naast"));
        assertThat(DesktopApplication.dataDirectory("Windows 11", home, null))
                .isEqualTo(home.resolve("AppData/Local/Standard Naast"));
        assertThat(DesktopApplication.dataDirectory("Linux", home, null))
                .isEqualTo(home.resolve(".local/share/standardnaast"));
    }

    @Test
    void offersImportOnlyForFreshInteractiveDataAndCancellationDoesNotCreateDatabase() throws Exception {
        Path database = directory.resolve("standardnaast.sqlite");
        assertThat(DesktopApplication.shouldOfferInitialImport(database, true)).isFalse();
        assertThat(DesktopApplication.shouldOfferInitialImport(database, false))
                .isEqualTo(!java.awt.GraphicsEnvironment.isHeadless());
        assertThatThrownBy(() -> DesktopApplication.applyInitialDataChoice(database,
                DesktopApplication.InitialDataChoice.CANCEL, null, null))
                .isInstanceOf(java.util.concurrent.CancellationException.class);
        assertThat(database).doesNotExist();
        Files.createFile(database);
        assertThat(DesktopApplication.shouldOfferInitialImport(database, false)).isFalse();
    }
}
