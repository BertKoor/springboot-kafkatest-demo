package nl.bertkoor.springboot.kafkatest.demo;

import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EmbeddedKafka(brokerProperties = {"port=${kafka.broker.port}"})
public class WebControllerIntegrationTest {

    @Value("${local.server.port}")
    private int myPort;

    @SpyBean
    private KafkaProducer kafkaProducer;

    @Test
    void givenTheEndpoint_whenGetHello_thenWorldIsReturned() {
        RestAssured.given().port(myPort)
                .when().get(WebController.CONTEXT_ROOT + WebController.ENDPOINT_HELLO + "/{arg}", "world")
                .then().statusCode(HttpStatus.SC_OK).body("hello", equalTo("world"));
    }

    @Test
    void givenTheEndpoint_whenGetNothing_thenIsBadRequest() {
        RestAssured.given().port(myPort)
                .when().get(WebController.CONTEXT_ROOT + WebController.ENDPOINT_HELLO + "/{arg}", " ")
                .then().statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    void givenTheEndpoint_whenGetException_thenIsInternalServerError() {
        RestAssured.given().port(myPort)
                .when().get(WebController.CONTEXT_ROOT + WebController.ENDPOINT_HELLO + "/{arg}", "exception")
                .then().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    void givenTheEndpoint_whenPutWorld_isAccepted() {
        String request = """
{"hello": "world"}
                """;
        RestAssured.given().port(myPort)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().put(WebController.CONTEXT_ROOT + WebController.ENDPOINT_HELLO)
                .then().statusCode(HttpStatus.SC_OK);
        Mockito.verify(kafkaProducer, times(1))
                .hello(any(HelloRecord.class));
    }

    @Test
    void givenTheEndpoint_whenPutFalse_isBadRequest() {
        String request = """
{"hello": "false"}
                """;
        RestAssured.given().port(myPort)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().put(WebController.CONTEXT_ROOT + WebController.ENDPOINT_HELLO)
                .then().statusCode(HttpStatus.SC_BAD_REQUEST);
        Mockito.verify(kafkaProducer, never())
                .hello(any(HelloRecord.class));
    }

}
