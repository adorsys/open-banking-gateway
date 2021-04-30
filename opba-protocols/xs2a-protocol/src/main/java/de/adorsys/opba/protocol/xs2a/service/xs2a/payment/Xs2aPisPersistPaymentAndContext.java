package de.adorsys.opba.protocol.xs2a.service.xs2a.payment;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingPayment;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableObjectMapper;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.opba.protocol.xs2a.util.logresolver.Xs2aLogResolver;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * Persists the context and the associated context with it to the database. The context is necessary for future reuse -
 * to validate the payment type, if it can be applied to current operation, etc.
 */
@Service("xs2aPisPersistPaymentAndContext")
@RequiredArgsConstructor
public class Xs2aPisPersistPaymentAndContext extends ValidatedExecution<Xs2aPisContext> {

    private final FlowableObjectMapper mapper;
    private final Xs2aLogResolver logResolver = new Xs2aLogResolver(getClass());

    @Override
    @SneakyThrows
    protected void doRealExecution(DelegateExecution execution, Xs2aPisContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);

        ProtocolFacingPayment payment = context.paymentAccess().createDoNotPersist();
        payment.setPaymentId(context.getPaymentId());
        payment.setPaymentContext(
                mapper.getMapper().writeValueAsString(
                        ImmutableMap.of(context.getClass().getCanonicalName(), context)
                )
        );
        context.paymentAccess().save(payment);

        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aPisContext ctx) -> ctx.setAuthorized(true)
        );

    }
}
