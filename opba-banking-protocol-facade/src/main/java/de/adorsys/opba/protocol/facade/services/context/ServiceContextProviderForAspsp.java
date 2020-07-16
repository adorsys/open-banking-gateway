package de.adorsys.opba.protocol.facade.services.context;

import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechOnlyPubKeyRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechRepository;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.facade.config.encryption.ConsentAuthorizationEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.config.encryption.FintechOnlyEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.config.encryption.FintechOnlyKeyPairConfig;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import de.adorsys.opba.protocol.facade.services.EncryptionKeySerde;
import de.adorsys.opba.protocol.facade.services.scoped.RequestScopedProvider;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.Objects;

@Service(ServiceContextProviderForAspsp.ASPSP_CONTEXT_PROVIDER)
public class ServiceContextProviderForAspsp extends ServiceContextProviderForFintech {

    public static final String ASPSP_CONTEXT_PROVIDER = "ASPSP_CONTEXT_PROVIDER";

    public ServiceContextProviderForAspsp(AuthorizationSessionRepository authSessions, FintechSecureStorage fintechSecureStorage, EntityManager entityManager, FintechOnlyPubKeyRepository pubKeyRepository, FintechOnlyKeyPairConfig fintechOnlyKeyPairConfig, FintechOnlyEncryptionServiceProvider fintechOnlyEncryptionServiceProvider, FintechRepository fintechRepository, BankProfileJpaRepository profileJpaRepository, ConsentAuthorizationEncryptionServiceProvider consentAuthorizationEncryptionServiceProvider, RequestScopedProvider provider, EncryptionKeySerde encryptionKeySerde, ServiceSessionRepository serviceSessions) {
        super(authSessions, fintechSecureStorage, entityManager, pubKeyRepository, fintechOnlyKeyPairConfig, fintechOnlyEncryptionServiceProvider, fintechRepository, profileJpaRepository, consentAuthorizationEncryptionServiceProvider, provider, encryptionKeySerde, serviceSessions);
    }

    protected <T extends FacadeServiceableGetter> void validateRedirectCode(T request, AuthSession session) {
        if (!Objects.equals(session.getAspspRedirectCode(), request.getFacadeServiceable().getRedirectCode())) {
            throw new IllegalArgumentException("Wrong redirect code");
        }
    }
}
