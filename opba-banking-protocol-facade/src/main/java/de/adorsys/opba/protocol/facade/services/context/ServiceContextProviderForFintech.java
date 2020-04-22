package de.adorsys.opba.protocol.facade.services.context;

import com.google.common.base.Strings;
import de.adorsys.opba.db.domain.entity.BankProfile;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechRepository;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.services.scoped.RequestScoped;
import de.adorsys.opba.protocol.facade.config.encryption.ConsentAuthorizationEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import de.adorsys.opba.protocol.facade.services.EncryptionKeySerde;
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

    private final FintechSecureStorage fintechSecureStorage;
    private final FintechRepository fintechRepository;
    private final BankProfileJpaRepository profileJpaRepository;
    private final ConsentAuthorizationEncryptionServiceProvider consentAuthorizationEncryptionServiceProvider;
    private final RequestScopedProvider provider;
    private final EncryptionKeySerde encryptionKeySerde;
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
                .requestScoped(getRequestScoped(request, session, authSession))
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
            return createServiceSession(UUID.randomUUID());
        }

        return serviceSessions.findById(serviceSessionId)
            .orElseGet(() -> createServiceSession(serviceSessionId));
    }

    @NotNull
    @SneakyThrows
    private ServiceSession createServiceSession(UUID serviceSessionId) {
        ServiceSession serviceSession = new ServiceSession();
        serviceSession.setId(serviceSessionId);
        return serviceSessions.save(serviceSession);
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
    private <T extends FacadeServiceableGetter> RequestScoped getRequestScoped(T request, ServiceSession session, AuthSession authSession) {
        return null == request.getFacadeServiceable().getAuthorizationKey()
                ? fintechFacingSecretKeyBasedEncryption(request, session)
                : psuCookieBasedKeyEncryption(request, authSession);
    }

    private <T extends FacadeServiceableGetter> RequestScoped psuCookieBasedKeyEncryption(T request, AuthSession session) {
        if (null == session) {
            throw new IllegalArgumentException("Missing authorization session");
        }

        return provider.registerForPsuSession(
                session,
                consentAuthorizationEncryptionServiceProvider,
                encryptionKeySerde.fromString(request.getFacadeServiceable().getAuthorizationKey())
        );
    }

    /**
     * To be consumed by {@link de.adorsys.opba.protocol.facade.services.NewAuthSessionHandler} if new auth session started.
     */
    private <T extends FacadeServiceableGetter> RequestScoped fintechFacingSecretKeyBasedEncryption(T request, ServiceSession session) {
        BankProfile profile = profileJpaRepository.findByBankUuid(request.getFacadeServiceable().getBankId())
                .orElseThrow(() -> new IllegalArgumentException("No bank profile for bank: " + request.getFacadeServiceable().getBankId()));

        // FinTech requests should be signed, so creating Fintech entity if it does not exist.
        Fintech fintech = fintechRepository.findByGlobalId(request.getFacadeServiceable().getAuthorization())
                .orElseGet(() -> registerFintech(request, request.getFacadeServiceable().getAuthorization()));
        fintechSecureStorage.validatePassword(fintech, () -> request.getFacadeServiceable().getSessionPassword().toCharArray());

        return provider.registerForFintechSession(
                fintech,
                profile,
                session,
                consentAuthorizationEncryptionServiceProvider,
                consentAuthorizationEncryptionServiceProvider.generateKey(),
                () -> request.getFacadeServiceable().getSessionPassword().toCharArray()
        );
    }

    private <T extends FacadeServiceableGetter> Fintech registerFintech(T request, String fintechId) {
        Fintech fintech = fintechRepository.save(Fintech.builder().globalId(fintechId).build());
        fintechSecureStorage.registerFintech(fintech, () -> request.getFacadeServiceable().getSessionPassword().toCharArray());
        return fintech;
    }
}
