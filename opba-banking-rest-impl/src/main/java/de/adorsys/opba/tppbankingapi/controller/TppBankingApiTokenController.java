package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.tppbankingapi.service.ConsentConfirmationService;
import de.adorsys.opba.tppbankingapi.token.resource.generated.ConsentConfirmationApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TppBankingApiTokenController implements ConsentConfirmationApi {

    private final ConsentConfirmationService consentConfirmationService;

    @Override
    public ResponseEntity<Void> confirmConsent(String authId, UUID xRequestID) {
        if (consentConfirmationService.confirmConsent(authId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
