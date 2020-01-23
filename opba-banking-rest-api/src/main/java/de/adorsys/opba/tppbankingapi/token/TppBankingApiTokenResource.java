package de.adorsys.opba.tppbankingapi.token;


import de.adorsys.opba.tppbankingapi.token.model.PsuConsentSessionResponse;
import de.adorsys.opba.tppbankingapi.token.resource.TppTokenApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class TppBankingApiTokenResource implements TppTokenApi {
    @Override
    public ResponseEntity<PsuConsentSessionResponse> code2TokenGET(String authorization, UUID xRequestID, String redirectCode) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
