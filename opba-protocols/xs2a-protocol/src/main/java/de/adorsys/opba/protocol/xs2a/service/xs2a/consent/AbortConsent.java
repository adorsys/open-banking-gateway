package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.ConsentInitiateHeaders;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service("abortConsent")
@RequiredArgsConstructor
public class AbortConsent {

    private final AccountInformationService ais;
    private final ConsentRepository consents;
    private final DtoMapper<Xs2aAisContext, ConsentInitiateHeaders> toHeaders;

    @Transactional
    public void abortConsent(Xs2aAisContext ctx) {
        Optional<Consent> consent = consents.findByServiceSessionId(ctx.getServiceSessionId());

        if (!consent.isPresent()) {
            return;
        }

        ais.deleteConsent(ctx.getConsentId(), toHeaders.map(ctx).toHeaders());
        consents.delete(consent.get());
    }
}
