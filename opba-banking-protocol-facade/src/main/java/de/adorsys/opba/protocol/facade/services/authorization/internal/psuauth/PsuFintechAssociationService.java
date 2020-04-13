package de.adorsys.opba.protocol.facade.services.authorization.internal.psuauth;

import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.repository.jpa.fintech.FintechUserRepository;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechUserSecureStorage;
import de.adorsys.opba.protocol.facade.config.encryption.impl.psu.PsuSecureStorage;
import de.adorsys.opba.protocol.facade.services.authorization.internal.SecretKeyForAuthorizationExchangeService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;

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
        SecretKey psuAspspKey = psuVault.getOrCreateKeyFromPrivateForAspsp(
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

        SecretKey specEncryptionKey = keyExchange.encryptAndStoreForFuture(session, data);
        fintechUserRepository.delete(session.getFintechUser());
        return new Association(specEncryptionKey, data);
    }

    @RequiredArgsConstructor
    public static class Association implements SecretKey {

        @Delegate
        private final SecretKey secretKey;

        @Getter
        private final FintechUserSecureStorage.FinTechUserInboxData consentSpec;
    }
}
