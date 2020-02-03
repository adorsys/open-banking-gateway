package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.generated.InlineResponse2004;
import de.adorsys.opba.fintech.api.model.generated.TransactionsResponse;
import de.adorsys.opba.fintech.impl.config.TppAisClient;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.service.mapper.ManualMapper;
import de.adorsys.opba.fintech.impl.service.mocks.TppListTransactionsMock;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountReport;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
@NoArgsConstructor
public class TransactionService {

    @Value("real.tpp.ais")
    String realTppAISString;
    Boolean mockTppAIS = realTppAISString != null && realTppAISString.equalsIgnoreCase("true") ? false : true;

    // Todo with RequiredArgsConstructor
    @Autowired
    TppAisClient tppAisClient;

    public InlineResponse2004 listTransactions(ContextInformation contextInformation, SessionEntity sessionEntity, String bankId,
                                               String accountId, LocalDate dateFrom, LocalDate dateTo,
                                               String entryReferenceFrom, String bookingStatus, Boolean deltaList) {

        AccountReport accountReport = null;
        if (!mockTppAIS) {
            accountReport = tppAisClient.getTransactions(
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
                    deltaList).getBody().getTransactions();
        }

        if (mockTppAIS) {
            accountReport = new TppListTransactionsMock().getTransactionList();
        }

        InlineResponse2004 inlineResponse2004 = new InlineResponse2004();
        de.adorsys.opba.fintech.api.model.generated.AccountReport finTechAccountReport = ManualMapper.fromTppToFintech(accountReport);
        TransactionsResponse transactionsResponse = new TransactionsResponse();
        transactionsResponse.setTransactions(finTechAccountReport);
        inlineResponse2004.setAccountList(transactionsResponse);
        return inlineResponse2004;
    }
}
