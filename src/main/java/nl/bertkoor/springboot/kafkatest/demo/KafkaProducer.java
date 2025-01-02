package nl.bertkoor.springboot.kafkatest.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    private final AppConfiguration config;
    private final KafkaTemplate<String, HelloRecord> kafkaTemplate;

    public KafkaProducer(AppConfiguration config, KafkaTemplate<String, HelloRecord> kafkaTemplate) {
        this.config = config;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void hello(HelloRecord rec) {
        String key = UUID.randomUUID().toString();
        kafkaTemplate.send(config.produceTopicName(), key, rec);
        log.info("event produced with key: {}", key);
    }

}
