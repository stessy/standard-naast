package standardNaast.backend.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.spi.FilterReply;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

class LogbackConfigurationTest {
    @Test
    void preservesStderrForErrorsConsumedByDesktopLauncher() {
        Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        assertThat(root.getAppender("CONSOLE")).isInstanceOf(ConsoleAppender.class);
        assertThat(root.getAppender("STDERR")).isInstanceOf(ConsoleAppender.class);
        ConsoleAppender<ILoggingEvent> stdout = (ConsoleAppender<ILoggingEvent>) root.getAppender("CONSOLE");
        ConsoleAppender<ILoggingEvent> stderr = (ConsoleAppender<ILoggingEvent>) root.getAppender("STDERR");

        assertThat(stdout.getTarget()).isEqualTo("System.out");
        assertThat(stderr.getTarget()).isEqualTo("System.err");
        assertThat(root.getAppender("FILE").isStarted()).isTrue();
        for (Level level : new Level[]{Level.INFO, Level.WARN, Level.ERROR}) {
            LoggingEvent event = new LoggingEvent();
            event.setLevel(level);
            assertThat(stdout.getFilterChainDecision(event) != FilterReply.DENY)
                    .as("stdout at %s", level).isEqualTo(level == Level.INFO);
            assertThat(stderr.getFilterChainDecision(event) != FilterReply.DENY)
                    .as("stderr at %s", level).isEqualTo(level != Level.INFO);
        }
    }
}
