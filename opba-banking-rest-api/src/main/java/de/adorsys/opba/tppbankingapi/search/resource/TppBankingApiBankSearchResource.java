package de.adorsys.opba.tppbankingapi.search.resource;

import de.adorsys.opba.tppbankingapi.search.model.InlineResponse200;
import de.adorsys.opba.tppbankingapi.search.model.InlineResponse2001;
import de.adorsys.opba.tppbankingapi.search.model.SearchInput;
import de.adorsys.opba.tppbankingapi.search.model.SearchMaxResult;
import de.adorsys.opba.tppbankingapi.search.model.SearchStartIndex;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class TppBankingApiBankSearchResource implements TppBankSearchApi {
    @Override
    public ResponseEntity<InlineResponse2001> bankProfileGET(String authorization, UUID xRequestID, String bankId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    @SuppressWarnings("checkstyle:ParameterNumber") // Parameters are provided through auto-generated base class
    public ResponseEntity<InlineResponse200> bankSearchGET(String authorization, UUID xRequestID, SearchInput keyword, SearchStartIndex start, SearchMaxResult max) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
