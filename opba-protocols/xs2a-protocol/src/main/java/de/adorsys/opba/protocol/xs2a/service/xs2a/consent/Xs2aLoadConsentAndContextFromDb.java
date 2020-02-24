package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import de.adorsys.opba.protocol.xs2a.config.flowable.Xs2aObjectMapper;
import de.adorsys.opba.protocol.xs2a.service.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;

@Service("xs2aLoadConsentAndContextFromDb")
@RequiredArgsConstructor
public class Xs2aLoadConsentAndContextFromDb extends ValidatedExecution<Xs2aContext> {

    private final Xs2aObjectMapper mapper;
    private final ConsentRepository consentRepository;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        loadContext(execution, context);
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
        loadContext(execution, context);
    }

    @SneakyThrows
    private void loadContext(DelegateExecution execution, Xs2aContext context) {
        Optional<Consent> consent = consentRepository.findByServiceSessionId(context.getServiceSessionId());

        if (!consent.isPresent()) {
            return;
        }

        execution.setVariable(CONTEXT, mapper.getMapper().readValue(consent.get().getContext(), Xs2aContext.class));
    }
}
