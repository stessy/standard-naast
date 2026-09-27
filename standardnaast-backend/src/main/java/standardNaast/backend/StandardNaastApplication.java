package standardNaast.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import standardNaast.backend.migration.H2ToPostgresMigrator;

@SpringBootApplication
public class StandardNaastApplication {

    public static void main(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("--migrate-from-h2=") || arg.startsWith("--migrate-h2=")) {
                String h2Path = arg.substring(arg.indexOf('=') + 1);
                System.out.println(">>> Mode migration H2 vers PostgreSQL activé.");
                System.out.println(">>> Fichier source H2: " + h2Path);
                H2ToPostgresMigrator.main(args);
                System.out.println(">>> Migration achevée.");
                return;
            }
        }
        SpringApplication.run(StandardNaastApplication.class, args);
    }
}
