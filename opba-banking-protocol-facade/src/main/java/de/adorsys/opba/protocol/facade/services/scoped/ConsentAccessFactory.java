package de.adorsys.opba.protocol.facade.services.scoped;

import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechPsuAspspPrvKey;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechConsentRepository;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.scoped.consent.ConsentAccess;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.facade.config.encryption.PsuConsentEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.config.encryption.SecretKeyWithIv;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsentAccessFactory {

    private final FintechSecureStorage fintechVault;
    private final PsuConsentEncryptionServiceProvider encryptionServiceProvider;
    private final FintechConsentRepository fintechConsentRepository;
    private final ConsentRepository consentRepository;

    public ConsentAccess forPsuAndAspsp(Psu psu, Bank aspsp, ServiceSession session, EncryptionService encryptionService) {
        return new PsuConsentAccess(psu, aspsp, encryptionService, session, consentRepository);
    }

    public ConsentAccess forFintech(Fintech fintech, ServiceSession session, Supplier<char[]> fintechPassword) {
        return new FintechConsentAccess(
                fintech, encryptionServiceProvider, fintechConsentRepository, fintechVault, consentRepository, session, fintechPassword
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
        public Optional<ProtocolFacingConsent> findByCurrentServiceSession() {
            return consentRepository.findByServiceSessionId(serviceSession.getId())
                    .map(it -> new ProtocolFacingConsentImpl(it, encryptionService));
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
        private final FintechConsentRepository consents;
        private final FintechSecureStorage fintechVault;
        private final ConsentRepository consentRepository;
        private final ServiceSession serviceSession;
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
        public Optional<ProtocolFacingConsent> findByCurrentServiceSession() {
            Optional<Consent> consent = consentRepository.findByServiceSessionId(serviceSession.getId());
            if (!consent.isPresent()) {
                return Optional.empty();
            }

            Optional<FintechPsuAspspPrvKey> fintechConsent = Optional.empty();//consents.findByFintechAndConsent(fintech, consent.get());

            if (!fintechConsent.isPresent()) {
                return Optional.empty();
            }

            SecretKeyWithIv psuAspspKey = fintechVault
                    .psuAspspKeyFromPrivate(fintechConsent.get().getFintech(), consent.get(), fintechPassword);

            return Optional.of(new ProtocolFacingConsentImpl(consent.get(), encryptionService.forSecretKey(psuAspspKey)));
        }

        @Override
        public Collection<ProtocolFacingConsent> getAvailableConsentsForCurrentPsu() {
            return Collections.emptyList();
        }
    }

    @Getter
    @RequiredArgsConstructor
    private static class ProtocolFacingConsentImpl implements ProtocolFacingConsent {

        private final Consent consent;
        private final EncryptionService encryptionService;

        @Override
        public String getConsentId() {
            return consent.getConsent(encryptionService);
        }

        @Override
        public String getConsentContext() {
            return consent.getContext(encryptionService);
        }

        @Override
        public void setConsentId(String id) {
            consent.setConsent(encryptionService, id);
        }

        @Override
        public void setConsentContext(String context) {
            consent.setContext(encryptionService, context);
        }
    }
}
