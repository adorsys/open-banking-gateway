package de.adorsys.opba.protocol.facade.services;

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
import de.adorsys.opba.protocol.api.dto.KeyWithParamsDto;
import de.adorsys.opba.protocol.api.dto.KeyDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceContextProvider {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    private final AuthenticationSessionRepository authSessions;
    private final ServiceSessionRepository serviceSessions;
    private final SecretKeyOperations secretKeyOperations;
    private final FacadeEncryptionServiceFactory facadeEncryptionServiceFactory;

    @Transactional
    @SneakyThrows
    public <T extends FacadeServiceableGetter> ServiceContext<T> provide(T request) {
        if (null == request.getFacadeServiceable()) {
            throw new IllegalArgumentException("No serviceable body");
        }
        AuthSession authSession = extractAndValidateAuthSession(request);
        ServiceSessionWithEncryption session = extractOrCreateServiceSession(request, authSession);
        return ServiceContext.<T>builder()
                .encryptionService(session.getEncryption())
                .serviceSessionId(session.getId())
                .serviceBankProtocolId(null == authSession ? null : authSession.getParent().getProtocol().getId())
                .authorizationBankProtocolId(null == authSession ? null : authSession.getProtocol().getId())
                .bankId(request.getFacadeServiceable().getBankId())
                .authSessionId(null == authSession ? null : authSession.getId())
                // Currently 1-1 auth-session to service session
                .futureAuthSessionId(session.getId())
                .futureRedirectCode(UUID.randomUUID())
                .request(request)
                .authContext(null == authSession ? null : authSession.getContext())
                .fintechRedirectOkUri(session.getFintechOkUri())
                .fintechRedirectNokUri(session.getFintechNokUri())
                .build();
    }

    private <T extends FacadeServiceableGetter> ServiceSessionWithEncryption extractOrCreateServiceSession(
            T request,
            AuthSession authSession
    ) {
        if (null != authSession) {
            KeyDto keyDto = getSessionSecretKey(request);
            EncryptionService encryptionService = facadeEncryptionServiceFactory
                    .provideEncryptionService(keyDto.getKey());
            return new ServiceSessionWithEncryption(authSession.getParent(), encryptionService);
        } else {
            return createServiceSession(request);
        }
    }

    @NotNull
    @SneakyThrows
    private <T extends FacadeServiceableGetter> ServiceSessionWithEncryption createServiceSession(T request) {
        FacadeServiceableRequest facadeServiceable = request.getFacadeServiceable();
        UUID serviceSessionId = facadeServiceable.getServiceSessionId();

        ServiceSession session = new ServiceSession();
        if (null != serviceSessionId) {
            session.setId(serviceSessionId);
        }

        KeyWithParamsDto keyWithParamsDto = getSessionSecretKey(request);
        EncryptionService encryptionService = facadeEncryptionServiceFactory
                .provideEncryptionService(keyWithParamsDto.getKey());
        String encryptedContext = new String(encryptionService.encrypt(MAPPER.writeValueAsBytes(facadeServiceable)));

        session.setContext(encryptedContext);
        session.setFintechOkUri(facadeServiceable.getFintechRedirectUrlOk());
        session.setFintechNokUri(facadeServiceable.getFintechRedirectUrlNok());
        session.setSecretKey(secretKeyOperations.encrypt(keyWithParamsDto.getKey()));
        session.setAlgo(keyWithParamsDto.getAlgorithm());
        session.setSalt(keyWithParamsDto.getSalt());
        session.setIterCount(keyWithParamsDto.getIterationCount());
        return new ServiceSessionWithEncryption(serviceSessions.save(session), encryptionService);
    }

    private <T extends FacadeServiceableGetter> KeyWithParamsDto getSessionSecretKey(T request) {
        FacadeServiceableRequest facadeServiceable = request.getFacadeServiceable();
        UUID sessionId = facadeServiceable.getServiceSessionId();
        String sessionPassword = facadeServiceable.getSessionPassword();
        if (null == sessionId) {
            return newSecretKey(sessionPassword);
        }

        ServiceSession existingSession = serviceSessions.findById(sessionId)
                .orElseThrow(() -> new IllegalStateException("Session not found for id:" + sessionId));

        if (!Strings.isNullOrEmpty(sessionPassword)) {
            return recreateSecretKey(sessionPassword, existingSession);
        }

        return savedKey(existingSession);
    }

    @NotNull
    private KeyWithParamsDto savedKey(ServiceSession session) {
        byte[] secretKey = session.getSecretKey();
        byte[] decryptedKey = secretKeyOperations.decrypt(secretKey);
        return new KeyWithParamsDto(decryptedKey);
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

        return validateAuthSession(request);
    }

    private <T extends FacadeServiceableGetter> AuthSession handleNoAuthSession(T request) {
        if (!Strings.isNullOrEmpty(request.getFacadeServiceable().getRedirectCode())) {
            throw new IllegalArgumentException("Unexpected redirect code as no auth session is present");
        }

        return null;
    }

    private <T extends FacadeServiceableGetter> AuthSession validateAuthSession(T request) {
        if (Strings.isNullOrEmpty(request.getFacadeServiceable().getRedirectCode())) {
            throw new IllegalArgumentException("Missing redirect code");
        }

        UUID sessionId = UUID.fromString(request.getFacadeServiceable().getAuthorizationSessionId());
        AuthSession session = authSessions.findById(sessionId)
                .orElseThrow(() -> new IllegalStateException("No auth session " + sessionId));

        if (!Objects.equals(session.getRedirectCode(), request.getFacadeServiceable().getRedirectCode())) {
            throw new IllegalArgumentException("Wrong redirect code");
        }

        return session;
    }
}
