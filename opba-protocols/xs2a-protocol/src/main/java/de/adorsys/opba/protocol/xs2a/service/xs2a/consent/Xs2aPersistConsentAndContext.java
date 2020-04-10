package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
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

    private final ServiceSessionRepository sessionRepository;
    private final Xs2aObjectMapper mapper;
    private final ConsentRepository consents;

    @Override
    @SneakyThrows
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        Consent consent = consents.findByServiceSessionId(context.getServiceSessionId())
                .orElseGet(() -> Consent.builder()
                        .serviceSession(sessionRepository.findById(context.getServiceSessionId()).get())
                        .build()
                );

        consent.setConsent(context.getEncryption(), context.getConsentId());
        context.setConsentId(null); // avoid storing it in context, the change is transient here
        consent.setContext(
                context.getEncryption(),
                mapper.getMapper().writeValueAsString(
                        ImmutableMap.of(context.getClass().getCanonicalName(), context)
                )
        );
        consents.save(consent);
    }
}
