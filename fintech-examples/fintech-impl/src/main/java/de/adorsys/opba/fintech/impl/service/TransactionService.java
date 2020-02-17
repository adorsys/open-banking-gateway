package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.mapper.ManualMapper;
import de.adorsys.opba.fintech.impl.service.mocks.TppListTransactionsMock;
import de.adorsys.opba.fintech.impl.tppclients.TppAisClient;
import de.adorsys.opba.tpp.ais.api.model.generated.TransactionsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService extends HandleAcceptedService {
    @Value("${mock.tppais.listtransactions:false}")
    String mockTppAisString;

    private final TppAisClient tppAisClient;

    public ResponseEntity listTransactions(ContextInformation contextInformation, SessionEntity sessionEntity,
                                           String fintechRedirectURLOK, String fintechRedirectURLNOK, String bankId,
                                           String accountId, LocalDate dateFrom, LocalDate dateTo,
                                           String entryReferenceFrom, String bookingStatus, Boolean deltaList) {

        if (BooleanUtils.toBoolean(mockTppAisString)) {
            log.warn("mocking call for list transactions");
            return new ResponseEntity<>(ManualMapper.fromTppToFintech(new TppListTransactionsMock().getTransactionsResponse()), HttpStatus.OK);
        }

        ResponseEntity<TransactionsResponse> transactions = tppAisClient.getTransactions(
                accountId,
                contextInformation.getFintechID(),
                sessionEntity.getLoginUserName(),
                fintechRedirectURLOK,
                fintechRedirectURLNOK,
                contextInformation.getXRequestID(),
                bankId,
                sessionEntity.getPsuConsentSession(),
                dateFrom,
                dateTo,
                entryReferenceFrom,
                bookingStatus,
                deltaList);
        switch (transactions.getStatusCode()) {
            case OK:
                return new ResponseEntity<>(ManualMapper.fromTppToFintech(transactions.getBody()), HttpStatus.OK);
            case ACCEPTED:
                return handleAccepted(transactions.getHeaders());
            case UNAUTHORIZED:
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            default:
                throw new RuntimeException("DID NOT EXPECT RETURNCODE:" + transactions.getStatusCode());
        }
    }
}
