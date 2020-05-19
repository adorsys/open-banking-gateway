package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.api.security.external.domain.OperationType;
import de.adorsys.opba.fintech.impl.config.FintechUiConfig;
import de.adorsys.opba.fintech.impl.controller.RestRequestContext;
import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.mapper.ManualMapper;
import de.adorsys.opba.fintech.impl.properties.CookieConfigProperties;
import de.adorsys.opba.fintech.impl.properties.TppProperties;
import de.adorsys.opba.fintech.impl.tppclients.TppAisClient;
import de.adorsys.opba.tpp.ais.api.model.generated.TransactionsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_FINTECH_ID;
import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_X_REQUEST_SIGNATURE;
import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_X_TIMESTAMP_UTC;

@Service
@Slf4j
public class TransactionService extends HandleAcceptedService {
    private final FintechUiConfig uiConfig;
    private final TppAisClient tppAisClient;

    @Autowired
    private RestRequestContext restRequestContext;

    @Autowired
    private TppProperties tppProperties;

    @Autowired
    private RedirectHandlerService redirectHandlerService;

    public TransactionService(AuthorizeService authorizeService, TppAisClient tppAisClient, FintechUiConfig uiConfig,
                              CookieConfigProperties cookieConfigProperties, RestRequestContext restRequestContext) {
        super(authorizeService, cookieConfigProperties, restRequestContext);
        this.tppAisClient = tppAisClient;
        this.uiConfig = uiConfig;
    }

    public ResponseEntity listTransactions(SessionEntity sessionEntity,
                                           String fintechOkUrl,
                                           String fintechNOkUrl,
                                           String bankId,
                                           String accountId,
                                           LocalDate dateFrom,
                                           LocalDate dateTo,
                                           String entryReferenceFrom,
                                           String bookingStatus,
                                           Boolean deltaList) {

        String fintechRedirectCode = UUID.randomUUID().toString();

        ResponseEntity<TransactionsResponse> transactions = tppAisClient.getTransactions(
                accountId,
                tppProperties.getServiceSessionPassword(),
                sessionEntity.getUserEntity().getLoginUserName(),
                RedirectUrlsEntity.buildOkUrl(uiConfig, fintechRedirectCode),
                RedirectUrlsEntity.buildNokUrl(uiConfig, fintechRedirectCode),
                UUID.fromString(restRequestContext.getRequestId()),
                COMPUTE_X_TIMESTAMP_UTC,
                OperationType.AIS.toString(),
                COMPUTE_X_REQUEST_SIGNATURE,
                COMPUTE_FINTECH_ID,
                bankId,
                null,
                sessionEntity.getConsentConfirmed() ? sessionEntity.getTppServiceSessionId() : null,
                dateFrom,
                dateTo,
                entryReferenceFrom,
                bookingStatus,
                deltaList);
        switch (transactions.getStatusCode()) {
            case OK:
                return new ResponseEntity<>(ManualMapper.fromTppToFintech(transactions.getBody()), HttpStatus.OK);
            case ACCEPTED:
                log.info("create redirect entity for lot for redirectcode {}", fintechRedirectCode);
                redirectHandlerService.registerRedirectStateForSession(fintechRedirectCode, fintechOkUrl, fintechNOkUrl);
                return handleAccepted(fintechRedirectCode, sessionEntity, transactions.getHeaders());
            case UNAUTHORIZED:
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            default:
                throw new RuntimeException("DID NOT EXPECT RETURNCODE:" + transactions.getStatusCode());
        }
    }
}
