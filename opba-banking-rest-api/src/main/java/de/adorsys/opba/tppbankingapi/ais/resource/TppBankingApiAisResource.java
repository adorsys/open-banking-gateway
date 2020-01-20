package de.adorsys.opba.tppbankingapi.ais.resource;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import de.adorsys.opba.tppbanking.services.ais.account.GetAccountsService;
import de.adorsys.opba.tppbanking.services.psuconsentsession.PsuConsentSession;
import de.adorsys.opba.tppbanking.services.ais.account.AccountsReport;
import de.adorsys.opba.tppbanking.services.psuconsentsession.PsuConsentSessionLoadingService;
import de.adorsys.opba.tppbanking.services.psuconsentsession.redirect.RedirectionService;
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
    public ResponseEntity<AccountList> getAccounts(String authorization, UUID xRequestID, String psuConsentSession) {
        Optional<PsuConsentSession> existingPsuConsentSession = StringUtils.isNotBlank(psuConsentSession)
                ? psuConsentSessionLoadingService.loadPsuConsentSessionById(psuConsentSession)
                : Optional.empty();
        if (existingPsuConsentSession.isPresent()) {
            Optional<AccountsReport> accountsReport = accountsSerivce.getAccountsFor(existingPsuConsentSession.get());

            return ResponseEntity.of(accountsReport
                                    .map(accountListMapper::toAccountList));
        } else {
            return new ResponseEntity(redirectionService.redirectForAuthorisation(), HttpStatus.SEE_OTHER);
        }
    }

    @Override
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
