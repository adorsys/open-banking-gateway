package de.adorsys.opba.fintech.impl.controller;

import de.adorsys.opba.fintech.impl.exceptions.EmailNotAllowed;
import de.adorsys.opba.fintech.impl.exceptions.EmailNotVerified;
import de.adorsys.opba.fintech.impl.exceptions.Oauth2UnauthorizedException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GenericControllerAdvice {

    @ExceptionHandler({EmailNotVerified.class, EmailNotAllowed.class})
    public ResponseEntity<OAuth2Error> handleOauth2Errors(Exception ex) {
        log.error("OAuth2 error occurred", ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new OAuth2Error(ex.getMessage()));
    }

    @ExceptionHandler({Oauth2UnauthorizedException.class})
    public ResponseEntity<OAuth2Error> handleOauth2UnauthorizedErrors(Exception ex) {
        log.error("OAuth2 error occurred", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new OAuth2Error(""));
    }

    @Getter
    @AllArgsConstructor
    public static class OAuth2Error {
        private final String message;
    }
}
