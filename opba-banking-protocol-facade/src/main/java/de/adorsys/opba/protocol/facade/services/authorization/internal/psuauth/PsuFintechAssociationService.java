package de.adorsys.opba.protocol.facade.services.authorization.internal.psuauth;

import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechConsentSpecSecureStorage;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import de.adorsys.opba.protocol.facade.config.encryption.impl.psu.PsuSecureStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.PrivateKey;
import java.security.PublicKey;

@Service
@RequiredArgsConstructor
public class PsuFintechAssociationService {

    private final FintechSecureStorage fintechVault;
    private final PsuSecureStorage psuVault;
    private final FintechConsentSpecSecureStorage vault;

    @Transactional
    public void sharePsuAspspSecretKeyWithFintech(String psuPassword, AuthSession session) {
        PrivateKey psuAspspKey = psuVault.getOrCreateKeyFromPrivateForAspsp(
                psuPassword::toCharArray,
                session,
                this::storePublicKey
        );

        fintechVault.psuAspspKeyToInbox(session, psuAspspKey);
    }

    @Transactional
    public FintechConsentSpecSecureStorage.FinTechUserInboxData associatePsuAspspWithFintechUser(AuthSession session, String fintechUserPassword) {
        return vault.fromInboxForAuth(
                session,
                fintechUserPassword::toCharArray
        );
    }

    private void storePublicKey(PublicKey publicKey) {

    }
}
