package de.adorsys.opba.core.protocol.service.xs2a.consent.authorize.embedded;

import de.adorsys.opba.core.protocol.service.ContextUtil;
import de.adorsys.opba.core.protocol.service.ValidatedExecution;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.ScaStatusResponse;
import de.adorsys.xs2a.adapter.service.model.TransactionAuthorisation;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("xs2aReportScaChallenge")
@RequiredArgsConstructor
public class Xs2aReportScaChallenge extends ValidatedExecution<Xs2aContext> {

    private final RuntimeService runtimeService;
    private final AccountInformationService ais;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        Response<ScaStatusResponse> authResponse = ais.updateConsentsPsuData(
                context.getConsentId(),
                context.getAuthorizationId(),
                context.toHeaders(),
                authentication(context)
        );

        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aContext ctx) -> ctx.setScaStatus(authResponse.getBody().getScaStatus().getValue())
        );
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
        runtimeService.trigger(execution.getId());
    }

    private TransactionAuthorisation authentication(Xs2aContext context) {
        TransactionAuthorisation authorisation = new TransactionAuthorisation();
        authorisation.setScaAuthenticationData(context.getLastScaChallenge());
        return authorisation;
    }
}
