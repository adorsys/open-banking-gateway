package de.adorsys.opba.protocol.facade.services.scoped.consentaccess;

import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.scoped.consent.ConsentAccess;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.facade.services.scoped.ConsentAccessUtil;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Consent access service (authorization process and consent itself) for Authenticated PSUs' (one that logs in into
 * OBG)
 */
@RequiredArgsConstructor
public class PsuConsentAccess implements ConsentAccess {

    private final Psu psu;
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
                .psu(psu)
                .aspsp(aspsp)
                .build();

        return new ProtocolFacingConsentImpl(newConsent, encryptionService, null, null);
    }

    @Override
    public void save(ProtocolFacingConsent consent) {
        consentRepository.save(((ProtocolFacingConsentImpl) consent).getConsent());
    }

    @Override
    public void delete(ProtocolFacingConsent consent) {
        consentRepository.delete(((ProtocolFacingConsentImpl) consent).getConsent());
    }

    @Override
    public Optional<ProtocolFacingConsent> findSingleByCurrentServiceSession() {
        return ConsentAccessUtil.getProtocolFacingConsent(findByCurrentServiceSessionOrderByModifiedDesc());
    }

    @Override
    public List<ProtocolFacingConsent> findByCurrentServiceSessionOrderByModifiedDesc() {
        return consentRepository.findByServiceSessionIdOrderByModifiedAtDesc(serviceSession.getId())
                .stream()
                .map(it -> new ProtocolFacingConsentImpl(it, encryptionService, null, null))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ProtocolFacingConsent> getAvailableConsentsForCurrentPsu() {
        return consentRepository.findByPsu(psu).stream()
                .map(it -> new ProtocolFacingConsentImpl(it, encryptionService, null, null))
                .collect(Collectors.toList());
    }
}
