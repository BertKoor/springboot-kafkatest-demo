package nl.bertkoor.springboot.kafkatest.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Value("app.produce.topic.name")
    private String produceTopic;

    public String produceTopicName() {
        return produceTopic;
    }
}
