package de.adorsys.opba.protocol.facade.services.context;

import com.google.common.base.Strings;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.services.scoped.RequestScoped;
import de.adorsys.opba.protocol.facade.config.encryption.ConsentAuthorizationEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.services.SecretKeySerde;
import de.adorsys.opba.protocol.facade.services.scoped.RequestScopedProvider;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service(ServiceContextProviderForFintech.FINTECH_CONTEXT_PROVIDER)
@RequiredArgsConstructor
public class ServiceContextProviderForFintech implements ServiceContextProvider {

    public static final String FINTECH_CONTEXT_PROVIDER = "FINTECH_CONTEXT_PROVIDER";

    protected final AuthorizationSessionRepository authSessions;

    private final ConsentAuthorizationEncryptionServiceProvider consentAuthorizationEncryptionServiceProvider;
    private final RequestScopedProvider provider;
    private final SecretKeySerde secretKeySerde;
    private final ServiceSessionRepository serviceSessions;

    @Override
    @Transactional
    @SneakyThrows
    public <T extends FacadeServiceableGetter> ServiceContext<T> provide(T request) {
        if (null == request.getFacadeServiceable()) {
            throw new IllegalArgumentException("No serviceable body");
        }
        AuthSession authSession = extractAndValidateAuthSession(request);
        ServiceSession session = extractOrCreateServiceSession(request, authSession);
        return ServiceContext.<T>builder()
                .requestScoped(getRequestScoped(request))
                .serviceSessionId(session.getId())
                .serviceBankProtocolId(null == authSession ? null : authSession.getParent().getProtocol().getId())
                .authorizationBankProtocolId(null == authSession ? null : authSession.getProtocol().getId())
                .bankId(request.getFacadeServiceable().getBankId())
                .authSessionId(null == authSession ? null : authSession.getId())
                .authContext(null == authSession ? null : authSession.getContext())
                // Currently 1-1 auth-session to service session
                .futureAuthSessionId(session.getId())
                .futureRedirectCode(UUID.randomUUID())
                .futureAspspRedirectCode(UUID.randomUUID())
                .request(request)
                .build();
    }

    protected <T extends FacadeServiceableGetter> void validateRedirectCode(T request, AuthSession session) {
        if (Strings.isNullOrEmpty(request.getFacadeServiceable().getRedirectCode())) {
            throw new IllegalArgumentException("Missing redirect code");
        }

        if (!Objects.equals(session.getRedirectCode(), request.getFacadeServiceable().getRedirectCode())) {
            throw new IllegalArgumentException("Wrong redirect code");
        }
    }

    private <T extends FacadeServiceableGetter> AuthSession readAndValidateAuthSession(T request) {
        UUID sessionId = UUID.fromString(request.getFacadeServiceable().getAuthorizationSessionId());
        AuthSession session = authSessions.findById(sessionId)
            .orElseThrow(() -> new IllegalStateException("No auth session " + sessionId));

        validateRedirectCode(request, session);

        return session;
    }

    private <T extends FacadeServiceableGetter> ServiceSession extractOrCreateServiceSession(
            T request,
            AuthSession authSession
    ) {
        if (null != authSession) {
            return authSession.getParent();
        } else {
            return readOrCreateServiceSessionFromRequest(request.getFacadeServiceable());
        }
    }

    private ServiceSession readOrCreateServiceSessionFromRequest(FacadeServiceableRequest facadeServiceable) {
        UUID serviceSessionId = facadeServiceable.getServiceSessionId();

        if (null == serviceSessionId) {
            return createServiceSession();
        }

        return serviceSessions.findById(serviceSessionId)
            .orElseGet(this::createServiceSession);
    }

    @NotNull
    @SneakyThrows
    private ServiceSession createServiceSession() {
        return serviceSessions.save(new ServiceSession());
    }

    @SneakyThrows
    private <T extends FacadeServiceableGetter> AuthSession extractAndValidateAuthSession(
            T request) {
        if (null == request.getFacadeServiceable().getAuthorizationSessionId()) {
            return handleNoAuthSession(request);
        }

        return readAndValidateAuthSession(request);
    }

    private <T extends FacadeServiceableGetter> AuthSession handleNoAuthSession(T request) {
        if (!Strings.isNullOrEmpty(request.getFacadeServiceable().getRedirectCode())) {
            throw new IllegalArgumentException("Unexpected redirect code as no auth session is present");
        }

        return null;
    }

    @Nullable
    private <T extends FacadeServiceableGetter> RequestScoped getRequestScoped(T request) {
        return null == request.getFacadeServiceable().getAuthorizationKey()
                ? fintechUserFacingSecretKeyBasedEncryption(request)
                : cookieBasedKeyEncryption(request);
    }

    private <T extends FacadeServiceableGetter> RequestScoped cookieBasedKeyEncryption(T request) {
        return provider.register(
                request.getFacadeServiceable(),
                consentAuthorizationEncryptionServiceProvider,
                secretKeySerde.fromString(request.getFacadeServiceable().getAuthorizationKey())
        );
    }

    /**
     * To be consumed by {@link de.adorsys.opba.protocol.facade.services.NewAuthSessionHandler} if new auth session started.
     */
    private <T extends FacadeServiceableGetter> RequestScoped fintechUserFacingSecretKeyBasedEncryption(T request) {
        return provider.register(
                request.getFacadeServiceable(),
                consentAuthorizationEncryptionServiceProvider,
                consentAuthorizationEncryptionServiceProvider.generateKey()
        );
    }
}
