package de.adorsys.opba.protocol.facade.services.scoped.consentaccess;

import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.domain.entity.psu.PsuAspspPrvKey;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechOnlyPubKeyRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechPsuAspspPrvKeyRepository;
import de.adorsys.opba.db.repository.jpa.psu.PsuAspspPrvKeyRepository;
import de.adorsys.opba.protocol.api.services.scoped.consent.ConsentAccess;
import de.adorsys.opba.protocol.facade.config.encryption.PsuEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class ConsentAccessFactory {

    private final EntityManager entityManager;
    private final PsuAspspPrvKeyRepository prvKeyRepository;
    private final FintechSecureStorage fintechVault;
    private final PsuEncryptionServiceProvider psuEncryption;
    private final FintechOnlyPubKeyRepository fintechPubKeys;
    private final FintechPsuAspspPrvKeyRepository fintechPsuAspspPrvKeyRepository;
    private final ConsentRepository consentRepository;

    public ConsentAccess consentForPsuAndAspsp(Psu psu, Bank aspsp, ServiceSession session) {
        PsuAspspPrvKey prvKey = prvKeyRepository.findByPsuIdAndAspspId(psu.getId(), aspsp.getId())
                .orElseThrow(() -> new IllegalStateException("No public key for: " + psu.getId()));
        return new PsuConsentAccess(psu, aspsp, psuEncryption.forPublicKey(prvKey.getId(), prvKey.getPubKey().getKey()), session, consentRepository);
    }

    public ConsentAccess consentForAnonymousPsu(Fintech fintech, Bank aspsp, ServiceSession session) {
        return new AnonymousPsuConsentAccess(aspsp, fintech, fintechPubKeys, psuEncryption, session, consentRepository);
    }

    public ConsentAccess consentForFintech(Fintech fintech, ServiceSession session, Supplier<char[]> fintechPassword) {
        return new FintechConsentAccess(
                fintech, psuEncryption, fintechPsuAspspPrvKeyRepository, fintechVault, consentRepository, entityManager, session.getId(), fintechPassword
        );
    }
}
