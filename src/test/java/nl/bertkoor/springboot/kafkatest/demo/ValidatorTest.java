package nl.bertkoor.springboot.kafkatest.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class ValidatorTest {

    private final Validator validator = new Validator();

    @Test
    void whenArgIsException_thenRuntimeExceptionIsThrown() {
        assertThrows(RuntimeException.class, () -> validator.validateArgument("exception"));
    }

    @ParameterizedTest
    @CsvSource({"world, false", ", true","true, true", "no, true"})
    void parameterizedTestWithoutName(String arg, boolean exceptionExpected) {
        assertValidatorWithArgument(arg, exceptionExpected);
    }

    @ParameterizedTest(name = "[{index}] {argumentsWithNames}")
    @CsvSource({"world, false", ", true","true, true", "no, true"})
    void parameterizedTestWithNameSetDefault(String arg, boolean exceptionExpected) {
        assertValidatorWithArgument(arg, exceptionExpected);
    }

    @ParameterizedTest(name = "{displayName}.{argumentsWithNames}")
    @CsvSource({"world, false", ", true","true, true", "no, true"})
    void parameterizedTestWithNameSetOther(String arg, boolean exceptionExpected) {
        assertValidatorWithArgument(arg, exceptionExpected);
    }

    void assertValidatorWithArgument(String arg, boolean exceptionExpected) {
        try {
            validator.validateArgument(arg);
            if (exceptionExpected) {
                fail("Exception was expected");
            }
        } catch (IllegalArgumentException ex) {
            if (!exceptionExpected) {
                fail("Exception not expected");
            }
        }
    }
}
