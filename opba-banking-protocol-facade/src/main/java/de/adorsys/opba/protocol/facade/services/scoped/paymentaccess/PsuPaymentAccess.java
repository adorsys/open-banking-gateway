package de.adorsys.opba.protocol.facade.services.scoped.paymentaccess;

import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.scoped.consent.ConsentAccess;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.facade.services.scoped.consentaccess.ProtocolFacingConsentImpl;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class PsuPaymentAccess implements ConsentAccess {

    private final Bank aspsp;
    private final EncryptionService encryptionService;
    private final ServiceSession serviceSession;
    private final ConsentRepository consentRepository;

    @Override
    public boolean isFinTechScope() {
        return false;
    }

    @Override
    public ProtocolFacingConsent createDoNotPersist() {
        Consent newConsent = Consent.builder()
                .serviceSession(serviceSession)
                .aspsp(aspsp)
                .build();

        return new ProtocolFacingConsentImpl(newConsent, encryptionService);
    }

    @Override
    public void save(ProtocolFacingConsent consent) {
        consentRepository.save(((ProtocolFacingConsentImpl) consent).getConsent());
    }

    @Override
    public void delete(ProtocolFacingConsent consent) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public Optional<ProtocolFacingConsent> findSingleByCurrentServiceSession() {
        return Optional.empty();
    }

    @Override
    public List<ProtocolFacingConsent> findByCurrentServiceSessionOrderByModifiedDesc() {
        return Collections.emptyList();
    }

    @Override
    public Collection<ProtocolFacingConsent> getAvailableConsentsForCurrentPsu() {
        return Collections.emptyList();
    }
}
