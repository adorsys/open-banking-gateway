package de.adorsys.opba.fintech.server.feignmocks;

import de.adorsys.opba.fintech.impl.tppclients.TppAisClient;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountList;
import de.adorsys.opba.tpp.ais.api.model.generated.TransactionsResponse;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;

public class TppAisClientFeignMock implements TppAisClient {

    @Override
    public ResponseEntity<AccountList> getAccounts(String authorization, String fintechUserID, String fintechRedirectURLOK, String fintechRedirectURLNOK, UUID xRequestID, String bankID, String psUConsentSession) {
        return null;
    }

    @Override
    public ResponseEntity<TransactionsResponse> getTransactions(String accountId, String authorization, String fintechUserID, String fintechRedirectURLOK, String fintechRedirectURLNOK, UUID xRequestID, String bankID, String psUConsentSession, @Valid LocalDate dateFrom, @Valid LocalDate dateTo, @Valid String entryReferenceFrom, @Valid String bookingStatus, @Valid Boolean deltaList) {
        return null;
    }
}
