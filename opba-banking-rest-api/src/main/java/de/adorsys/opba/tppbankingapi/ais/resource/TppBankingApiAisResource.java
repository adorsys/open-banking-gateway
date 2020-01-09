package de.adorsys.opba.tppbankingapi.ais.resource;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import de.adorsys.opba.tppbankingapi.ais.model.AccountList;
import de.adorsys.opba.tppbankingapi.ais.model.TransactionsResponse;

@RestController
public class TppBankingApiAisResource implements TppBankingApiAccountInformationServiceAisApi {

    @Override
    public ResponseEntity<AccountList> getAccounts(String authorization, UUID xRequestID, String psuConsentSession) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    @SuppressWarnings("checkstyle:ParameterNumber") // Parameters are provided through auto-generated base class
    public ResponseEntity<TransactionsResponse> getTransactions(String accountId,
                                                                String authorization,
                                                                UUID xRequestID,
                                                                String psuConsentSession,
                                                                LocalDate dateFrom,
                                                                LocalDate dateTo,
                                                                String entryReferenceFrom,
                                                                String bookingStatus,
                                                                Boolean deltaList) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
