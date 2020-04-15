package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.tppbankingapi.token.model.generated.PsuConsentSession;
import de.adorsys.opba.tppbankingapi.token.model.generated.PsuConsentSessionResponse;
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
    public ResponseEntity<PsuConsentSessionResponse> confirmConsent(String authorization, UUID xRequestID, String redirectCode) {
        PsuConsentSession psuConsentSession = new PsuConsentSession();
        psuConsentSession.setValue("Confirmed");
        PsuConsentSessionResponse psuConsentSessionResponse = new PsuConsentSessionResponse();
        psuConsentSessionResponse.setPsuConsentSession(psuConsentSession);
        return new ResponseEntity<>(psuConsentSessionResponse, HttpStatus.OK);
    }
}
