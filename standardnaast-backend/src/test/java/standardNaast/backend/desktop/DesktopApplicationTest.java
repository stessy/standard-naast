package standardNaast.backend.desktop;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class DesktopApplicationTest {
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
}
