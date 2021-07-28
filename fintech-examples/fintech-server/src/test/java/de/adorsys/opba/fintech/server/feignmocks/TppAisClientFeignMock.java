package de.adorsys.opba.fintech.server.feignmocks;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.fintech.impl.tppclients.TppAisClient;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountList;
import de.adorsys.opba.tpp.ais.api.model.generated.TransactionsResponse;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public class TppAisClientFeignMock implements TppAisClient {

    @Override
    public ResponseEntity<AccountList> getAccounts(
            String serviceSessionPassword,
            String fintechUserID,
            String fintechRedirectURLOK,
            String fintechRedirectURLNOK,
            UUID xRequestID,
            String xTimestampUTC,
            String xRequestSignature,
            String fintechId,
            String bankID,
            Boolean xPsuAuthenticationRequired,
            UUID serviceSessionID,
            String createConsentIfNone,
            Boolean useObgCache,
            Boolean withBalance
    ) {
        return null;
    }

    @Override
    public ResponseEntity<TransactionsResponse> getTransactions(
            String accountId,
            String serviceSessionPassword,
            String fintechUserID,
            String fintechRedirectURLOK,
            String fintechRedirectURLNOK,
            UUID xRequestID,
            String xTimestampUTC,
            String xRequestSignature,
            String fintechId,
            String bankID,
            Boolean xPsuAuthenticationRequired,
            UUID serviceSessionID,
            String createConsentIfNone,
            LocalDate dateFrom,
            @Valid LocalDate dateTo,
            String entryReferenceFrom,
            @Valid String bookingStatus,
            @Valid Boolean deltaList,
            Boolean online,
            Boolean analytics,
            Integer page,
            Integer pageSize
    ) {
        return null;
    }

    @Override
    public ResponseEntity<TransactionsResponse> getTransactionsWithoutAccountId(
            String serviceSessionPassword,
            String fintechUserID,
            String fintechRedirectURLOK,
            String fintechRedirectURLNOK,
            UUID xRequestID,
            String xTimestampUTC,
            String xRequestSignature,
            String fintechID,
            String bankID,
            Boolean xPsuAuthenticationRequired,
            UUID serviceSessionID,
            String createConsentIfNone,
            LocalDate dateFrom,
            LocalDate dateTo,
            String entryReferenceFrom,
            String bookingStatus,
            Boolean deltaList,
            Integer page,
            Integer pageSize
    ) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteConsent(UUID serviceSessionID, String serviceSessionPassword, UUID xRequestID,
                                              String xTimestampUTC, String xRequestSignature, String fintechID, Boolean deleteAll) {
        return null;
    }

    // TODO: https://github.com/adorsys/open-banking-gateway/issues/559
    @Override
    public Optional<ObjectMapper> getObjectMapper() {
        return Optional.empty();
    }

    // TODO: https://github.com/adorsys/open-banking-gateway/issues/559
    @Override
    public Optional<HttpServletRequest> getRequest() {
        return Optional.empty();
    }
}
