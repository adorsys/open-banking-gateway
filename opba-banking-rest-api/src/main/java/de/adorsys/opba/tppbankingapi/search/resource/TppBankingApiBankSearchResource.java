package de.adorsys.opba.tppbankingapi.search.resource;

import de.adorsys.opba.tppbankingapi.search.model.InlineResponse200;
import de.adorsys.opba.tppbankingapi.search.model.InlineResponse2001;
import de.adorsys.opba.tppbankingapi.search.model.SearchInput;
import de.adorsys.opba.tppbankingapi.search.model.SearchMaxResult;
import de.adorsys.opba.tppbankingapi.search.model.SearchStartIndex;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class TppBankingApiBankSearchResource implements TppBankSearchApi {
    @Override
    public ResponseEntity<InlineResponse2001> bankProfileGET(UUID xRequestID, String bankId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<InlineResponse200> bankSearchGET(UUID xRequestID, SearchInput keyword, SearchStartIndex start, SearchMaxResult max) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
