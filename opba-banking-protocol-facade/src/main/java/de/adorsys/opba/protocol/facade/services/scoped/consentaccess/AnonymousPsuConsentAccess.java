package de.adorsys.opba.protocol.facade.services.scoped.consentaccess;

import com.google.common.collect.Iterables;
import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechPubKey;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechOnlyPubKeyRepository;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.scoped.consent.ConsentAccess;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.facade.config.encryption.ConsentAuthorizationEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.config.encryption.PsuEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.services.EncryptionKeySerde;
import de.adorsys.opba.protocol.facade.services.scoped.ConsentAccessUtil;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Consent access service (authorization process and consent itself) for Anonymous PSUs' (one that does not require to
 * login into OBG)
 */
@SuppressWarnings("CPD-START") // Acceptable duplication, as entities are quite different in their essence
@RequiredArgsConstructor
public class AnonymousPsuConsentAccess implements ConsentAccess {

    private final Bank aspsp;
    private final Fintech fintech;
    private final FintechOnlyPubKeyRepository pubKeys;
    private final PsuEncryptionServiceProvider psuEncryption;
    private final ServiceSession serviceSession;
    private final ConsentRepository consentRepository;
    private final ConsentAuthorizationEncryptionServiceProvider encServiceProvider;
    private final EncryptionKeySerde encryptionKeySerde;

    @Override
    public boolean isFinTechScope() {
        return false;
    }

    @Override
    public ProtocolFacingConsent createDoNotPersist() {
        Collection<FintechPubKey> keys = pubKeys.findByFintech(fintech);
        // Expecting key amount to be small
        FintechPubKey fintechPubKey = Iterables.get(keys, ThreadLocalRandom.current().nextInt(0, keys.size() - 1));

        Consent newConsent = Consent.builder()
                .serviceSession(serviceSession)
                .aspsp(aspsp)
                .fintechPubKey(fintechPubKey)
                .build();

        return new ProtocolFacingConsentImpl(newConsent, anonymousEncryptionServiceBasedOnKeyFromFintech(fintechPubKey),
                                             encServiceProvider, encryptionKeySerde);
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
                .map(it ->
                        new ProtocolFacingConsentImpl(
                                it,
                                anonymousEncryptionServiceBasedOnKeyFromFintech(it.getFintechPubKey()),
                                encServiceProvider,
                                encryptionKeySerde
                        )
                )
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ProtocolFacingConsent> getAvailableConsentsForCurrentPsu() {
        return Collections.emptyList();
    }

    private EncryptionService anonymousEncryptionServiceBasedOnKeyFromFintech(FintechPubKey fintechPubKey) {
        return psuEncryption.forPublicKey(fintechPubKey.getId(), fintechPubKey.getKey());
    }
}
