package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.decoupled;

import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.xs2a.adapter.service.PaymentInitiationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.PaymentInitiationScaStatusResponse;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("xs2aPisCheckDecoupledAuthorizationStatus")
@RequiredArgsConstructor
public class Xs2aPisCheckDecoupledAuthorisationStatus extends ValidatedExecution<Xs2aPisContext> {

    private final PaymentInitiationService pis;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aPisContext context) {
        Response<PaymentInitiationScaStatusResponse> paymentInitiationScaStatus = pis.getPaymentInitiationScaStatus(context.getPaymentType().getValue(),
                                                                                                                    context.getPaymentProduct(),
                                                                                                                    context.getPaymentId(),
                                                                                                                    context.getAuthorizationId(),
                                                                                                                    null,
                                                                                                                    null);

        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aContext ctx) -> {
                    ctx.setDecoupledScaFinished(paymentInitiationScaStatus.getBody().getScaStatus().isFinalisedStatus());
                }
        );
    }
}
