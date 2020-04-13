package de.adorsys.opba.protocol.facade.services.authorization.internal;

import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.protocol.facade.config.encryption.KeyGeneratorConfig;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechUserSecureStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
@RequiredArgsConstructor
public class SecretKeyForAuthorizationExchangeService {

    private final KeyGeneratorConfig.ConsentSpecSecretKeyGenerator specSecretKeyGenerator;

    public SecretKey encryptAndStoreForFuture(AuthSession session, FintechUserSecureStorage.FinTechUserInboxData data) {
        SecretKey key = specSecretKeyGenerator.generate();
        return key;
    }
}
