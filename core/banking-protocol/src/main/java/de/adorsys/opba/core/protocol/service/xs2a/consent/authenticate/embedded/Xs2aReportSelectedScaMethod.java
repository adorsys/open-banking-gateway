package de.adorsys.opba.core.protocol.service.xs2a.consent.authenticate.embedded;

import de.adorsys.opba.core.protocol.service.ContextUtil;
import de.adorsys.opba.core.protocol.service.ValidatedExecution;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.core.protocol.service.xs2a.dto.Xs2aStandardHeaders;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.SelectPsuAuthenticationMethod;
import de.adorsys.xs2a.adapter.service.model.SelectPsuAuthenticationMethodResponse;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("xs2aReportSelectedScaMethod")
@RequiredArgsConstructor
public class Xs2aReportSelectedScaMethod extends ValidatedExecution<Xs2aContext> {

    private final RuntimeService runtimeService;
    private final AccountInformationService ais;

    // TODO validation projection
    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        Response<SelectPsuAuthenticationMethodResponse> authResponse = ais.updateConsentsPsuData(
            context.getConsentId(),
            context.getAuthorizationId(),
            Xs2aStandardHeaders.FROM_CTX.map(context).toHeaders(),
            selectPsuMethod(context)
        );

        ContextUtil.getAndUpdateContext(
            execution,
            (Xs2aContext ctx) -> ctx.setScaSelected(authResponse.getBody().getChosenScaMethod())
        );
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
        runtimeService.trigger(execution.getId());
    }

    private SelectPsuAuthenticationMethod selectPsuMethod(Xs2aContext context) {
        SelectPsuAuthenticationMethod selectMethod = new SelectPsuAuthenticationMethod();
        selectMethod.setAuthenticationMethodId(context.getUserSelectScaId());
        return selectMethod;
    }
}
