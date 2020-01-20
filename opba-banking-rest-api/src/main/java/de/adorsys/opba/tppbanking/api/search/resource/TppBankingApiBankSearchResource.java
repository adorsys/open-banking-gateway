package de.adorsys.opba.tppbanking.api.search.resource;

import de.adorsys.opba.tppbankingapi.search.model.BankProfileResponse;
import de.adorsys.opba.tppbankingapi.search.model.BankSearchResponse;
import de.adorsys.opba.tppbankingapi.search.resource.TppBankSearchApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class TppBankingApiBankSearchResource implements TppBankSearchApi {
    @Override
    public ResponseEntity<BankProfileResponse> bankProfileGET(String authorization, UUID xRequestID, String bankId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    @SuppressWarnings("checkstyle:ParameterNumber") // Parameters are provided through auto-generated base class
    public ResponseEntity<BankSearchResponse> bankSearchGET(String authorization, UUID xRequestID, String keyword,
                                                            Integer start, Integer max) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
