package standardNaast.backend.controller;

import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationRuntimeController {
    private final Environment environment;

    public ApplicationRuntimeController(Environment environment) {
        this.environment = environment;
    }

    public record RuntimeInfo(boolean desktop) {}

    @GetMapping("/api/application/runtime")
    public RuntimeInfo runtime() {
        return new RuntimeInfo(environment.acceptsProfiles(Profiles.of("desktop")));
    }
}
