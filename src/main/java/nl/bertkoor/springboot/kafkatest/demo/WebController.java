package nl.bertkoor.springboot.kafkatest.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(WebController.CONTEXT_ROOT)
public class WebController {
    static final String CONTEXT_ROOT = "/api";
    static final String ENDPOINT_HELLO = "/hello";

    private final Validator validator = new Validator();
    private final KafkaProducer kafkaProducer;

    public WebController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @GetMapping(ENDPOINT_HELLO + "/{arg}")
    public HelloRecord getHello(@PathVariable("arg") String arg) {
        validator.validateArgument(arg);
        return new HelloRecord(arg);
    }

    @PutMapping(value = ENDPOINT_HELLO)
    public void putHello(@RequestBody HelloRecord request) {
        String arg = request.hello();
        validator.validateArgument(arg);
        kafkaProducer.hello(new HelloRecord(arg));
    }

}
