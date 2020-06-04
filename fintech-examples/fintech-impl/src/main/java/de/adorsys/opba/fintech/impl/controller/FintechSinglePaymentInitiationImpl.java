package de.adorsys.opba.fintech.impl.controller;

import de.adorsys.opba.fintech.api.model.generated.SinglePaymentInitiationRequest;
import de.adorsys.opba.fintech.api.resource.generated.FintechSinglePaymentInitiationApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FintechSinglePaymentInitiationImpl implements FintechSinglePaymentInitiationApi {
    public ResponseEntity<Void> initiateSinglePayment(
            SinglePaymentInitiationRequest body,
            UUID xRequestID,
            String X_XSRF_TOKEN,
            String fintechRedirectURLOK,
            String fintechRedirectURLNOK, String bankId) {
        log.info("got payment requrest");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
