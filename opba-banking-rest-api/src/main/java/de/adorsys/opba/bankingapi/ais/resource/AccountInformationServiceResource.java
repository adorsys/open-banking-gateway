package de.adorsys.opba.bankingapi.ais.resource;

import de.adorsys.opba.bankingapi.ais.model.AccountList;
import de.adorsys.opba.bankingapi.ais.model.TransactionsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.UUID;

@Controller
public class AccountInformationServiceResource implements AccountInformationServiceAisApi {
    @Override
    public ResponseEntity<AccountList> getAccounts(UUID xRequestID
                                                  ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<TransactionsResponse> getTransactions(String accountId, UUID xRequestID, LocalDate dateFrom,
                                                                LocalDate dateTo, String entryReferenceFrom,
                                                                String bookingStatus, Boolean deltaList
                                                               ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
