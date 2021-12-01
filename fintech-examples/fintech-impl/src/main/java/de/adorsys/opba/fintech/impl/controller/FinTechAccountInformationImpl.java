package de.adorsys.opba.fintech.impl.controller;

import de.adorsys.opba.fintech.api.model.generated.AccountList;
import de.adorsys.opba.fintech.api.model.generated.TransactionsResponse;
import de.adorsys.opba.fintech.api.resource.generated.FinTechAccountInformationApi;
import de.adorsys.opba.fintech.impl.controller.utils.LoARetrievalInformation;
import de.adorsys.opba.fintech.impl.controller.utils.LoTRetrievalInformation;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.service.AccountService;
import de.adorsys.opba.fintech.impl.service.ConsentService;
import de.adorsys.opba.fintech.impl.service.SessionLogicService;
import de.adorsys.opba.fintech.impl.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FinTechAccountInformationImpl implements FinTechAccountInformationApi {

    private final SessionLogicService sessionLogicService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final ConsentService consentService;


    @Override
    public ResponseEntity<AccountList> aisAccountsGET(String bankId, UUID xRequestID, String xsrfToken, String fintechRedirectURLOK, String fintechRedirectURLNOK,
                                                      String loARetrievalInformation, Boolean xPsuAuthenticationRequired, String createConsentIfNone,
                                                      Boolean fintechDecoupledPreferred, String fintechBrandLoggingInformation, String fintechNotificationURI,
                                                      String fintechRedirectNotificationContentPreferred, Boolean withBalance, Boolean online) {
        if (!sessionLogicService.isSessionAuthorized()) {
            log.warn("aisAccountsGET failed: user is not authorized!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        SessionEntity sessionEntity = sessionLogicService.getSession();
        return sessionLogicService.addSessionMaxAgeToHeader(
                accountService.listAccounts(sessionEntity, fintechRedirectURLOK, fintechRedirectURLNOK, bankId, LoARetrievalInformation.valueOf(loARetrievalInformation),
                        createConsentIfNone, fintechDecoupledPreferred, fintechBrandLoggingInformation, fintechNotificationURI, fintechRedirectNotificationContentPreferred,
                        withBalance, xPsuAuthenticationRequired, online));
    }

    @Override
    public ResponseEntity<TransactionsResponse> aisTransactionsGET(String bankId, String accountId, UUID xRequestID, String xsrfToken, String fintechRedirectURLOK, String fintechRedirectURLNOK,
                                                                   String loTRetrievalInformation, Boolean xPsuAuthenticationRequired, String createConsentIfNone, LocalDate dateFrom,
                                                                   LocalDate dateTo, String entryReferenceFrom, String bookingStatus, Boolean deltaList, Boolean online) {
        if (!sessionLogicService.isSessionAuthorized()) {
            log.warn("aisTransactionsGET failed: user is not authorized!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        SessionEntity sessionEntity = sessionLogicService.getSession();
        return sessionLogicService.addSessionMaxAgeToHeader(
                transactionService.listTransactions(sessionEntity, fintechRedirectURLOK, fintechRedirectURLNOK,
                        bankId, accountId, createConsentIfNone, dateFrom, dateTo, entryReferenceFrom, bookingStatus, deltaList,
                        LoTRetrievalInformation.valueOf(loTRetrievalInformation), xPsuAuthenticationRequired, online));
    }

    @Override
    public ResponseEntity<Object> aisConsentsDELETE(String bankId, UUID xRequestID, String xsrfToken) {
        SessionEntity sessionEntity = sessionLogicService.getSession();
        consentService.deleteAllConsentsOfBank(sessionEntity, bankId);
        return ResponseEntity.ok().body(Map.of());
    }
}
