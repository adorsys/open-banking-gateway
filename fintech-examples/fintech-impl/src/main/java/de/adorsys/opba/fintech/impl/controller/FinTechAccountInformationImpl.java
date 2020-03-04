package de.adorsys.opba.fintech.impl.controller;

import de.adorsys.opba.fintech.api.model.generated.AccountList;
import de.adorsys.opba.fintech.api.model.generated.TransactionsResponse;
import de.adorsys.opba.fintech.api.resource.generated.FinTechAccountInformationApi;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.service.AccountService;
import de.adorsys.opba.fintech.impl.service.AuthorizeService;
import de.adorsys.opba.fintech.impl.service.ContextInformation;
import de.adorsys.opba.fintech.impl.service.TransactionService;
import de.adorsys.opba.fintech.impl.service.RedirectHandlerService;
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
    private final RedirectHandlerService redirectHandlerService;

    @Override
    public ResponseEntity<AccountList> aisAccountsGET(String bankId, UUID xRequestID, String xsrfToken, String fintechRedirectURLOK, String fintechRedirectURLNOK) {
        if (!authorizeService.isAuthorized(xsrfToken, null)) {
            log.warn("Request was failed: Xsrf Token is wrong or user are not authorized!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        ContextInformation contextInformation = new ContextInformation(xRequestID);
        SessionEntity sessionEntity = authorizeService.getByXsrfToken(xsrfToken);
        redirectHandlerService.registerRedirectUrlForSession(xsrfToken, fintechRedirectURLOK, fintechRedirectURLNOK);
        return accountService.listAccounts(contextInformation, sessionEntity, bankId, fintechRedirectURLOK, fintechRedirectURLNOK);
    }

    @Override
    public ResponseEntity<TransactionsResponse> aisTransactionsGET(String bankId, String accountId, UUID xRequestID,
                                                                            String xsrfToken, String fintechRedirectURLOK, String fintechRedirectURLNOK,
                                                                            LocalDate dateFrom, LocalDate dateTo,
                                                                            String entryReferenceFrom, String bookingStatus, Boolean deltaList) {
        if (!authorizeService.isAuthorized(xsrfToken, null)) {
            log.warn("Request was failed: Xsrf Token is wrong or user are not authorized!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        ContextInformation contextInformation = new ContextInformation(xRequestID);
        SessionEntity sessionEntity = authorizeService.getByXsrfToken(xsrfToken);

        redirectHandlerService.registerRedirectUrlForSession(xsrfToken, fintechRedirectURLOK, fintechRedirectURLNOK);

        return transactionService.listTransactions(contextInformation, sessionEntity, fintechRedirectURLOK,
                fintechRedirectURLNOK, bankId, accountId, dateFrom, dateTo, entryReferenceFrom, bookingStatus, deltaList);
    }
}
