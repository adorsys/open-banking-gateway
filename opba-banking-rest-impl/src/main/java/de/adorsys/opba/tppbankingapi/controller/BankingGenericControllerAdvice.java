package de.adorsys.opba.tppbankingapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.security.UnrecoverableKeyException;
import java.util.List;

@ControllerAdvice(basePackageClasses = {TppBankingApiAisController.class})
@Slf4j
public class BankingGenericControllerAdvice {

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<List<String>> handleException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        if (ex.getCause() instanceof UnrecoverableKeyException) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        if (ex instanceof IllegalArgumentException) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(List.of(ex.getMessage()));
        }
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }
}
