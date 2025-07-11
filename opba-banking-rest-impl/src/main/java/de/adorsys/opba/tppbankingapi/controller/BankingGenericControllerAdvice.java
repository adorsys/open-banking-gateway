package de.adorsys.opba.tppbankingapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import java.security.UnrecoverableKeyException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@ControllerAdvice(basePackageClasses = {TppBankingApiAisController.class, TppBankingApiOrchestratedPayment.class})
@Slf4j
public class BankingGenericControllerAdvice {

    @ExceptionHandler({AsyncRequestTimeoutException.class})
    public ResponseEntity<List<String>> handleAsyncTimeout(AsyncRequestTimeoutException ex) {
        log.error("Asynchronous request timed out: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.REQUEST_TIMEOUT)
                .body(List.of(ex.getMessage()));
    }

    @ExceptionHandler({InterruptedException.class})
    public ResponseEntity<List<String>> handleInterrupted(InterruptedException ex) {
        log.error("Interrupted exception: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(List.of(ex.getMessage()));
    }

    @ExceptionHandler({ExecutionException.class})
    public ResponseEntity<List<String>> handleExecution(ExecutionException ex) {
        log.error("Execution exception: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(List.of("Execution exception"));
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<List<String>> handleException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        if (ex.getCause() instanceof UnrecoverableKeyException) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        if (ex instanceof MissingDataProtectionPassword) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(List.of(ex.getMessage()));
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
