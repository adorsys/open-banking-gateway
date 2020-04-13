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
    public ResponseEntity<AccountList> getAccounts(String authorization, String serviceSessionPassword, String fintechUserID, String fintechRedirectURLOK, String fintechRedirectURLNOK, UUID xRequestID, String bankID, String psUConsentSession, UUID serviceSessionID) {
        return null;
    }

    @Override
    public ResponseEntity<TransactionsResponse> getTransactions(String accountId, String authorization, String serviceSessionPassword, String fintechUserID, String fintechRedirectURLOK, String fintechRedirectURLNOK, UUID xRequestID, String bankID, String psUConsentSession, UUID serviceSessionID, @Valid LocalDate dateFrom, @Valid LocalDate dateTo, @Valid String entryReferenceFrom, @Valid String bookingStatus, @Valid Boolean deltaList) {
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
