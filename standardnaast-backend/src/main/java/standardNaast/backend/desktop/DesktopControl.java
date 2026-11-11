package standardNaast.backend.desktop;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@Profile("desktop")
public class DesktopControl {
    private final ConfigurableApplicationContext context;
    private final DesktopStorage storage;
    private volatile String token;

    public DesktopControl(ConfigurableApplicationContext context, DesktopStorage storage) {
        this.context = context;
        this.storage = storage;
    }

    void initialize(String token) {
        this.token = token;
    }

    @PostMapping("/api/desktop/stop")
    public ResponseEntity<Void> stop(@RequestHeader(value = "X-Desktop-Token", defaultValue = "") String suppliedToken) {
        String expected = token;
        if (expected == null || !MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
                suppliedToken.getBytes(StandardCharsets.UTF_8))) {
            org.slf4j.LoggerFactory.getLogger(DesktopControl.class).warn("Commande d'arret desktop refusee");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return scheduleStop();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/api/desktop/quit")
    public ResponseEntity<Void> quit() {
        return scheduleStop();
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public org.springframework.http.ProblemDetail stopFailed(org.springframework.web.server.ResponseStatusException error) {
        return org.springframework.http.ProblemDetail.forStatusAndDetail(error.getStatusCode(), error.getReason());
    }

    private ResponseEntity<Void> scheduleStop() {
        try {
            storage.preflight();
        } catch (java.io.IOException e) {
            org.slf4j.LoggerFactory.getLogger(DesktopControl.class).error("Sauvegarde indisponible, arret annule", e);
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.CONFLICT,
                    "Sauvegarde indisponible. L'application reste ouverte : " + e.getMessage(), e);
        }
        org.slf4j.LoggerFactory.getLogger(DesktopControl.class).info("Arret desktop demande");
        new Thread(() -> {
            try {
                Thread.sleep(300);
                context.close();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                org.slf4j.LoggerFactory.getLogger(DesktopControl.class).error("Arret desktop interrompu", e);
            }
        }, "desktop-stop").start();
        return ResponseEntity.accepted().build();
    }
}
