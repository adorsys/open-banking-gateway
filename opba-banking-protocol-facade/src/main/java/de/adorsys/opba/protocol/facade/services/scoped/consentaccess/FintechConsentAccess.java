package de.adorsys.opba.protocol.facade.services.scoped.consentaccess;

import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechPsuAspspPrvKey;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechPsuAspspPrvKeyRepository;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.scoped.consent.ConsentAccess;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.facade.config.encryption.PsuEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import de.adorsys.opba.protocol.facade.services.scoped.ConsentAccessUtil;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.security.PrivateKey;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class FintechConsentAccess implements ConsentAccess {

    private final Fintech fintech;
    private final PsuEncryptionServiceProvider encryptionService;
    private final FintechPsuAspspPrvKeyRepository keys;
    private final FintechSecureStorage fintechVault;
    private final ConsentRepository consents;
    private final EntityManager entityManager;
    private final UUID serviceSessionId;
    private final Supplier<char[]> fintechPassword;


    @Override
    public boolean isFinTechScope() {
        return true;
    }

    @Override
    public ProtocolFacingConsent createDoNotPersist() {
        throw new IllegalStateException("No PSU present - can't create consent");
    }

    @Override
    public void save(ProtocolFacingConsent consent) {
        throw new IllegalStateException("No PSU present - can't save consent");
    }

    @Override
    public void delete(ProtocolFacingConsent consent) {
        throw new IllegalStateException("No PSU present - can't delete consent");
    }

    @Override
    public Optional<ProtocolFacingConsent> findSingleByCurrentServiceSession() {
        return ConsentAccessUtil.getProtocolFacingConsent(findByCurrentServiceSessionOrderByModifiedDesc());
    }

    @Override
    public List<ProtocolFacingConsent> findByCurrentServiceSessionOrderByModifiedDesc() {
        ServiceSession serviceSession = entityManager.find(ServiceSession.class, serviceSessionId);
        if (null == serviceSession || null == serviceSession.getAuthSession()) {
            return Collections.emptyList();
        }

        Optional<FintechPsuAspspPrvKey> psuAspspPrivateKey = keys.findByFintechIdAndPsuIdAndAspspId(
                fintech.getId(),
                serviceSession.getAuthSession().getPsu().getId(),
                serviceSession.getAuthSession().getAction().getBankProfile().getBank().getId()
        );
        List<Consent> consent = consents.findByServiceSessionIdOrderByModifiedAtDesc(serviceSession.getId());
        if (!psuAspspPrivateKey.isPresent() || consent.isEmpty()) {
            return Collections.emptyList();
        }

        PrivateKey psuAspspKey = fintechVault.psuAspspKeyFromPrivate(serviceSession, fintech, fintechPassword);
        EncryptionService enc = encryptionService.forPrivateKey(psuAspspPrivateKey.get().getId(), psuAspspKey);
        return consent.stream().map(it -> new ProtocolFacingConsentImpl(it, enc)).collect(Collectors.toList());
    }

    @Override
    public Collection<ProtocolFacingConsent> getAvailableConsentsForCurrentPsu() {
        return Collections.emptyList();
    }
}
