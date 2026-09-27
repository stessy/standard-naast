package standardNaast.backend.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class LiquibaseConfig {

    @Value("${spring.liquibase.change-log:classpath:/db/changelog/db.changelog-master.xml}")
    private String changeLog;

    @Value("${spring.liquibase.enabled:true}")
    private boolean enabled;

    @Bean("liquibase")
    @ConditionalOnProperty(name = "spring.liquibase.enabled", havingValue = "true", matchIfMissing = true)
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLog);
        liquibase.setShouldRun(enabled);
        return liquibase;
    }

    @Bean
    public static BeanFactoryPostProcessor dependsOnPostProcessor() {
        return beanFactory -> {
            for (String name : beanFactory.getBeanDefinitionNames()) {
                if ("entityManagerFactory".equalsIgnoreCase(name)) {
                    var definition = beanFactory.getBeanDefinition(name);
                    String[] dependsOn = definition.getDependsOn();
                    Set<String> set = new HashSet<>(dependsOn != null ? Arrays.asList(dependsOn) : Set.of());
                    set.add("liquibase");
                    definition.setDependsOn(set.toArray(String[]::new));
                }
            }
        };
    }
}
