package de.adorsys.opba.tppbankingapi.token.resource;

import de.adorsys.opba.tppbankingapi.token.model.InlineResponse201;
import de.adorsys.opba.tppbankingapi.token.model.RedirectCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class TppBankingApiTokenResource implements TppTokenApi {
    @Override
    public ResponseEntity<InlineResponse201> code2TokenGET(UUID xRequestID, RedirectCode redirectCode) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
