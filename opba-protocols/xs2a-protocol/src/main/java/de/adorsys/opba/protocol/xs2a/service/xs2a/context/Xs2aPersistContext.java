package de.adorsys.opba.protocol.xs2a.service.xs2a.context;

import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import de.adorsys.opba.protocol.xs2a.config.flowable.Xs2aObjectMapper;
import de.adorsys.opba.protocol.xs2a.service.ValidatedExecution;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("xs2aPersistContext")
@RequiredArgsConstructor
public class Xs2aPersistContext extends ValidatedExecution<Xs2aContext> {

    private final Xs2aObjectMapper mapper;
    private final ConsentRepository consents;

    @Override
    @SneakyThrows
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        Consent consent = consents.findByServiceSessionId(context.getServiceSessionId())
                .orElseThrow(() -> new IllegalStateException("No consent for session"));

        consent.setContext(mapper.getMapper().writeValueAsString(context));
        consents.save(consent);
    }
}
