package standardNaast.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;
import standardNaast.backend.logging.LoggingRuntimeHints;
import standardNaast.backend.migration.H2ToPostgresMigrator;
import standardNaast.backend.security.InitialAdminSetup;

@SpringBootApplication
@ImportRuntimeHints(LoggingRuntimeHints.class)
public class StandardNaastApplication {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StandardNaastApplication.class);

    public static void main(String[] args) {
        for (String arg : args) {
            if (arg.equals("--initial-admin-status") || arg.equals("--create-initial-admin")) {
                InitialAdminSetup.run(arg.equals("--create-initial-admin"));
                return;
            }
            if (arg.startsWith("--migrate-from-h2=") || arg.startsWith("--migrate-h2=")) {
                String h2Path = arg.substring(arg.indexOf('=') + 1);
                log.info("Mode migration H2 vers PostgreSQL active.");
                H2ToPostgresMigrator.main(args);
                log.info("Migration achevee.");
                return;
            }
        }
        SpringApplication.run(StandardNaastApplication.class, args);
    }
}
