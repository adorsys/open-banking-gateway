package de.adorsys.opba.protocol.hbci.service.protocol.pis;

import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingPayment;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.PaymentHbciContext;
import de.adorsys.opba.protocol.hbci.service.SafeCacheSerDeUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("hbciPersistPaymentToDb")
@RequiredArgsConstructor
public class HbciPersistPaymentToDb extends ValidatedExecution<PaymentHbciContext> {

    private final SafeCacheSerDeUtil safeCacheSerDe;

    @Override
    @SneakyThrows
    protected void doRealExecution(DelegateExecution execution, PaymentHbciContext context) {
        ProtocolFacingPayment payment = context.paymentAccess().createDoNotPersist();

        payment.setPaymentId(context.getResponse().getTransactionId());
        payment.setPaymentContext(safeCacheSerDe.safeSerialize(context));
        context.paymentAccess().save(payment);
    }
}
