package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Declines already existing consent (maybe yet unauthorized fully) and deletes it from DB.
 */
@Slf4j
@Service("abortConsent")
@RequiredArgsConstructor
public class AbortConsent {

    private final AspspConsentDrop dropper;

    @Transactional
    public void abortConsent(Xs2aContext ctx) {
        if (ctx instanceof Xs2aAisContext) {
            dropConsent((Xs2aAisContext) ctx);
            return;
        } else if (ctx instanceof Xs2aPisContext) {
            handleDropPayments((Xs2aPisContext) ctx);
            return;
        }

        throw new IllegalStateException("Unknown context type: " + ctx);
    }

    private void dropConsent(Xs2aAisContext ctx) {
        Optional<ProtocolFacingConsent> consent = ctx.consentAccess().findSingleByCurrentServiceSession();

        if (!consent.isPresent()) {
            return;
        }

        dropper.dropConsent(ctx);
        ctx.consentAccess().delete(consent.get());
    }

    private void handleDropPayments(Xs2aPisContext ctx) {
        if (!ctx.isAuthorized()) {
            return;
        }

        throw new IllegalStateException("Unable to drop payment after it was authorized");
    }
}
