package standardNaast.backend.desktop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import standardNaast.backend.StandardNaastApplication;
import standardNaast.backend.security.InitialAdminSetup;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class DesktopStorageIntegrationTest {
    @TempDir Path root;
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper json = new ObjectMapper();

    private ConfigurableApplicationContext start(Path directory) throws Exception {
        var store = new DesktopBackupStore(directory);
        store.prepare((versions, dirty) -> {
            throw new java.io.IOException("Unexpected interactive decision");
        });
        store.markActive();
        var application = new SpringApplication(StandardNaastApplication.class);
        application.setRegisterShutdownHook(false);
        var context = application.run("--spring.profiles.active=desktop", "--server.port=0",
                "--app.desktop.data-dir=" + directory, "--spring.main.headless=true");
        context.getBean(DesktopStorage.class).activate(false);
        return context;
    }

    private String url(ConfigurableApplicationContext context) {
        return "http://127.0.0.1:" + ((WebServerApplicationContext) context).getWebServer().getPort();
    }

    private HttpResponse<String> request(String url, String method, String token, String body) throws Exception {
        var request = HttpRequest.newBuilder(URI.create(url)).header("Content-Type", "application/json")
                .method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
        if (token != null) request.header("Authorization", "Bearer " + token);
        return http.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    private String login(String url) throws Exception {
        var response = request(url + "/api/auth/login", "POST", null,
                "{\"username\":\"storage-admin\",\"password\":\"storage-test-password\"}");
        assertThat(response.statusCode()).isEqualTo(200);
        return json.readTree(response.body()).get("accessToken").asText();
    }

    @Test
    void settingsShutdownSnapshotAndAutomaticRestoreWorkThroughRealHttpAndJpa() throws Exception {
        Path shared = Files.createDirectory(root.resolve("drive"));
        Path mac = Files.createDirectory(root.resolve("mac"));
        Path windows = Files.createDirectory(root.resolve("windows"));
        Path logs = Files.createDirectory(root.resolve("selected-logs"));
        long memberId;
        try (var context = start(mac)) {
            String url = url(context);
            assertThat(request(url + "/api/desktop/storage", "GET", null, null).statusCode()).isEqualTo(401);
            try (var connection = context.getBean(DataSource.class).getConnection()) {
                InitialAdminSetup.createAdmin(connection, "storage-admin", "storage-test-password");
            }
            String token = login(url);
            var settings = json.createObjectNode().put("backupDirectory", shared.toString())
                    .put("logsDirectory", logs.toString());
            var saved = request(url + "/api/desktop/storage", "PUT", token, json.writeValueAsString(settings));
            assertThat(saved.statusCode()).isEqualTo(200);
            assertThat(json.readTree(saved.body()).get("logsRestartRequired").asBoolean()).isTrue();
            var loaded = request(url + "/api/desktop/storage", "GET", token, null);
            assertThat(json.readTree(loaded.body()).get("backupDirectory").asText()).isEqualTo(shared.toString());
            var cancelled = request(url + "/api/desktop/storage/choose-directory/backup", "POST", token, "{}");
            assertThat(cancelled.statusCode()).isEqualTo(409);
            assertThat(json.readTree(cancelled.body()).get("detail").asText()).contains("indisponible");
            var invalid = request(url + "/api/desktop/storage", "PUT", token,
                    "{\"backupDirectory\":\"relative\",\"logsDirectory\":\"\"}");
            assertThat(invalid.statusCode()).isEqualTo(400);
            var created = request(url + "/api/members", "POST", token,
                    "{\"name\":\"Shared snapshot\",\"firstname\":\"Mac\",\"birthdate\":\"1980-02-29\",\"student\":false,\"redCard\":true}");
            assertThat(created.statusCode()).isEqualTo(201);
            memberId = json.readTree(created.body()).get("id").asLong();
        }
        var macStore = new DesktopBackupStore(mac);
        assertThat(macStore.versions()).hasSize(1);
        assertThat(macStore.dirty()).isFalse();
        var windowsStore = new DesktopBackupStore(windows);
        windowsStore.saveSettings(shared.toString(), windows.resolve("logs").toString());
        try (var context = start(windows)) {
            String url = url(context);
            String token = login(url);
            var member = request(url + "/api/members/" + memberId, "GET", token, null);
            assertThat(member.statusCode()).isEqualTo(200);
            assertThat(json.readTree(member.body()).get("name").asText()).isEqualTo("Shared snapshot");
            var updated = request(url + "/api/members/" + memberId, "PUT", token,
                    "{\"name\":\"Shared Windows update\",\"firstname\":\"Mac\",\"birthdate\":\"1980-02-29\",\"student\":false,\"redCard\":true}");
            assertThat(updated.statusCode()).isEqualTo(200);
        }
        try (var context = start(mac)) {
            var member = request(url(context) + "/api/members/" + memberId, "GET", login(url(context)), null);
            assertThat(json.readTree(member.body()).get("name").asText()).isEqualTo("Shared Windows update");
            assertThat(context.getBean(DesktopStorage.class).settings().logsRestartRequired()).isFalse();
        }
        assertThat(new DesktopBackupStore(mac).versions()).hasSize(3);
    }

    @Test
    void unavailableDirectoryBlocksQuitButSettingsRemainEditableAndLocalDataSurvives() throws Exception {
        Path local = Files.createDirectory(root.resolve("local"));
        Path shared = Files.createDirectory(root.resolve("drive"));
        try (var context = start(local)) {
            try (var connection = context.getBean(DataSource.class).getConnection()) {
                InitialAdminSetup.createAdmin(connection, "storage-admin", "storage-test-password");
            }
            String url = url(context);
            String token = login(url);
            var settings = json.createObjectNode().put("backupDirectory", shared.toString()).put("logsDirectory", "");
            assertThat(request(url + "/api/desktop/storage", "PUT", token,
                    json.writeValueAsString(settings)).statusCode()).isEqualTo(200);
            Files.delete(shared);
            var stopped = request(url + "/api/desktop/quit", "POST", token, "{}");
            assertThat(stopped.statusCode()).isEqualTo(409);
            assertThat(json.readTree(stopped.body()).get("detail").asText()).contains("reste ouverte");
            assertThat(context.isActive()).isTrue();
            var loaded = request(url + "/api/desktop/storage", "GET", token, null);
            assertThat(loaded.statusCode()).isEqualTo(200);
            assertThat(json.readTree(loaded.body()).get("backupError").asText()).contains("indisponible");
            settings.put("backupDirectory", "");
            assertThat(request(url + "/api/desktop/storage", "PUT", token,
                    json.writeValueAsString(settings)).statusCode()).isEqualTo(200);
            assertThat(login(url)).isNotBlank();
        }
        assertThat(local.resolve("standardnaast.sqlite")).exists();
    }
}
