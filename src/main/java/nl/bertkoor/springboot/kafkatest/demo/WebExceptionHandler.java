package nl.bertkoor.springboot.kafkatest.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class WebExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        log.error("Something went wrong (internal)", ex);
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestErrorResponse> handleException(Exception ex) {
        log.error("Something went wrong (generic)", ex);
        return new ResponseEntity<>(new RestErrorResponse(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RestErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(new RestErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    record RestErrorResponse(String error) {}
}
