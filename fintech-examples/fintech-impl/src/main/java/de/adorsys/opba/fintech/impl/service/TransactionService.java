package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.generated.InlineResponse2004;
import de.adorsys.opba.fintech.impl.service.mocks.TppListAccountsMock;
import de.adorsys.opba.fintech.impl.tppclients.TppAisClient;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.mapper.ManualMapper;
import de.adorsys.opba.fintech.impl.service.mocks.TppListTransactionsMock;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountList;
import de.adorsys.opba.tpp.ais.api.model.generated.TransactionsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDate;

import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.AUTHORIZATION_SESSION_ID;
import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.PSU_CONSENT_SESSION;
import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.REDIRECT_CODE;
import static org.springframework.http.HttpStatus.SEE_OTHER;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService extends HandleAcceptedService{
    @Value("${mock.tppais.listtransactions}")
    String mockTppAisString;

    private final TppAisClient tppAisClient;

    public ResponseEntity listTransactions(ContextInformation contextInformation, SessionEntity sessionEntity,
                                               String fintechRedirectURLOK, String fintechRedirectURLNOK, String bankId,
                                               String accountId, LocalDate dateFrom, LocalDate dateTo,
                                               String entryReferenceFrom, String bookingStatus, Boolean deltaList) {

        if (mockTppAisString != null && mockTppAisString.equalsIgnoreCase("true") ? true : false) {
            log.warn("mocking call for list transactions");
            return createInlineResponse2004(new TppListTransactionsMock().getTransactionsResponse());
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
                return createInlineResponse2004(transactions.getBody());
            case ACCEPTED:
                return handleAccepted(transactions.getHeaders());
            case UNAUTHORIZED:
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            default:
                throw new RuntimeException("DID NOT EXPECT RETURNCODE:" + transactions.getStatusCode());
        }
    }

    private ResponseEntity createInlineResponse2004(TransactionsResponse transactionsResponse) {
        InlineResponse2004 response = new InlineResponse2004();
        response.setAccountList(ManualMapper.fromTppToFintech(transactionsResponse));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
