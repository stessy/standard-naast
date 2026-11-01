package standardNaast.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationRuntimeControllerTest {
    @Test
    void desktopControlsAreAvailableOnlyWithTheDesktopProfile() {
        var environment = new MockEnvironment();
        var controller = new ApplicationRuntimeController(environment);
        assertThat(controller.runtime().desktop()).isFalse();
        environment.setActiveProfiles("postgres");
        assertThat(controller.runtime().desktop()).isFalse();
        environment.setActiveProfiles("desktop");
        assertThat(controller.runtime().desktop()).isTrue();
    }
}
