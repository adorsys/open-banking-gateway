package de.adorsys.opba.fintech.impl.exceptions;

import de.adorsys.opba.fintech.impl.service.exceptions.InvalidIbanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Collections;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidIbanException.class)
    public ResponseEntity<String> handleInvalidIban(InvalidIbanException ex) {
        LOG.warn("Invalid IBAN request: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Oauth2UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> handleOauth2Unauthorized(Oauth2UnauthorizedException ex) {
        LOG.warn("Unauthorized OAuth2 request: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonMap("message", ex.getMessage())); // 401
    }

    @ExceptionHandler({EmailNotAllowed.class, EmailNotVerified.class})
    public ResponseEntity<Map<String, String>> handleEmailExceptions(Exception ex) {
        LOG.warn("Forbidden email operation: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Collections.singletonMap("message", ex.getMessage())); // 403
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        LOG.error("Unexpected error: {}", ex.getMessage());
        return ResponseEntity.internalServerError().body("An unexpected error occurred. Please try again later.");
    }
}
