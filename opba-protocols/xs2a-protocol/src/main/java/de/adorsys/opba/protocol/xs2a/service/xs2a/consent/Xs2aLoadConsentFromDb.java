package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import de.adorsys.opba.protocol.xs2a.service.ContextUtil;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;

@Service("xs2aLoadConsentFromDb")
@RequiredArgsConstructor
public class Xs2aLoadConsentFromDb implements JavaDelegate {

    private final ConsentRepository consentRepository;

    @Override
    public void execute(DelegateExecution execution) {
        Xs2aContext context = ContextUtil.getContext(execution, Xs2aContext.class);
        Optional<Consent> consent = consentRepository.findByServiceSessionId(context.getServiceSessionId());

        if (!consent.isPresent()) {
            return;
        }

        context.setConsentId(consent.get().getConsentCode());
        execution.setVariable(CONTEXT, context);
    }
}
