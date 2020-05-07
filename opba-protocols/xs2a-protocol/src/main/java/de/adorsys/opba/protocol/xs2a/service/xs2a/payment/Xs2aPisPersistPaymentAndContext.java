package de.adorsys.opba.protocol.xs2a.service.xs2a.payment;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.xs2a.config.flowable.Xs2aObjectMapper;
import de.adorsys.opba.protocol.xs2a.service.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.pis.Xs2aPisContext;
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

    private final Xs2aObjectMapper mapper;

    @Override
    @SneakyThrows
    protected void doRealExecution(DelegateExecution execution, Xs2aPisContext context) {
        ProtocolFacingConsent consent = context.consentAccess().findByCurrentServiceSession()
                .orElseGet(() -> context.consentAccess().createDoNotPersist());

        consent.setConsentId(context.getConsentId());
        consent.setConsentContext(
                mapper.getMapper().writeValueAsString(
                        ImmutableMap.of(context.getClass().getCanonicalName(), context)
                )
        );
        context.consentAccess().save(consent);
    }
}
