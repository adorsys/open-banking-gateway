package de.adorsys.opba.tppbankingapi.token;


import de.adorsys.opba.tppbankingapi.token.model.generated.PsuConsentSessionResponse;
import de.adorsys.opba.tppbankingapi.token.resource.generated.TppTokenApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class TppBankingApiTokenResource implements TppTokenApi {
    @Override
    public ResponseEntity<PsuConsentSessionResponse> code2TokenGET(
            UUID xRequestID,
            String redirectCode,
            String xTimestampUTC,
            String xRequestSignature,
            String fintechId) {

        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
