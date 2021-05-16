package quickhacks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@DisplayName("Executable JAR")
public class ExecutableJarTest {

    @Test
    @DisplayName("should run and exit without errors when ran as an executable JAR")
    void shouldRunAndExitWithoutErrorsWhenRanAsAnExecutableJAR() throws IOException, InterruptedException {
        /* We are making assumptions about the build tool, that is Gradle */
        final ProcessBuilder builder = new ProcessBuilder("java", "-jar", "build/libs/broken-links.jar");
        builder.inheritIO();

        final Process process = builder.start();
        failIfProcessDoesNotFinishInSeconds(process, 5);

        assertThat(process.exitValue())
                .describedAs("Application should exit with exit value of 0, indicating no errors")
                .isEqualTo(0);
    }

    private void failIfProcessDoesNotFinishInSeconds(final Process process, final int seconds) throws InterruptedException {
        if (process.waitFor(seconds, TimeUnit.SECONDS) == false) {
            /* Process did not finish in time */
            process.destroyForcibly();
            fail("The application did not finish within %s seconds", seconds);
        }
    }
}
