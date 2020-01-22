package de.adorsys.opba.core.protocol.service.xs2a.consent.authenticate.embedded;

import de.adorsys.opba.core.protocol.service.ContextUtil;
import de.adorsys.opba.core.protocol.service.ValidatedExecution;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.core.protocol.service.xs2a.dto.Xs2aStandardHeaders;
import de.adorsys.opba.core.protocol.service.xs2a.dto.consent.authenticate.embedded.ProvideScaChallengeResult;
import de.adorsys.opba.core.protocol.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.ScaStatusResponse;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("xs2aReportScaChallenge")
@RequiredArgsConstructor
public class Xs2aReportScaChallenge extends ValidatedExecution<Xs2aContext> {

    private final ProvideScaChallengeResult.FromCtx toValidatableBody;
    private final ProvideScaChallengeResult.ToXs2aApi toBody;
    private final Xs2aStandardHeaders.FromCtx toHeaders;
    private final Xs2aValidator validator;
    private final RuntimeService runtimeService;
    private final AccountInformationService ais;

    @Override
    protected void doValidate(DelegateExecution execution, Xs2aContext context) {
        validator.validate(execution, toHeaders.map(context), toValidatableBody.map(context));
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        Response<ScaStatusResponse> authResponse = ais.updateConsentsPsuData(
                context.getConsentId(),
                context.getAuthorizationId(),
                toHeaders.map(context).toHeaders(),
                toBody.map(toValidatableBody.map(context))
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
}
