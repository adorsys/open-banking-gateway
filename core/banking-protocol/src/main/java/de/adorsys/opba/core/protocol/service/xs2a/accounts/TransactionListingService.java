package de.adorsys.opba.core.protocol.service.xs2a.accounts;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.core.protocol.service.xs2a.context.TransactionListXs2aContext;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.RequestParams;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.TransactionsReport;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.CONTEXT;

@Service("xs2aTransactionListing")
@RequiredArgsConstructor
public class TransactionListingService implements JavaDelegate {

    private final AccountInformationService ais;

    @Override
    @Transactional
    @SuppressWarnings("checkstyle:MagicNumber") // Hardcoded as it is POC, these should be read from context
    public void execute(DelegateExecution delegateExecution) {
        TransactionListXs2aContext context = delegateExecution.getVariable(CONTEXT, TransactionListXs2aContext.class);

        Response<TransactionsReport> accounts = ais.getTransactionList(
                context.getResourceId(),
                context.toHeaders(),
                RequestParams.fromMap(ImmutableMap.of("bookingStatus", "BOTH", "withBalance", String.valueOf(context.isWithBalance()), "dateFrom", "2018-01-01", "dateTo", "2020-09-30"))
        );

        context.setResult(accounts.getBody());
        delegateExecution.setVariable(CONTEXT, context);
    }
}
