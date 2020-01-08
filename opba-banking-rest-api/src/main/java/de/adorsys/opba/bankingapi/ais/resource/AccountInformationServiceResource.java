package de.adorsys.opba.bankingapi.ais.resource;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import de.adorsys.opba.bankingapi.ais.model.AccountList;
import de.adorsys.opba.bankingapi.ais.model.TransactionsResponse;

@RestController
public class AccountInformationServiceResource implements AccountInformationServiceAisApi {
  @Override
  public ResponseEntity<AccountList> getAccounts(UUID xRequestID) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @Override
  @SuppressWarnings("checkstyle:ParameterNumber") // Parameters are provided through auto-generated base class
  public ResponseEntity<TransactionsResponse> getTransactions(String accountId, UUID xRequestID, LocalDate dateFrom,
      LocalDate dateTo, String entryReferenceFrom, String bookingStatus, Boolean deltaList) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }
}
