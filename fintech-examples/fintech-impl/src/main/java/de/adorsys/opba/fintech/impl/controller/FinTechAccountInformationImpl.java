package de.adorsys.opba.fintech.impl.controller;

import de.adorsys.opba.fintech.api.model.generated.AccountList;
import de.adorsys.opba.fintech.api.model.generated.TransactionsResponse;
import de.adorsys.opba.fintech.api.resource.generated.FinTechAccountInformationApi;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.service.AccountService;
import de.adorsys.opba.fintech.impl.service.AuthorizeService;
import de.adorsys.opba.fintech.impl.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FinTechAccountInformationImpl implements FinTechAccountInformationApi {

    private final AuthorizeService authorizeService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    @Override
    public ResponseEntity<AccountList> aisAccountsGET(String bankId, UUID xRequestID, String xsrfToken, String fintechRedirectURLOK, String fintechRedirectURLNOK) {


        if (!authorizeService.isAuthorized()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        SessionEntity sessionEntity = authorizeService.getSession();

        return accountService.listAccounts(sessionEntity, fintechRedirectURLOK, fintechRedirectURLNOK, bankId);
    }

    @Override
    public ResponseEntity<TransactionsResponse> aisTransactionsGET(String bankId, String accountId, UUID xRequestID,
                                                                   String xsrfToken, String fintechRedirectURLOK, String fintechRedirectURLNOK,
                                                                   LocalDate dateFrom, LocalDate dateTo,
                                                                   String entryReferenceFrom, String bookingStatus, Boolean deltaList) {
        if (!authorizeService.isAuthorized()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        SessionEntity sessionEntity = authorizeService.getSession();

        return transactionService.listTransactions(sessionEntity, fintechRedirectURLOK, fintechRedirectURLNOK,
                bankId, accountId, dateFrom, dateTo, entryReferenceFrom, bookingStatus, deltaList);
    }
}
