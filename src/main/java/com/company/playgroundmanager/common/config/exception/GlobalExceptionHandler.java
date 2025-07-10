package com.company.playgroundmanager.common.config.exception;

import com.company.playgroundmanager.common.model.CustomErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({RecordNotFoundException.class})
    public ResponseEntity<CustomErrorResponse> handleNotFound(RecordNotFoundException ex) {
        log.error("Not found error: ", ex);
        return buildResponse(HttpStatus.NOT_FOUND, "Not found: " + ex.getMessage());
    }

    @ExceptionHandler({PlayGroundValidationException.class})
    public ResponseEntity<CustomErrorResponse> handleValidation(PlayGroundValidationException ex) {
        log.warn("Business validation failed: ", ex);
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation error: " + ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(err -> errors.add(err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(CustomErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation Error(s): " + errors)
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error: " + ex.getMessage());
    }

    private ResponseEntity<CustomErrorResponse> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(CustomErrorResponse.builder()
                .status(status.value())
                .message(message)
                .build());
    }
}
