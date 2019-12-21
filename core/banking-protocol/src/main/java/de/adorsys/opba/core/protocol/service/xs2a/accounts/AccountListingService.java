package de.adorsys.opba.core.protocol.service.xs2a.accounts;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.core.protocol.service.ValidatedExecution;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.RequestParams;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.AccountListHolder;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.CONTEXT;

@Service("xs2aAccountListing")
@RequiredArgsConstructor
public class AccountListingService extends ValidatedExecution<Xs2aContext> {

    private final AccountInformationService ais;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        Response<AccountListHolder> accounts = ais.getAccountList(
                context.toHeaders(),
                RequestParams.fromMap(ImmutableMap.of("withBalance", String.valueOf(context.isWithBalance())))
        );

        context.setResult(accounts.getBody());
        execution.setVariable(CONTEXT, context);
    }
}
