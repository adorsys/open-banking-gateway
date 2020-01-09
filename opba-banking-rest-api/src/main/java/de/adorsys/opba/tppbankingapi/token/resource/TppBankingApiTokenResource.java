package de.adorsys.opba.tppbankingapi.token.resource;


import de.adorsys.opba.tppbankingapi.token.model.PsuConsentSessionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class TppBankingApiTokenResource implements TppTokenApi {
    @Override
    public ResponseEntity<PsuConsentSessionResponse> code2TokenGET(String authorization, UUID xRequestID, String redirectCode) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
