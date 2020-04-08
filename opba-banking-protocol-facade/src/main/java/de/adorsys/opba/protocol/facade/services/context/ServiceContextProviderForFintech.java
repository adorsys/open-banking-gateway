package de.adorsys.opba.protocol.facade.services.context;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Strings;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.AuthenticationSessionRepository;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
import de.adorsys.opba.protocol.api.dto.KeyDto;
import de.adorsys.opba.protocol.api.dto.KeyWithParamsDto;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.SecretKeyOperations;
import de.adorsys.opba.protocol.facade.services.FacadeEncryptionServiceFactory;
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
    private final FacadeEncryptionServiceFactory encryptionFactory;

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
                .authContext(null == authSession ? null : authSession.getContext())
                .fintechRedirectOkUri(session.getFintechOkUri())
                .fintechRedirectNokUri(session.getFintechNokUri())
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
            return readServiceSessionFromAuthSession(authSession, request.getFacadeServiceable());
        } else {
            return readOrCreateServiceSessionFromRequest(request.getFacadeServiceable());
        }
    }

    private ServiceSessionWithEncryption readServiceSessionFromAuthSession(AuthSession authSession, FacadeServiceableRequest facadeServiceable) {
        return serviceSessionWithEncryption(authSession.getParent(), facadeServiceable);
    }

    private ServiceSessionWithEncryption readOrCreateServiceSessionFromRequest(FacadeServiceableRequest facadeServiceable) {
        UUID serviceSessionId = facadeServiceable.getServiceSessionId();

        if (null == serviceSessionId) {
            return createServiceSession(facadeServiceable);
        }

        return serviceSessions.findById(serviceSessionId)
            .map(it -> serviceSessionWithEncryption(it, facadeServiceable))
            .orElseGet(() -> createServiceSession(facadeServiceable));
    }

    @NotNull
    @SneakyThrows
    private ServiceSessionWithEncryption createServiceSession(FacadeServiceableRequest facadeServiceable) {
        KeyWithParamsDto keyWithParams = newSecretKey(facadeServiceable.getSessionPassword());
        EncryptionService encryptionService = encryptionFactory.provideEncryptionService(keyWithParams);
        String encryptedContext = new String(encryptionService.encrypt(MAPPER.writeValueAsBytes(facadeServiceable)));

        ServiceSession session = new ServiceSession();
        session.setId(facadeServiceable.getServiceSessionId());
        session.setContext(encryptedContext);
        session.setFintechOkUri(facadeServiceable.getFintechRedirectUrlOk());
        session.setFintechNokUri(facadeServiceable.getFintechRedirectUrlNok());
        session.setSecretKey(secretKeyOperations.encrypt(keyWithParams.getKey()));
        session.setAlgo(keyWithParams.getAlgorithm());
        session.setSalt(keyWithParams.getSalt());
        session.setIterCount(keyWithParams.getIterationCount());
        return new ServiceSessionWithEncryption(serviceSessions.save(session), encryptionService);
    }

    @NotNull
    private ServiceSessionWithEncryption serviceSessionWithEncryption(ServiceSession session, FacadeServiceableRequest facadeServiceable) {
        KeyDto key = deriveFromSessionOrRequest(session, facadeServiceable.getSessionPassword());
        EncryptionService encryptionService = encryptionFactory.provideEncryptionService(key);
        return new ServiceSessionWithEncryption(session, encryptionService);
    }

    private KeyWithParamsDto deriveFromSessionOrRequest(ServiceSession session, String passwordFromRequest) {
        if (null != passwordFromRequest) {
            return recreateSecretKey(passwordFromRequest, session);
        }

        return savedKey(session);
    }

    @NotNull
    private KeyWithParamsDto savedKey(ServiceSession session) {
        byte[] secretKey = session.getSecretKey();
        byte[] decryptedKey = secretKeyOperations.decrypt(secretKey);
        return new KeyWithParamsDto("FIXME", decryptedKey); // FIXME drop this
    }

    @NotNull
    private KeyWithParamsDto recreateSecretKey(String sessionPassword, ServiceSession session) {
        return secretKeyOperations.generateKey(
                sessionPassword,
                session.getAlgo(),
                session.getSalt(),
                session.getIterCount());
    }

    @NotNull
    private KeyWithParamsDto newSecretKey(String sessionPassword) {
        if (Strings.isNullOrEmpty(sessionPassword)) {
            throw new IllegalStateException("No password. Can't generate secret key");
        }
        return secretKeyOperations.generateKey(sessionPassword);
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
