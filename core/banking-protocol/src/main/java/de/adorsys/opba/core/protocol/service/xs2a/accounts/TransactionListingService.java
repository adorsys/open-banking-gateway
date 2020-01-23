package de.adorsys.opba.core.protocol.service.xs2a.accounts;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.core.protocol.service.ContextUtil;
import de.adorsys.opba.core.protocol.service.ValidatedExecution;
import de.adorsys.opba.core.protocol.service.xs2a.context.TransactionListXs2aContext;
import de.adorsys.opba.core.protocol.service.xs2a.dto.Xs2aWithConsentIdHeaders;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.RequestParams;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.TransactionsReport;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("xs2aTransactionListing")
@RequiredArgsConstructor
public class TransactionListingService extends ValidatedExecution<TransactionListXs2aContext> {

    private final Xs2aWithConsentIdHeaders.FromCtx toHeaders;
    private final AccountInformationService ais;

    @Override
    @SuppressWarnings("checkstyle:MagicNumber") // Hardcoded as it is POC, these should be read from context
    protected void doRealExecution(DelegateExecution execution, TransactionListXs2aContext context) {
        Response<TransactionsReport> accounts = ais.getTransactionList(
                context.getResourceId(),
                toHeaders.map(context).toHeaders(),
                RequestParams.fromMap(ImmutableMap.of("bookingStatus", "BOTH", "withBalance", String.valueOf(context.isWithBalance()), "dateFrom", "2018-01-01", "dateTo", "2020-09-30"))
        );

        ContextUtil.setResult(execution, accounts.getBody());
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, TransactionListXs2aContext context) {
        ContextUtil.setResult(execution, new TransactionsReport());
    }
}
