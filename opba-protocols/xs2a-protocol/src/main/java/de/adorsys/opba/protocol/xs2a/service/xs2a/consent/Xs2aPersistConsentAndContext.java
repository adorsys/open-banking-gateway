package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.xs2a.config.flowable.Xs2aObjectMapper;
import de.adorsys.opba.protocol.xs2a.service.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("xs2aPersistConsentAndContext")
@RequiredArgsConstructor
public class Xs2aPersistConsentAndContext extends ValidatedExecution<Xs2aContext> {

    private final Xs2aObjectMapper mapper;

    @Override
    @SneakyThrows
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        ProtocolFacingConsent consent = context.consentAccess().findByInternalId(context.getServiceSessionId())
                .orElseGet(() -> context.consentAccess().createDoNotPersist(context.getServiceSessionId()));

        consent.setConsentId(context.getConsentId());
        consent.setConsentContext(
                mapper.getMapper().writeValueAsString(
                        ImmutableMap.of(context.getClass().getCanonicalName(), context)
                )
        );
        context.consentAccess().save(consent);
    }
}
