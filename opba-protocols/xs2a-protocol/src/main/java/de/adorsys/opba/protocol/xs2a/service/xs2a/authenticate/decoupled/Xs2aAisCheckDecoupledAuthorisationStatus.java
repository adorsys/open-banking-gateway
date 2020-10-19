package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.decoupled;

import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.ScaStatusResponse;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("xs2aAisCheckDecoupledAuthorizationStatus")
@RequiredArgsConstructor
public class Xs2aAisCheckDecoupledAuthorisationStatus extends ValidatedExecution<Xs2aContext> {

    private final AccountInformationService ais;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        Response<ScaStatusResponse> consentScaStatus = ais.getConsentScaStatus(context.getConsentId(),
                                                                               context.getAuthorizationId(),
                                                                               null,
                                                                               null);

        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aContext ctx) -> {
                    ctx.setDecoupledScaFinished(consentScaStatus.getBody().getScaStatus().isFinalisedStatus());
                }
        );
    }

}
