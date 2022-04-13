package de.adorsys.opba.protocol.facade.services.scoped.consentaccess;

import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechPsuAspspPrvKey;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechPsuAspspPrvKeyRepository;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.scoped.consent.FintechConsentAccess;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.facade.config.encryption.ConsentAuthorizationEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.config.encryption.PsuEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import de.adorsys.opba.protocol.facade.services.EncryptionKeySerde;
import de.adorsys.opba.protocol.facade.services.scoped.ConsentAccessUtil;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Consent access service (shared consent itself for i.e. ListAccounts) for FinTech.
 */
@RequiredArgsConstructor
public class FintechConsentAccessImpl implements FintechConsentAccess {

    private final Fintech fintech;
    private final PsuEncryptionServiceProvider encryptionService;
    private final FintechPsuAspspPrvKeyRepository keys;
    private final FintechSecureStorage fintechVault;
    private final ConsentRepository consents;
    private final EntityManager entityManager;
    private final UUID serviceSessionId;
    private final Supplier<char[]> fintechPassword;
    private final AnonymousPsuConsentAccess anonymousPsuConsentAccess;
    private final ConsentAuthorizationEncryptionServiceProvider encServiceProvider;
    private final EncryptionKeySerde encryptionKeySerde;

    /**
     * Creates consent template, but does not persist it
     */
    public ProtocolFacingConsent createAnonymousConsentNotPersist() {
        return anonymousPsuConsentAccess.createDoNotPersist();
    }

    /**
     * Saves consent to the database.
     */
    @Override
    public void save(ProtocolFacingConsent consent) {
        consents.save(((ProtocolFacingConsentImpl) consent).getConsent());
    }

    /**
     * Deletes consent from the database.
     */
    @Override
    public void delete(ProtocolFacingConsent consent) {
        consents.delete(((ProtocolFacingConsentImpl) consent).getConsent());
    }

    /**
     * Finds the most recent granted consent for the current service session.
     */
    @Override
    public Optional<ProtocolFacingConsent> findSingleByCurrentServiceSession() {
        return ConsentAccessUtil.getProtocolFacingConsent(findByCurrentServiceSessionOrderByModifiedDesc());
    }

    /**
     * Lists all consents that are associated with current service session.
     */
    @Override
    public List<ProtocolFacingConsent> findByCurrentServiceSessionOrderByModifiedDesc() {
        ServiceSession serviceSession = entityManager.find(ServiceSession.class, serviceSessionId);
        if (null == serviceSession) {
            return Collections.emptyList();
        }

        List<Consent> consent = consents.findByServiceSessionIdOrderByModifiedAtDesc(serviceSession.getId());
        // Anonymous consent session:
        if (null == serviceSession.getAuthSession() || null == serviceSession.getAuthSession().getPsu()) {
            return anonymousConsent(consent);
        }

        Optional<FintechPsuAspspPrvKey> psuAspspPrivateKey = keys.findByFintechIdAndPsuIdAndAspspId(
                fintech.getId(),
                serviceSession.getAuthSession().getPsu().getId(),
                serviceSession.getAuthSession().getAction().getBankProfile().getBank().getId()
        );
        if (!psuAspspPrivateKey.isPresent() || consent.isEmpty()) {
            return Collections.emptyList();
        }

        var psuAspspKey = fintechVault.psuAspspKeyFromPrivate(serviceSession, fintech, fintechPassword);
        EncryptionService enc = encryptionService.forPublicAndPrivateKey(psuAspspPrivateKey.get().getId(), psuAspspKey);
        return consent.stream().map(it -> new ProtocolFacingConsentImpl(it, enc, encServiceProvider, encryptionKeySerde))
            .collect(Collectors.toList());
    }

    /**
     * Returns available consents to the PSU (not FinTech).
     */
    @Override
    public Collection<ProtocolFacingConsent> getAvailableConsentsForCurrentPsu() {
        return Collections.emptyList();
    }

    @NotNull
    private List<ProtocolFacingConsent> anonymousConsent(List<Consent> consent) {
        return consent.stream()
                .map(it -> new ProtocolFacingConsentImpl(
                        it,
                        encryptionService.forPublicAndPrivateKey(
                                it.getFintechPubKey().getId(),
                                fintechVault.fintechOnlyPrvKeyFromPrivate(
                                        it.getFintechPubKey().getPrvKey(),
                                        fintech,
                                        fintechPassword
                                )
                        ),
                        encServiceProvider,
                        encryptionKeySerde)
                ).collect(Collectors.toList());
    }
}
