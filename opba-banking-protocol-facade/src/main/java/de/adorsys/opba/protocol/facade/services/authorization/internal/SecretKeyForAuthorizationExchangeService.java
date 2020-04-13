package de.adorsys.opba.protocol.facade.services.authorization.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.facade.config.encryption.ConsentAuthorizationEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.config.encryption.SecretKeyWithIv;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechUserSecureStorage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SecretKeyForAuthorizationExchangeService {

    private final ConsentAuthorizationEncryptionServiceProvider encryptionServiceProvider;
    private final AuthorizationSessionRepository authSessions;
    private final ObjectMapper mapper;

    @SneakyThrows
    @Transactional
    public SecretKeyWithIv encryptAndStoreForFuture(AuthSession session, FintechUserSecureStorage.FinTechUserInboxData data) {
        SecretKeyWithIv key = encryptionServiceProvider.generateKey();
        EncryptionService service = encryptionServiceProvider.forSecretKey(key);
        session.setConsentSpec(service, mapper.writeValueAsString(data));
        authSessions.save(session);
        return key;
    }
}
