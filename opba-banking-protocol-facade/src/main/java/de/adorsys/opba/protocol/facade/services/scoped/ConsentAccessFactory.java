package de.adorsys.opba.protocol.facade.services.scoped;

import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechPsuAspspPrvKey;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.domain.entity.psu.PsuAspspPrvKey;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechPsuAspspPrvKeyRepository;
import de.adorsys.opba.db.repository.jpa.psu.PsuAspspPrvKeyRepository;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.scoped.consent.ConsentAccess;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.facade.config.encryption.PsuConsentEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.security.PrivateKey;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsentAccessFactory {

    private final EntityManager entityManager;
    private final PsuAspspPrvKeyRepository prvKeyRepository;
    private final FintechSecureStorage fintechVault;
    private final PsuConsentEncryptionServiceProvider psuEncryption;
    private final FintechPsuAspspPrvKeyRepository fintechPsuAspspPrvKeyRepository;
    private final ConsentRepository consentRepository;

    public ConsentAccess forPsuAndAspsp(Psu psu, Bank aspsp, ServiceSession session) {
        PsuAspspPrvKey prvKey = prvKeyRepository.findByPsuIdAndAspspId(psu.getId(), aspsp.getId())
                .orElseThrow(() -> new IllegalStateException("No public key for: " + psu.getId()));
        return new PsuConsentAccess(psu, aspsp, psuEncryption.forPublicKey(prvKey.getId(), prvKey.getPubKey().getKey()), session, consentRepository);
    }

    public ConsentAccess forFintech(Fintech fintech, ServiceSession session, Supplier<char[]> fintechPassword) {
        return new FintechConsentAccess(
                fintech, psuEncryption, fintechPsuAspspPrvKeyRepository, fintechVault, consentRepository, entityManager, session.getId(), fintechPassword
        );
    }

    @RequiredArgsConstructor
    private static class PsuConsentAccess implements ConsentAccess {

        private final Psu psu;
        private final Bank aspsp;
        private final EncryptionService encryptionService;
        private final ServiceSession serviceSession;
        private final ConsentRepository consentRepository;

        @Override
        public ProtocolFacingConsent createDoNotPersist() {
            Consent newConsent = Consent.builder()
                    .serviceSession(serviceSession)
                    .psu(psu)
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
            consentRepository.delete(((ProtocolFacingConsentImpl) consent).getConsent());
        }

        @Override
        public Optional<ProtocolFacingConsent> findSingleByCurrentServiceSession() {
            return getProtocolFacingConsent(findByCurrentServiceSession());
        }

        @Override
        public Collection<ProtocolFacingConsent> findByCurrentServiceSession() {
            return consentRepository.findByServiceSessionId(serviceSession.getId())
                    .stream()
                    .map(it -> new ProtocolFacingConsentImpl(it, encryptionService))
                    .collect(Collectors.toList());
        }

        @Override
        public Collection<ProtocolFacingConsent> getAvailableConsentsForCurrentPsu() {
            return consentRepository.findByPsu(psu).stream()
                    .map(it -> new ProtocolFacingConsentImpl(it, encryptionService))
                    .collect(Collectors.toList());
        }
    }

    @RequiredArgsConstructor
    private static class FintechConsentAccess implements ConsentAccess {

        private final Fintech fintech;
        private final PsuConsentEncryptionServiceProvider encryptionService;
        private final FintechPsuAspspPrvKeyRepository keys;
        private final FintechSecureStorage fintechVault;
        private final ConsentRepository consents;
        private final EntityManager entityManager;
        private final UUID serviceSessionId;
        private final Supplier<char[]> fintechPassword;


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
            return getProtocolFacingConsent(findByCurrentServiceSession());
        }

        @Override
        public Collection<ProtocolFacingConsent> findByCurrentServiceSession() {
            ServiceSession serviceSession = entityManager.find(ServiceSession.class, serviceSessionId);
            if (null == serviceSession || null == serviceSession.getAuthSession()) {
                return Collections.emptyList();
            }

            Optional<FintechPsuAspspPrvKey> psuAspspPrivateKey = keys.findByFintechIdAndPsuIdAndAspspId(
                    fintech.getId(),
                    serviceSession.getAuthSession().getPsu().getId(),
                    serviceSession.getAuthSession().getAction().getBankProfile().getBank().getId()
            );
            Collection<Consent> consent = consents.findByServiceSessionId(serviceSession.getId());
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

    @NotNull
    private static Optional<ProtocolFacingConsent> getProtocolFacingConsent(Collection<ProtocolFacingConsent> consents) {
        if (consents.isEmpty()) {
            return Optional.empty();
        }

        if (consents.size() > 1) {
            throw new IllegalStateException("Too many consents");
        }

        return Optional.of(consents.iterator().next());
    }

    @Getter
    @RequiredArgsConstructor
    private static class ProtocolFacingConsentImpl implements ProtocolFacingConsent {

        private final Consent consent;
        private final EncryptionService encryptionService;

        @Override
        public String getConsentId() {
            return consent.getConsentId(encryptionService);
        }

        @Override
        public String getConsentContext() {
            return consent.getContext(encryptionService);
        }

        @Override
        public void setConsentId(String id) {
            consent.setConsentId(encryptionService, id);
        }

        @Override
        public void setConsentContext(String context) {
            consent.setContext(encryptionService, context);
        }
    }
}
