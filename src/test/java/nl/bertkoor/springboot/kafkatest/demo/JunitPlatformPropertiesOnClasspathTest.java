package nl.bertkoor.springboot.kafkatest.demo;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ClassLoaderUtils;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Let the tests of this project fail when there is more than one 'junit-platform.properties' on the classpath.
 *
 * I have tried to intercept the warnings logged by the LauncherConfigurationParameters class with a LogRecordListener,
 * but loading of test classes is done after the configuration phase, so this cannot be detected.
 */
public class JunitPlatformPropertiesOnClasspathTest {

    @Test
    void detectIssue() throws IOException {
        ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
        int count = Collections.list(classLoader.getResources("junit-platform.properties")).size();
        if (count > 0) {
            assertEquals(1, count, "there should be at most one 'junit-platform.properties' on the classpath");
        }
    }
}
