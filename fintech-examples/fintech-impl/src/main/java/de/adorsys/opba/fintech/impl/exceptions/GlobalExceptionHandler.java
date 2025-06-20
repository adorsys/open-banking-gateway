package de.adorsys.opba.fintech.impl.exceptions;

import de.adorsys.opba.fintech.impl.service.exceptions.InvalidIbanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidIbanException.class)
    public ResponseEntity<String> handleInvalidIban(InvalidIbanException ex) {
        LOG.warn("Invalid IBAN request: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        LOG.error("Unexpected error: {}", ex.getMessage());
        return ResponseEntity.internalServerError().body("An unexpected error occurred. Please try again later.");
    }
}
