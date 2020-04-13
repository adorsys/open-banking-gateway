package de.adorsys.opba.protocol.facade.services.authorization.internal.psuauth;

import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.repository.jpa.fintech.FintechUserRepository;
import de.adorsys.opba.protocol.facade.config.encryption.SecretKeyWithIv;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechUserSecureStorage;
import de.adorsys.opba.protocol.facade.config.encryption.impl.psu.PsuSecureStorage;
import de.adorsys.opba.protocol.facade.services.authorization.internal.SecretKeyForAuthorizationExchangeService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PsuFintechAssociationService {

    private final FintechSecureStorage fintechVault;
    private final PsuSecureStorage psuVault;
    private final FintechUserSecureStorage vault;
    private final FintechUserRepository fintechUserRepository;
    private final SecretKeyForAuthorizationExchangeService keyExchange;

    @Transactional
    public void shareAspspSecretKeyWithFintech(String psuPassword, AuthSession session) {
        SecretKeyWithIv psuAspspKey = psuVault.getOrCreateKeyFromPrivateForAspsp(
                psuPassword::toCharArray,
                session
        );

        fintechVault.psuAspspKeyToInbox(session, psuAspspKey);
    }

    @Transactional
    public Association associateAspspWithFintech(AuthSession session, String fintechUserPassword) {
        FintechUserSecureStorage.FinTechUserInboxData data = vault.fromInboxForAuth(
                session,
                fintechUserPassword::toCharArray
        );

        SecretKeyWithIv specEncryptionKey = keyExchange.encryptAndStoreForFuture(session, data);
        fintechUserRepository.delete(session.getFintechUser());
        return new Association(specEncryptionKey, data);
    }

    @Getter
    @RequiredArgsConstructor
    public static class Association {

        private final SecretKeyWithIv secretKey;
        private final FintechUserSecureStorage.FinTechUserInboxData consentSpec;
    }
}
