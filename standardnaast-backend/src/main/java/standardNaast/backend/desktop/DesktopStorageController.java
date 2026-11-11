package standardNaast.backend.desktop;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Profile("desktop")
@RequestMapping("/api/desktop/storage")
@PreAuthorize("hasRole('ADMIN')")
public class DesktopStorageController {
    public record SettingsRequest(@NotNull String backupDirectory, @NotNull String logsDirectory) {}
    public record DirectorySelection(String directory) {}
    private final DesktopStorage storage;

    public DesktopStorageController(DesktopStorage storage) { this.storage = storage; }

    @GetMapping
    public DesktopStorage.SettingsView settings() throws IOException { return storage.settings(); }

    @PutMapping
    public DesktopStorage.SettingsView save(@Valid @RequestBody SettingsRequest settings) throws IOException {
        return storage.save(settings.backupDirectory(), settings.logsDirectory());
    }

    @PostMapping("/choose-directory/{kind}")
    public DirectorySelection choose(@PathVariable String kind) throws IOException {
        return new DirectorySelection(storage.chooseDirectory(kind));
    }

    @ExceptionHandler(IOException.class)
    public org.springframework.http.ProblemDetail failed(IOException error) {
        org.slf4j.LoggerFactory.getLogger(DesktopStorageController.class).error("Configuration du stockage impossible", error);
        return org.springframework.http.ProblemDetail.forStatusAndDetail(
                org.springframework.http.HttpStatus.CONFLICT, error.getMessage());
    }
}
