package de.adorsys.opba.protocol.facade.services.authorization.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.EncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.config.encryption.KeyGeneratorConfig;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechUserSecureStorage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;

@Service
@RequiredArgsConstructor
public class SecretKeyForAuthorizationExchangeService {

    private final KeyGeneratorConfig.ConsentSpecSecretKeyGenerator specSecretKeyGenerator;
    private final AuthorizationSessionRepository authSessions;
    private final EncryptionServiceProvider provider;
    private final ObjectMapper mapper;

    @SneakyThrows
    @Transactional
    public SecretKey encryptAndStoreForFuture(AuthSession session, FintechUserSecureStorage.FinTechUserInboxData data) {
        SecretKey key = specSecretKeyGenerator.generate();
        EncryptionService service = provider.forSecretKey(key);
        session.setConsentSpec(service, mapper.writeValueAsString(data));
        authSessions.save(session);
        return key;
    }
}
