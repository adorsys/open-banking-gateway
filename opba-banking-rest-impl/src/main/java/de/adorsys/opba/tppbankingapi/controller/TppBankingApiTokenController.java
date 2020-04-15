package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.tppbankingapi.token.resource.generated.ConsentConfirmationApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TppBankingApiTokenController implements ConsentConfirmationApi {
    @Override
    public ResponseEntity<Void> confirmConsent(String authorization, UUID xRequestID) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
