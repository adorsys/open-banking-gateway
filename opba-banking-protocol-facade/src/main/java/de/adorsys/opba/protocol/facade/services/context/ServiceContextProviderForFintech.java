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
import de.adorsys.opba.protocol.api.dto.context.Context;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.services.scoped.RequestScoped;
import de.adorsys.opba.protocol.facade.config.encryption.ConsentAuthorizationEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.services.EncryptionKeySerde;
import de.adorsys.opba.protocol.facade.services.InternalContext;
import de.adorsys.opba.protocol.facade.services.fintech.FintechAuthenticator;
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

    private final FintechAuthenticator authenticator;
    private final BankProfileJpaRepository profileJpaRepository;
    private final ConsentAuthorizationEncryptionServiceProvider consentAuthorizationEncryptionServiceProvider;
    private final RequestScopedProvider provider;
    private final EncryptionKeySerde encryptionKeySerde;
    private final ServiceSessionRepository serviceSessions;

    @Override
    @Transactional
    @SneakyThrows
    public <REQUEST extends FacadeServiceableGetter, ACTION> InternalContext<REQUEST, ACTION> provide(REQUEST request) {
        if (null == request.getFacadeServiceable()) {
            throw new IllegalArgumentException("No serviceable body");
        }
        AuthSession authSession = extractAndValidateAuthSession(request);
        ServiceSession session = extractOrCreateServiceSession(request, authSession);
        return InternalContext.<REQUEST, ACTION>builder()
                .serviceCtx(Context.<REQUEST>builder()
                        .serviceSessionId(session.getId())
                        .authorizationBankProtocolId(null == authSession ? null : authSession.getAction().getId())
                        .bankId(request.getFacadeServiceable().getBankId())
                        .authSessionId(null == authSession ? null : authSession.getId())
                        .authContext(null == authSession ? null : authSession.getContext())
                        // Currently 1-1 auth-session to service session
                        .futureAuthSessionId(session.getId())
                        .futureRedirectCode(UUID.randomUUID())
                        .futureAspspRedirectCode(UUID.randomUUID())
                        .request(request)
                        .build()
                )
                .authSession(authSession)
                .session(session)
                .build();
    }

    @Override
    public <REQUEST extends FacadeServiceableGetter, ACTION> ServiceContext<REQUEST> provideRequestScoped(REQUEST request, InternalContext<REQUEST, ACTION> ctx) {
        RequestScoped requestScoped = getRequestScoped(request, ctx.getSession(), ctx.getAuthSession(), ctx.getServiceCtx().getServiceBankProtocolId());
        return ServiceContext.<REQUEST>builder().ctx(ctx.getServiceCtx()).requestScoped(requestScoped).build();
    }

    protected <REQUEST extends FacadeServiceableGetter> void validateRedirectCode(REQUEST request, AuthSession session) {
        if (Strings.isNullOrEmpty(request.getFacadeServiceable().getRedirectCode())) {
            throw new IllegalArgumentException("Missing redirect code");
        }

        if (!Objects.equals(session.getRedirectCode(), request.getFacadeServiceable().getRedirectCode())) {
            throw new IllegalArgumentException("Wrong redirect code");
        }
    }

    private <REQUEST extends FacadeServiceableGetter> AuthSession readAndValidateAuthSession(REQUEST request) {
        UUID sessionId = UUID.fromString(request.getFacadeServiceable().getAuthorizationSessionId());
        AuthSession session = authSessions.findById(sessionId)
            .orElseThrow(() -> new IllegalStateException("No auth session " + sessionId));

        validateRedirectCode(request, session);

        return session;
    }

    private <REQUEST extends FacadeServiceableGetter> ServiceSession extractOrCreateServiceSession(
            REQUEST request,
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
            return createServiceSession(UUID.randomUUID(), facadeServiceable);
        }

        return serviceSessions.findById(serviceSessionId)
            .orElseGet(() -> createServiceSession(serviceSessionId, facadeServiceable));
    }

    @NotNull
    @SneakyThrows
    private ServiceSession createServiceSession(UUID serviceSessionId, FacadeServiceableRequest request) {
        ServiceSession serviceSession = new ServiceSession();
        serviceSession.setId(serviceSessionId);
        serviceSession.setBankProfile(getBankProfileFromRequest(request));
        return serviceSessions.save(serviceSession);
    }

    @SneakyThrows
    private <REQUEST extends FacadeServiceableGetter> AuthSession extractAndValidateAuthSession(
            REQUEST request) {
        if (null == request.getFacadeServiceable().getAuthorizationSessionId()) {
            return handleNoAuthSession(request);
        }

        return readAndValidateAuthSession(request);
    }

    private <REQUEST extends FacadeServiceableGetter> AuthSession handleNoAuthSession(REQUEST request) {
        if (!Strings.isNullOrEmpty(request.getFacadeServiceable().getRedirectCode())) {
            throw new IllegalArgumentException("Unexpected redirect code as no auth session is present");
        }

        return null;
    }

    @Nullable
    private <REQUEST extends FacadeServiceableGetter> RequestScoped getRequestScoped(
            REQUEST request,
            ServiceSession session,
            AuthSession authSession,
            long bankProtocolId) {
        return null == request.getFacadeServiceable().getAuthorizationKey()
                ? fintechFacingSecretKeyBasedEncryption(request, session, bankProtocolId)
                : psuCookieBasedKeyEncryption(request, authSession, bankProtocolId);
    }

    private <REQUEST extends FacadeServiceableGetter> RequestScoped psuCookieBasedKeyEncryption(
            REQUEST request,
            AuthSession session,
            long bankProtocolId
    ) {
        if (null == session) {
            throw new IllegalArgumentException("Missing authorization session");
        }

        return provider.registerForPsuSession(
                session,
                consentAuthorizationEncryptionServiceProvider,
                bankProtocolId,
                encryptionKeySerde.fromString(request.getFacadeServiceable().getAuthorizationKey())
        );
    }

    /**
     * To be consumed by {@link de.adorsys.opba.protocol.facade.services.AuthSessionHandler} if new auth session started.
     */
    private <REQUEST extends FacadeServiceableGetter> RequestScoped fintechFacingSecretKeyBasedEncryption(
            REQUEST request,
            ServiceSession session,
            long bankProtocolId
    ) {
        BankProfile profile = getBankProfileFromRequest(request.getFacadeServiceable());

        // FinTech requests should be signed, so creating Fintech entity if it does not exist.
        Fintech fintech = authenticator.authenticateOrCreateFintech(request.getFacadeServiceable());

        return provider.registerForFintechSession(
                fintech,
                profile,
                session,
                bankProtocolId,
                consentAuthorizationEncryptionServiceProvider,
                consentAuthorizationEncryptionServiceProvider.generateKey(),
                () -> request.getFacadeServiceable().getSessionPassword().toCharArray()
        );
    }

    private BankProfile getBankProfileFromRequest(FacadeServiceableRequest request) {
        return profileJpaRepository.findByBankUuid(request.getBankId())
                    .orElseThrow(() -> new IllegalArgumentException("No bank profile for bank: " + request.getBankId()));
    }
}
