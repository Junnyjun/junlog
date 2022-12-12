package git.io.join.exception;

import git.io.join.adapter.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = {"git.io.join.adapter.in"})
public class ServiceExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<MessageResponse> handleException(Exception ex) {
        ex.getStackTrace();

        return ResponseEntity
                .status(HttpStatus.ALREADY_REPORTED)
                .body(new MessageResponse(ex.getMessage()));
    }
}