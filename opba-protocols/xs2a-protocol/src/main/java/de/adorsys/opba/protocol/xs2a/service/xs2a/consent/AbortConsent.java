package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.ais.Xs2aAisContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service("abortConsent")
@RequiredArgsConstructor
public class AbortConsent {

    private final AspspConsentDrop dropper;
    private final ConsentRepository consents;

    @Transactional
    public void abortConsent(Xs2aAisContext ctx) {
        Optional<Consent> consent = consents.findByServiceSessionId(ctx.getServiceSessionId());

        if (!consent.isPresent()) {
            return;
        }

        dropper.dropConsent(ctx);
        consents.delete(consent.get());
    }
}
