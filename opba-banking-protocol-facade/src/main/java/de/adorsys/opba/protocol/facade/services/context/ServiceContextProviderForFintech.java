package de.adorsys.opba.protocol.facade.services.context;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Strings;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.AuthenticationSessionRepository;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.SecretKeyOperations;
import de.adorsys.opba.protocol.facade.services.NoEncryptionServiceImpl;
import de.adorsys.opba.protocol.facade.services.ServiceSessionWithEncryption;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service(ServiceContextProviderForFintech.FINTECH_CONTEXT_PROVIDER)
@RequiredArgsConstructor
public class ServiceContextProviderForFintech implements ServiceContextProvider {

    public static final String FINTECH_CONTEXT_PROVIDER = "FINTECH_CONTEXT_PROVIDER";

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    protected final AuthenticationSessionRepository authSessions;
    private final ServiceSessionRepository serviceSessions;
    private final SecretKeyOperations secretKeyOperations;

    @Override
    @Transactional
    @SneakyThrows
    public <T extends FacadeServiceableGetter> ServiceContext<T> provide(T request) {
        if (null == request.getFacadeServiceable()) {
            throw new IllegalArgumentException("No serviceable body");
        }
        AuthSession authSession = extractAndValidateAuthSession(request);
        ServiceSessionWithEncryption session = extractOrCreateServiceSession(request, authSession);
        return ServiceContext.<T>builder()
                .encryption(session.getEncryption())
                .serviceSessionId(session.getId())
                .serviceBankProtocolId(null == authSession ? null : authSession.getParent().getProtocol().getId())
                .authorizationBankProtocolId(null == authSession ? null : authSession.getProtocol().getId())
                .bankId(request.getFacadeServiceable().getBankId())
                .authSessionId(null == authSession ? null : authSession.getId())
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

    private <T extends FacadeServiceableGetter> ServiceSessionWithEncryption extractOrCreateServiceSession(
            T request,
            AuthSession authSession
    ) {
        if (null != authSession) {
            return readServiceSessionFromAuthSession(authSession);
        } else {
            return readOrCreateServiceSessionFromRequest(request.getFacadeServiceable());
        }
    }

    private ServiceSessionWithEncryption readServiceSessionFromAuthSession(AuthSession authSession) {
        return serviceSessionWithEncryption(authSession.getParent());
    }

    private ServiceSessionWithEncryption readOrCreateServiceSessionFromRequest(FacadeServiceableRequest facadeServiceable) {
        UUID serviceSessionId = facadeServiceable.getServiceSessionId();

        if (null == serviceSessionId) {
            return createServiceSession(facadeServiceable);
        }

        return serviceSessions.findById(serviceSessionId)
            .map(this::serviceSessionWithEncryption)
            .orElseGet(() -> createServiceSession(facadeServiceable));
    }

    @NotNull
    @SneakyThrows
    private ServiceSessionWithEncryption createServiceSession(FacadeServiceableRequest facadeServiceable) {
        EncryptionService encryptionService = new NoEncryptionServiceImpl(); // FIXME - this should be removed
        ServiceSession session = new ServiceSession();
        session.setId(facadeServiceable.getServiceSessionId());
        return new ServiceSessionWithEncryption(serviceSessions.save(session), encryptionService);
    }

    @NotNull
    private ServiceSessionWithEncryption serviceSessionWithEncryption(ServiceSession session) {
        EncryptionService encryptionService = new NoEncryptionServiceImpl(); // FIXME - this should be removed
        return new ServiceSessionWithEncryption(session, encryptionService);
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
}
