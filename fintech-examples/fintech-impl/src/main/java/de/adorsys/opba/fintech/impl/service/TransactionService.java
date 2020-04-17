package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.config.FintechUiConfig;
import de.adorsys.opba.fintech.impl.controller.RestRequestContext;
import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.mapper.ManualMapper;
import de.adorsys.opba.fintech.impl.properties.TppProperties;
import de.adorsys.opba.fintech.impl.service.mocks.TppListTransactionsMock;
import de.adorsys.opba.fintech.impl.tppclients.TppAisClient;
import de.adorsys.opba.tpp.ais.api.model.generated.TransactionsResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Slf4j
public class TransactionService extends HandleAcceptedService {
    @Value("${mock.tppais.listtransactions:false}")
    String mockTppAisString;

    private final FintechUiConfig uiConfig;
    private final TppAisClient tppAisClient;

    @Autowired
    private RestRequestContext restRequestContext;

    @Autowired
    private TppProperties tppProperties;

    @Autowired
    private RedirectHandlerService redirectHandlerService;

    public TransactionService(AuthorizeService authorizeService, TppAisClient tppAisClient, FintechUiConfig uiConfig) {
        super(authorizeService);
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

        String redirectCode = UUID.randomUUID().toString();

        if (BooleanUtils.toBoolean(mockTppAisString)) {
            log.warn("mocking call for list transactions");
            return new ResponseEntity<>(ManualMapper.fromTppToFintech(new TppListTransactionsMock().getTransactionsResponse()), HttpStatus.OK);
        }
        UUID xRequestId = UUID.fromString(restRequestContext.getRequestId());

        ResponseEntity<TransactionsResponse> transactions = requestGetTransactions(sessionEntity, bankId, accountId,
                                                                                   dateFrom, dateTo, entryReferenceFrom,
                                                                                   bookingStatus, deltaList, redirectCode,
                                                                                   xRequestId);
        switch (transactions.getStatusCode()) {
            case OK:
                return new ResponseEntity<>(ManualMapper.fromTppToFintech(transactions.getBody()), HttpStatus.OK);
            case ACCEPTED:
                log.info("create redirect entity for lot for redirect code {}", redirectCode);
                redirectHandlerService.registerRedirectStateForSession(redirectCode, fintechOkUrl, fintechNOkUrl);
                return handleAccepted(sessionEntity, transactions.getHeaders());
            case UNAUTHORIZED:
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            default:
                throw new RuntimeException("DID NOT EXPECT RETURN CODE:" + transactions.getStatusCode());
        }
    }

    private ResponseEntity<TransactionsResponse> requestGetTransactions(SessionEntity sessionEntity,
                                                                        String bankId,
                                                                        String accountId,
                                                                        LocalDate dateFrom,
                                                                        LocalDate dateTo,
                                                                        String entryReferenceFrom,
                                                                        String bookingStatus,
                                                                        Boolean deltaList,
                                                                        String redirectCode,
                                                                        UUID xRequestId) {
        return tppAisClient.getTransactions(
                accountId,
                tppProperties.getServiceSessionPassword(),
                sessionEntity.getLoginUserName(),
                RedirectUrlsEntity.buildOkUrl(uiConfig, redirectCode),
                RedirectUrlsEntity.buildNokUrl(uiConfig, redirectCode),
                xRequestId,
                null,
                null,
                null,
                bankId,
                sessionEntity.getPsuConsentSession(),
                sessionEntity.getConsentConfirmed() ? sessionEntity.getServiceSessionId() : null,
                dateFrom,
                dateTo,
                entryReferenceFrom,
                bookingStatus,
                deltaList);
    }
}
