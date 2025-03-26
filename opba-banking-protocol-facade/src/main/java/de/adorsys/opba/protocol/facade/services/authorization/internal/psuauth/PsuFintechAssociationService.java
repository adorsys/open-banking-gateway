package de.adorsys.opba.protocol.facade.services.authorization.internal.psuauth;

import de.adorsys.opba.db.domain.entity.psu.PsuAspspPrvKey;
import de.adorsys.opba.db.domain.entity.psu.PsuAspspPubKey;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.repository.jpa.psu.PsuAspspPubKeyRepository;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechConsentSpecSecureStorage;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import de.adorsys.opba.protocol.facade.config.encryption.impl.psu.PsuSecureStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.security.PublicKey;
import java.util.UUID;

/**
 * Associates the consent granted to PSU/Fintech user with the requesting Fintech (on consent confirmation by FinTech)
 */
@Service
@RequiredArgsConstructor
public class PsuFintechAssociationService {

    private final PsuAspspPubKeyRepository pubKeys;
    private final EntityManager entityManager;
    private final FintechSecureStorage fintechVault;
    private final PsuSecureStorage psuVault;
    private final FintechConsentSpecSecureStorage vault;

    /**
     * Share PSUs' ASPSP encryption key with the FinTech - sends the key to INBOX.
     * @param psuPassword PSU password
     * @param session Session where consent granting was executed
     */
    @Transactional
    public void sharePsuAspspSecretKeyWithFintech(String psuPassword, AuthSession session) {
        var psuAspspKey = psuVault.getOrCreateKeyFromPrivateForAspsp(
                psuPassword::toCharArray,
                session,
                this::storePublicKey
        );

        fintechVault.psuAspspKeyToInbox(session, psuAspspKey);
    }

    /**
     * Allows to read consent specification that was required by the FinTech
     * @param session Authorization session for the consent grant
     * @param fintechUserPassword PSU/Fintech users' password
     * @return Consent specification
     */
    @Transactional
    public FintechConsentSpecSecureStorage.FinTechUserInboxData readInboxFromFinTech(AuthSession session, String fintechUserPassword) {
        return vault.fromInboxForAuth(
                session,
                fintechUserPassword::toCharArray
        );
    }

    private void storePublicKey(UUID id, PublicKey publicKey) {
        PsuAspspPubKey psuAspspPubKey = PsuAspspPubKey.builder()
                .prvKey(entityManager.find(PsuAspspPrvKey.class, id))
                .build();
        psuAspspPubKey.setKey(publicKey);
        pubKeys.save(psuAspspPubKey);
    }
}
