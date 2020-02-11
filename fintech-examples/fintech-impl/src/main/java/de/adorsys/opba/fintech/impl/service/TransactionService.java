package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.generated.InlineResponse2004;
import de.adorsys.opba.fintech.impl.config.TppAisClient;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.mapper.ManualMapper;
import de.adorsys.opba.fintech.impl.service.mocks.TppListTransactionsMock;
import de.adorsys.opba.tpp.ais.api.model.generated.TransactionsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {
    @Value("${mock.tppais.listtransactions}")
    String mockTppAisString;

    private final TppAisClient tppAisClient;

    public InlineResponse2004 listTransactions(ContextInformation contextInformation, SessionEntity sessionEntity, String bankId,
                                               String accountId, LocalDate dateFrom, LocalDate dateTo,
                                               String entryReferenceFrom, String bookingStatus, Boolean deltaList) {

        if (BooleanUtils.toBoolean(mockTppAisString)) {
            log.warn("mocking call for list transactions");
            return createInlineResponse2004(new TppListTransactionsMock().getTransactionsResponse());
        }

        return createInlineResponse2004(tppAisClient.getTransactions(
                accountId,
                contextInformation.getFintechID(),
                sessionEntity.getLoginUserName(),
                sessionEntity.getRedirectListTransactions().getOkURL(),
                sessionEntity.getRedirectListTransactions().getNotOkURL(),
                contextInformation.getXRequestID(),
                bankId,
                sessionEntity.getPsuConsentSession(),
                dateFrom,
                dateTo,
                entryReferenceFrom,
                bookingStatus,
                deltaList).getBody());
    }

    private InlineResponse2004 createInlineResponse2004(TransactionsResponse transactionsResponse) {
        InlineResponse2004 inlineResponse2004 = new InlineResponse2004();
        inlineResponse2004.setAccountList(ManualMapper.fromTppToFintech(transactionsResponse));
        return inlineResponse2004;
    }
}
