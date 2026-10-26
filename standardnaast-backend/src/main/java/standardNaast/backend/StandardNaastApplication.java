package standardNaast.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;
import standardNaast.backend.logging.LoggingRuntimeHints;
import standardNaast.backend.migration.H2ToPostgresMigrator;
import standardNaast.backend.security.InitialAdminSetup;
import standardNaast.backend.desktop.DesktopApplication;

@SpringBootApplication
@ImportRuntimeHints(LoggingRuntimeHints.class)
public class StandardNaastApplication {
    public static void main(String[] args) {
        if (java.util.Arrays.asList(args).contains("--desktop")
                || java.util.Arrays.asList(args).contains("--desktop-stop")) {
            DesktopApplication.launch(args);
            return;
        }
        for (String arg : args) {
            if (arg.equals("--initial-admin-status") || arg.equals("--create-initial-admin")) {
                InitialAdminSetup.run(arg.equals("--create-initial-admin"));
                return;
            }
            if (arg.startsWith("--migrate-from-h2=") || arg.startsWith("--migrate-h2=")) {
                var log = org.slf4j.LoggerFactory.getLogger(StandardNaastApplication.class);
                log.info("Mode migration H2 vers PostgreSQL active.");
                H2ToPostgresMigrator.main(args);
                log.info("Migration achevee.");
                return;
            }
        }
        SpringApplication.run(StandardNaastApplication.class, args);
    }
}
