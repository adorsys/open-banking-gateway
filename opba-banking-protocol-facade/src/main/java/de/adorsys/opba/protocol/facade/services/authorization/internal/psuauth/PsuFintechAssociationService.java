package de.adorsys.opba.protocol.facade.services.authorization.internal.psuauth;

import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.protocol.facade.config.encryption.SecretKeyWithIv;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechUserSecureStorage;
import de.adorsys.opba.protocol.facade.config.encryption.impl.psu.PsuSecureStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PsuFintechAssociationService {

    private final FintechSecureStorage fintechVault;
    private final PsuSecureStorage psuVault;
    private final FintechUserSecureStorage vault;

    @Transactional
    public void sharePsuAspspSecretKeyWithFintech(String psuPassword, AuthSession session) {
        SecretKeyWithIv psuAspspKey = psuVault.getOrCreateKeyFromPrivateForAspsp(
                psuPassword::toCharArray,
                session
        );

        fintechVault.psuAspspKeyToInbox(session, psuAspspKey);
    }

    @Transactional
    public FintechUserSecureStorage.FinTechUserInboxData associatePsuAspspWithFintechUser(AuthSession session, String fintechUserPassword) {
        return vault.fromInboxForAuth(
                session,
                fintechUserPassword::toCharArray
        );
    }
}
