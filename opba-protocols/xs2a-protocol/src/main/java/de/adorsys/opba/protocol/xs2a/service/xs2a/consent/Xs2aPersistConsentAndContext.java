package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableObjectMapper;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.util.logresolver.Xs2aLogResolver;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * Persists the context and the associated context with it to the database. The context is necessary for future reuse -
 * to validate the consent type, if it can be applied to current operation, etc.
 */
@Service("xs2aPersistConsentAndContext")
@RequiredArgsConstructor
public class Xs2aPersistConsentAndContext extends ValidatedExecution<Xs2aContext> {

    private final FlowableObjectMapper mapper;
    private final Xs2aLogResolver logResolver = new Xs2aLogResolver(getClass());

    @Override
    @SneakyThrows
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);

        ProtocolFacingConsent consent = context.consentAccess().findSingleByCurrentServiceSession()
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
