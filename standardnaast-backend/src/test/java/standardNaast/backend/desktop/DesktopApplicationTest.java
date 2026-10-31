package standardNaast.backend.desktop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DesktopApplicationTest {
    @TempDir
    Path directory;

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
