package standardNaast.backend.liquibase;

import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import liquibase.change.AbstractSQLChange;
import standardNaast.backend.config.LiquibaseRuntimeHints;

import static org.assertj.core.api.Assertions.assertThat;

class LiquibaseRuntimeHintsTest {

    @Test
    void allowsInheritedLiquibaseXmlSetters() throws Exception {
        RuntimeHints hints = new RuntimeHints();
        new LiquibaseRuntimeHints().registerHints(hints, getClass().getClassLoader());
        assertThat(RuntimeHintsPredicates.reflection().onMethodInvocation(
                AbstractSQLChange.class.getMethod("setSplitStatements", Boolean.class)).test(hints)).isTrue();
    }

    @Test
    void includesMasterAndAllNestedChangelogResources() throws Exception {
        RuntimeHints hints = new RuntimeHints();
        new LiquibaseRuntimeHints().registerHints(hints, getClass().getClassLoader());

        assertThat(RuntimeHintsPredicates.resource()
                .forResource("db/changelog/db.changelog-master.xml").test(hints)).isTrue();
        var resources = new PathMatchingResourcePatternResolver()
                .getResources("classpath*:db/changelog/**/*");
        int checked = 0;
        for (var resource : resources) {
            String url = resource.getURL().toString();
            if (!url.endsWith("/")) {
                String path = url.substring(url.indexOf("db/changelog/"));
                assertThat(RuntimeHintsPredicates.resource().forResource(path).test(hints))
                        .as(path).isTrue();
                checked++;
            }
        }
        assertThat(checked).isGreaterThan(1);
        assertThat(RuntimeHintsPredicates.resource().forResource("unrelated.txt").test(hints)).isFalse();
    }
}
