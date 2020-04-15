package de.adorsys.opba.protocol.facade.services.scoped;

import de.adorsys.opba.protocol.api.services.scoped.consent.ConsentAccess;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsentAccessProvider implements ConsentAccess {

    @Override
    public ProtocolFacingConsent createDoNotPersist(UUID internalId) {
        return null;
    }

    @Override
    public void save(ProtocolFacingConsent consent) {

    }

    @Override
    public void delete(ProtocolFacingConsent consent) {

    }

    @Override
    public Optional<ProtocolFacingConsent> findByInternalId(UUID internalId) {
        return Optional.empty();
    }

    @Override
    public List<ProtocolFacingConsent> getAvailableConsentsForCurrentPsu() {
        return null;
    }
}
