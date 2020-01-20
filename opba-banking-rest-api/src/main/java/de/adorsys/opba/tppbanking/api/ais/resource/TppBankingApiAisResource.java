package de.adorsys.opba.tppbanking.api.ais.resource;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import de.adorsys.opba.protocol.services.TppBankingService;
import de.adorsys.opba.protocol.services.ais.account.GetAccountsService;
import de.adorsys.opba.protocol.services.psuconsentsession.PsuConsentSession;
import de.adorsys.opba.protocol.services.ais.account.AccountsReport;
import de.adorsys.opba.protocol.services.psuconsentsession.PsuConsentSessionLoadingService;
import de.adorsys.opba.protocol.services.psuconsentsession.redirect.RedirectionService;
import de.adorsys.opba.tppbankingapi.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import de.adorsys.opba.tppbankingapi.ais.model.AccountList;
import de.adorsys.opba.tppbankingapi.ais.model.TransactionsResponse;

@RestController
@RequiredArgsConstructor
public class TppBankingApiAisResource implements TppBankingApiAccountInformationServiceAisApi {
    private final GetAccountsService accountsSerivce;
    private final AccountListMapper accountListMapper;
    private final PsuConsentSessionLoadingService psuConsentSessionLoadingService;
    private final RedirectionService redirectionService;

    @Override
    public ResponseEntity getAccounts(String authorization,
                                                   String fintechUserID,
                                                   String fintechRedirectURLOK,
                                                   String fintechRedirectURLNOK,
                                                   UUID xRequestID,
                                                   String bankID,
                                                   String psuConsentSession) {

        Optional<PsuConsentSession> existingPsuConsentSession = StringUtils.isNotBlank(psuConsentSession)
                ? psuConsentSessionLoadingService.loadPsuConsentSessionById(psuConsentSession)
                : Optional.empty();
        if (existingPsuConsentSession.isPresent()) {
            Optional<AccountsReport> accountsReport = accountsSerivce.getAccountsFor(existingPsuConsentSession.get());
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.X_REQUEST_ID, xRequestID.toString())
                    .header(HttpHeaders.PSU_CONSENT_SESSION, existingPsuConsentSession.get().getFintechId())
                    .body(accountsReport
                        .map(accountListMapper::toAccountList)
                        .orElseGet(AccountList::new));
        } else {
            PsuConsentSession newPsuConsentSession =
                    psuConsentSessionLoadingService.establishNewPsuConsentSession(bankID,
                            fintechUserID, fintechRedirectURLOK, fintechRedirectURLNOK, TppBankingService.AIS_ACCOUNT_LIST);
            return ResponseEntity
                    .status(HttpStatus.SEE_OTHER)
                    .header(HttpHeaders.X_REQUEST_ID, xRequestID.toString())
                    .header(HttpHeaders.PSU_CONSENT_SESSION, newPsuConsentSession.getFintechId())
                    .location(redirectionService.redirectForAuthorisation(newPsuConsentSession).getRedirectionUrl())
                    .body("Please use redirect link in Location header");
        }
    }

    @Override
    public ResponseEntity<TransactionsResponse> getTransactions(String accountId,
                                                                String fintechUserID,
                                                                String authorization,
                                                                String fintechRedirectURLOK,
                                                                String fintechRedirectURLNOK,
                                                                UUID xRequestID,
                                                                String bankID,
                                                                String psuConsentSession,
                                                                LocalDate dateFrom,
                                                                LocalDate dateTo,
                                                                String entryReferenceFrom,
                                                                String bookingStatus,
                                                                Boolean deltaList) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
