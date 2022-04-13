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
import de.adorsys.opba.protocol.facade.config.encryption.ConsentAuthorizationEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.config.encryption.PsuEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import de.adorsys.opba.protocol.facade.services.EncryptionKeySerde;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.function.Supplier;

/**
 * Factory responsible for creating {@link ConsentAccess} new objects' templates.
 */
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
    private final ConsentAuthorizationEncryptionServiceProvider encServiceProvider;
    private final EncryptionKeySerde encryptionKeySerde;

    /**
     * Consent access for PSU-ASPSP tuple.
     * @param psu Fintech user/PSU to grant consent for
     * @param aspsp ASPSP(bank) that grants consent
     * @param session Service session for this consent
     * @return New consent access template
     */
    public ConsentAccess consentForPsuAndAspsp(Psu psu, Bank aspsp, ServiceSession session) {
        PsuAspspPrvKey prvKey = prvKeyRepository.findByPsuIdAndAspspId(psu.getId(), aspsp.getId())
                .orElseThrow(() -> new IllegalStateException("No public key for: " + psu.getId()));
        return new PsuConsentAccess(psu, aspsp, psuEncryption.forPublicKey(prvKey.getId(), prvKey.getPubKey().getKey()), session, consentRepository);
    }

    /**
     * Consent access for Anonymous PSU (does not require login to OBG)-ASPSP tuple.
     * @param aspsp ASPSP(bank) that grants consent
     * @param session Service session for this consent
     * @return New consent access template
     */
    public ConsentAccess consentForAnonymousPsu(Fintech fintech, Bank aspsp, ServiceSession session) {
        return new AnonymousPsuConsentAccess(aspsp, fintech, fintechPubKeys, psuEncryption, session, consentRepository,
                                             encServiceProvider, encryptionKeySerde);
    }

    /**
     * Consent for Fintech (executed on i.e. ListAccounts).
     * @param fintech FinTech that wants to access consents'
     * @param aspsp ASPSP(bank) that grants consent
     * @param session Service session for this consent
     * @param fintechPassword FinTech Keystore protection password
     * @return New consent access template
     */
    public ConsentAccess consentForFintech(Fintech fintech, Bank aspsp, ServiceSession session, Supplier<char[]> fintechPassword) {
        var anonymousAccess = new AnonymousPsuConsentAccess(aspsp, fintech, fintechPubKeys, psuEncryption, session,
                                                            consentRepository, encServiceProvider, encryptionKeySerde);
        return new FintechConsentAccessImpl(
                fintech, psuEncryption, fintechPsuAspspPrvKeyRepository, fintechVault, consentRepository, entityManager,
                session.getId(), fintechPassword, anonymousAccess, encServiceProvider, encryptionKeySerde
        );
    }
}
