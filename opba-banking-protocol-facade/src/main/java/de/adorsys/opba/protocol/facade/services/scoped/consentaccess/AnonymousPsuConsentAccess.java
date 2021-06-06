package de.adorsys.opba.protocol.facade.services.scoped.consentaccess;

import com.google.common.collect.Iterables;
import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.db.domain.entity.Payment;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechPubKey;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import de.adorsys.opba.db.repository.jpa.PaymentRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechOnlyPubKeyRepository;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.scoped.consent.ConsentAccess;
import de.adorsys.opba.protocol.api.services.scoped.consent.PaymentAccess;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingPayment;
import de.adorsys.opba.protocol.facade.config.encryption.PsuEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.services.scoped.ConsentAccessUtil;
import de.adorsys.opba.protocol.facade.services.scoped.paymentaccess.ProtocolFacingPaymentImpl;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AnonymousPsuConsentAccess implements ConsentAccess {

    private final Bank aspsp;
    private final Fintech fintech;
    private final FintechOnlyPubKeyRepository pubKeys;
    private final PsuEncryptionServiceProvider psuEncryption;
    private final ServiceSession serviceSession;
    private final ConsentRepository consentRepository;

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
                .build();

        return new ProtocolFacingConsentImpl(newConsent, anonymousEncryptionServiceBasedOnRandomKeyFromFintech(fintechPubKey));
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
                .map(it -> new ProtocolFacingConsentImpl(it, encryptionService))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ProtocolFacingConsent> getAvailableConsentsForCurrentPsu() {
        return Collections.emptyList();
    }

    private EncryptionService anonymousEncryptionServiceBasedOnRandomKeyFromFintech(FintechPubKey fintechPubKey) {
        return psuEncryption.forPublicKey(fintechPubKey.getId(), fintechPubKey.getKey());
    }
}
