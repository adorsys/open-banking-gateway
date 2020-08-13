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

    public TransactionService(AuthorizeService authorizeService, TppAisClient tppAisClient, FintechUiConfig uiConfig) {
        super(authorizeService);
        this.tppAisClient = tppAisClient;
        this.uiConfig = uiConfig;
    }

    public ResponseEntity listTransactions(SessionEntity sessionEntity,
                                           RedirectUrlsEntity redirectUrlsEntity,
                                           String bankId,
                                           String accountId,
                                           LocalDate dateFrom,
                                           LocalDate dateTo,
                                           String entryReferenceFrom,
                                           String bookingStatus,
                                           Boolean deltaList) {

        if (BooleanUtils.toBoolean(mockTppAisString)) {
            log.warn("mocking call for list transactions");
            return new ResponseEntity<>(ManualMapper.fromTppToFintech(new TppListTransactionsMock().getTransactionsResponse()), HttpStatus.OK);
        }

        ResponseEntity<TransactionsResponse> transactions = tppAisClient.getTransactions(
                accountId,
                tppProperties.getFintechID(),
                tppProperties.getServiceSessionPassword(),
                sessionEntity.getLoginUserName(),
                redirectUrlsEntity.buildOkUrl(uiConfig),
                redirectUrlsEntity.buildNokUrl(uiConfig),
                UUID.fromString(restRequestContext.getRequestId()),
                bankId,
                sessionEntity.getPsuConsentSession(),
                sessionEntity.getServiceSessionId(),
                dateFrom,
                dateTo,
                entryReferenceFrom,
                bookingStatus,
                deltaList);
        switch (transactions.getStatusCode()) {
            case OK:
                return new ResponseEntity<>(ManualMapper.fromTppToFintech(transactions.getBody()), HttpStatus.OK);
            case ACCEPTED:
                return handleAccepted(sessionEntity, transactions.getHeaders());
            case UNAUTHORIZED:
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            default:
                throw new RuntimeException("DID NOT EXPECT RETURNCODE:" + transactions.getStatusCode());
        }
    }
}
