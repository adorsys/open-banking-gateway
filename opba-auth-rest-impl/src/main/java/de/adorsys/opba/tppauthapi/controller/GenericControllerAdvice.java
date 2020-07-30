package de.adorsys.opba.tppauthapi.controller;

import de.adorsys.opba.protocol.facade.exceptions.NoProtocolRegisteredException;
import de.adorsys.opba.protocol.facade.exceptions.PsuAuthenticationException;
import de.adorsys.opba.protocol.facade.exceptions.PsuAuthorizationException;
import de.adorsys.opba.protocol.facade.exceptions.PsuRegisterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.List;

@ControllerAdvice(basePackageClasses = {
        PsuAuthController.class
})
@Slf4j
public class GenericControllerAdvice {

    @ExceptionHandler({PsuRegisterException.class})
    public ResponseEntity<List<String>> handleUserExistsException(PsuRegisterException ex) {
        log.error("User exists exception: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonList(ex.getMessage()));
    }

    @ExceptionHandler({PsuAuthenticationException.class})
    public ResponseEntity<List<String>> handleUserDoesNotExistException(PsuAuthenticationException ex) {
        log.error("User does not exist exception: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonList(ex.getMessage()));
    }

    @ExceptionHandler({PsuAuthorizationException.class})
    public ResponseEntity<List<String>> handleUnauthorizedException(Exception ex) {
        log.error("Unauthorized exception: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonList(ex.getMessage()));
    }

    @ExceptionHandler({NoProtocolRegisteredException.class})
    public ResponseEntity<List<String>> handleNoProtocolException(Exception ex) {
        log.error("No protocol registered exception: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonList(ex.getMessage()));
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<List<String>> handleException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonList(ex.getMessage()));
    }
}
