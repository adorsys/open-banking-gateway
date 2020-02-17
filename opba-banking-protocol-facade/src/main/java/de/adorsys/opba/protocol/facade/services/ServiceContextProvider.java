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
import de.adorsys.opba.protocol.facade.config.EncryptionProperties;
import de.adorsys.opba.protocol.facade.dto.KeyDto;
import de.adorsys.opba.protocol.facade.utils.ArrUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static de.adorsys.opba.protocol.facade.utils.EncryptionUtils.getNewSalt;

@Service
@RequiredArgsConstructor
public class ServiceContextProvider {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    private final AuthenticationSessionRepository authSessions;
    private final ServiceSessionRepository serviceSessions;
    private final FacadeEncryptionServiceFactory facadeEncryptionServiceFactory;
    private final EncryptionProperties properties;
    private final SecretKeyOperations secretKeyOperations;

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
            EncryptionService encryptionService = facadeEncryptionServiceFactory.provideEncryptionService(keyDto.getKey());
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

        KeyDto keyDto = getSessionSecretKey(request);
        EncryptionService encryptionService = facadeEncryptionServiceFactory.provideEncryptionService(keyDto.getKey());
        String encryptedContext = new String(encryptionService.encrypt(MAPPER.writeValueAsBytes(facadeServiceable)));

        session.setContext(encryptedContext);
        session.setFintechOkUri(facadeServiceable.getFintechRedirectUrlOk());
        session.setFintechNokUri(facadeServiceable.getFintechRedirectUrlNok());
        session.setSecretKey(secretKeyOperations.encrypt(keyDto.getKey()));
        session.setAlgo(properties.getAlgorithm());
        session.setSalt(keyDto.getSalt());
        session.setIterCount(properties.getIterationCount());
        return new ServiceSessionWithEncryption(serviceSessions.save(session), encryptionService);
    }

    private <T extends FacadeServiceableGetter> KeyDto getSessionSecretKey(T request) {
        FacadeServiceableRequest facadeServiceable = request.getFacadeServiceable();
        UUID serviceSessionId = facadeServiceable.getServiceSessionId();
        String sessionPassword = facadeServiceable.getSessionPassword();
        if (null == serviceSessionId) {
            return newSecretKey(sessionPassword);
        }

        Optional<ServiceSession> existingSession = serviceSessions.findById(serviceSessionId);
        if (!existingSession.isPresent()) {
            throw new RuntimeException("Session not found");
        }

        if (!Strings.isNullOrEmpty(sessionPassword)) {
            return recreateSecretKey(sessionPassword, existingSession.get());
        }

        return savedKey(existingSession.get());
    }

    @NotNull
    private KeyDto savedKey(ServiceSession session) {
        byte[] secretKey = session.getSecretKey();
        if (!ArrUtils.isEmpty(secretKey)) {
            byte[] decryptedKey = secretKeyOperations.decrypt(secretKey);
            return new KeyDto(decryptedKey, null);
        }

        throw new RuntimeException("Can't find secret key. Please provide password to recreate secret key");
    }

    @NotNull
    private KeyDto recreateSecretKey(String sessionPassword, ServiceSession session) {
        byte[] key = secretKeyOperations.generateKey(
                sessionPassword,
                session.getAlgo(),
                session.getSalt(),
                session.getIterCount());
        return new KeyDto(key, null);
    }

    @NotNull
    private KeyDto newSecretKey(String sessionPassword) {
        if (Strings.isNullOrEmpty(sessionPassword)) {
            throw new RuntimeException("No password. Can't generate secret key");
        }
        byte[] salt = getNewSalt(properties.getSaltLength());
        byte[] key = secretKeyOperations.generateKey(sessionPassword, salt);
        return new KeyDto(key, salt);
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
