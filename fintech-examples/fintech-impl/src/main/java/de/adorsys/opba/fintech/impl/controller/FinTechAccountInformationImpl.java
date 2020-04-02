package de.adorsys.opba.fintech.impl.controller;

import de.adorsys.opba.fintech.api.model.generated.AccountList;
import de.adorsys.opba.fintech.api.model.generated.TransactionsResponse;
import de.adorsys.opba.fintech.api.resource.generated.FinTechAccountInformationApi;
import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
import de.adorsys.opba.fintech.impl.database.entities.RequestAction;
import de.adorsys.opba.fintech.impl.database.entities.RequestInfoEntity;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.service.AccountService;
import de.adorsys.opba.fintech.impl.service.AuthorizeService;
import de.adorsys.opba.fintech.impl.service.ContextInformation;
import de.adorsys.opba.fintech.impl.service.RedirectHandlerService;
import de.adorsys.opba.fintech.impl.service.RequestInfoService;
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
    private final RedirectHandlerService redirectHandlerService;
    private final RequestInfoService requestInfoService;

    // uaContext
    @Override
    public ResponseEntity<AccountList> aisAccountsGET(String bankId, UUID xRequestID, String xsrfToken, String fintechRedirectURLOK, String fintechRedirectURLNOK) {


        ContextInformation contextInformation = new ContextInformation();
        if (!authorizeService.isAuthorized()) {
            log.warn("Request was failed: Xsrf Token is wrong or user are not authorized!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        SessionEntity sessionEntity = authorizeService.getByXsrfToken(xsrfToken);

        RedirectUrlsEntity redirectUrlsEntity = redirectHandlerService.registerRedirectStateForSession(xsrfToken, fintechRedirectURLOK, fintechRedirectURLNOK);
        RequestInfoEntity info = requestInfoService.addRequestInfo(xsrfToken, bankId, RequestAction.LIST_ACCOUNTS);

        return accountService.listAccounts(contextInformation, sessionEntity, redirectUrlsEntity, info);
    }

    @Override
    public ResponseEntity<TransactionsResponse> aisTransactionsGET(String bankId, String accountId, UUID xRequestID,
                                                                            String xsrfToken, String fintechRedirectURLOK, String fintechRedirectURLNOK,
                                                                            LocalDate dateFrom, LocalDate dateTo,
                                                                            String entryReferenceFrom, String bookingStatus, Boolean deltaList) {
        if (!authorizeService.isAuthorized()) {
            log.warn("Request was failed: Xsrf Token is wrong or user are not authorized!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        ContextInformation contextInformation = new ContextInformation();

        SessionEntity sessionEntity = authorizeService.getByXsrfToken(xsrfToken);

        RedirectUrlsEntity redirectUrlsEntity = redirectHandlerService.registerRedirectStateForSession(xsrfToken, fintechRedirectURLOK, fintechRedirectURLNOK);
        RequestInfoEntity info = requestInfoService.addRequestInfo(xsrfToken, bankId, RequestAction.LIST_TRANSACTIONS, accountId, dateFrom, dateTo, entryReferenceFrom, bookingStatus, deltaList);

        return transactionService.listTransactions(contextInformation, sessionEntity, redirectUrlsEntity, info);
    }
}
