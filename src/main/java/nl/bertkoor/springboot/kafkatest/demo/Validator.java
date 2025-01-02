package nl.bertkoor.springboot.kafkatest.demo;

import java.util.List;
import org.springframework.util.Assert;

public class Validator {

    private static final String NOT_AN_ARGUMENT = "Not a valid argument";

    public void validateArgument(String arg) {
        Assert.hasText(arg, NOT_AN_ARGUMENT);
        if ("exception".equals(arg)) {
            throw new RuntimeException("Exception thrown on request");
        }
        Assert.isTrue(!List.of("true", "false", "yes", "no").contains(arg), NOT_AN_ARGUMENT);
    }
}
