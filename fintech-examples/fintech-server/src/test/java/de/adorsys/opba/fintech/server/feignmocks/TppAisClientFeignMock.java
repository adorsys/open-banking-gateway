package de.adorsys.opba.fintech.server.feignmocks;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.fintech.impl.tppclients.TppAisClient;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountList;
import de.adorsys.opba.tpp.ais.api.model.generated.DeleteMetadata;
import de.adorsys.opba.tpp.ais.api.model.generated.SessionStatusDetails;
import de.adorsys.opba.tpp.ais.api.model.generated.TransactionsResponse;
import de.adorsys.opba.tpp.ais.api.model.generated.UpdateAisExternalSessionStatus;
import de.adorsys.opba.tpp.ais.api.model.generated.UpdateMetadata;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public class TppAisClientFeignMock implements TppAisClient {

    @Override
    public ResponseEntity<AccountList> getAccounts(
            String fintechUserID,
            String fintechRedirectURLOK,
            String fintechRedirectURLNOK,
            UUID xRequestID,
            String xTimestampUTC,
            String xRequestSignature,
            String fintechId,
            String serviceSessionPassword,
            String fintechDataPassword,
            UUID bankProfileID,
            Boolean xPsuAuthenticationRequired,
            UUID serviceSessionID,
            String createConsentIfNone,
            String importUserData,
            String protocolConfiguration,
            Boolean computePsuIpAddress,
            String psuIpAddress,
            Boolean fintechDecoupledPreferred,
            String fintechBrandLoggingInformation,
            String fintechNotificationURI,
            String fintechNotificationContentPreferred,
            Boolean withBalance,
            Boolean online
    ) {
        return null;
    }

    @Override
    public ResponseEntity<TransactionsResponse> getTransactions(
            String accountId,
            String fintechUserID,
            String fintechRedirectURLOK,
            String fintechRedirectURLNOK,
            UUID xRequestID,
            String xTimestampUTC,
            String xRequestSignature,
            String fintechId,
            String serviceSessionPassword,
            String fintechDataPassword,
            UUID bankProfileID,
            Boolean xPsuAuthenticationRequired,
            UUID serviceSessionID,
            String createConsentIfNone,
            String importUserData,
            String protocolConfiguration,
            Boolean computePsuIpAddress,
            String psuIpAddress,
            LocalDate dateFrom,
            @Valid LocalDate dateTo,
            String entryReferenceFrom,
            @Valid String bookingStatus,
            @Valid Boolean deltaList,
            Boolean online,
            String analytics,
            Integer page,
            Integer pageSize
    ) {
        return null;
    }

    @Override
    public ResponseEntity<TransactionsResponse> getTransactionsWithoutAccountId(
            String fintechUserID,
            String fintechRedirectURLOK,
            String fintechRedirectURLNOK,
            UUID xRequestID,
            String xTimestampUTC,
            String xRequestSignature,
            String fintechID,
            String serviceSessionPassword,
            String fintechDataPassword,
            UUID bankProfileID,
            Boolean xPsuAuthenticationRequired,
            UUID serviceSessionID,
            String createConsentIfNone,
            String importUserData,
            String protocolConfiguration,
            Boolean computePsuIpAddress,
            String psuIpAddress,
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
    public ResponseEntity<Void> deleteConsent(UUID xRequestID, UUID serviceSessionID, DeleteMetadata requestBody,
                                              String xTimestampUTC, String xRequestSignature, String fintechID, String serviceSessionPassword, String fintechDataPassword, Boolean deleteAll) {
        return null;
    }

    @Override
    public ResponseEntity<SessionStatusDetails> getAisSessionStatus(UUID serviceSessionID, UUID xRequestID, String externalSessionId, String xTimestampUTC, String xRequestSignature, String fintechID,
                                                                    String serviceSessionPassword, String fintechDataPassword) {
        return null;
    }

    @Override
    public ResponseEntity<UpdateAisExternalSessionStatus> updateExternalAisSession(UUID xRequestID, UUID serviceSessionID, UpdateMetadata updateMetadata, String xTimestampUTC, String xRequestSignature, String fintechID,
                                                                                   String serviceSessionPassword, String fintechDataPassword) {
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
