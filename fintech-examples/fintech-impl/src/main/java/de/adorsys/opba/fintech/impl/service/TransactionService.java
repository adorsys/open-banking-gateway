package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
import de.adorsys.opba.fintech.impl.database.entities.RequestInfoEntity;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.mapper.ManualMapper;
import de.adorsys.opba.fintech.impl.service.mocks.TppListTransactionsMock;
import de.adorsys.opba.fintech.impl.tppclients.TppAisClient;
import de.adorsys.opba.tpp.ais.api.model.generated.TransactionsResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TransactionService extends HandleAcceptedService {
    @Value("${mock.tppais.listtransactions:false}")
    String mockTppAisString;

    private final TppAisClient tppAisClient;

    public TransactionService(AuthorizeService authorizeService, TppAisClient tppAisClient) {
        super(authorizeService);
        this.tppAisClient = tppAisClient;
    }

    public ResponseEntity listTransactions(ContextInformation contextInformation,
                                           SessionEntity sessionEntity,
                                           RedirectUrlsEntity redirectUrlsEntity,
                                           RequestInfoEntity requestInfoEntity) {

        if (BooleanUtils.toBoolean(mockTppAisString)) {
            log.warn("mocking call for list transactions");
            return new ResponseEntity<>(ManualMapper.fromTppToFintech(new TppListTransactionsMock().getTransactionsResponse()), HttpStatus.OK);
        }

        ResponseEntity<TransactionsResponse> transactions = tppAisClient.getTransactions(
                requestInfoEntity.getAccountId(),
                contextInformation.getFintechID(),
                contextInformation.getServiceSessionPassword(),
                sessionEntity.getLoginUserName(),
                redirectUrlsEntity.getOkURL(),
                redirectUrlsEntity.getNotOkURL(),
                contextInformation.getXRequestID(),
                requestInfoEntity.getBankId(),
                sessionEntity.getPsuConsentSession(),
                sessionEntity.getServiceSessionID(),
                requestInfoEntity.getDateFrom(),
                requestInfoEntity.getDateTo(),
                requestInfoEntity.getEntryReferenceFrom(),
                requestInfoEntity.getBookingStatus(),
                requestInfoEntity.getDeltaList());
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
