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
import de.adorsys.opba.protocol.api.services.SecretKeyService;
import de.adorsys.opba.protocol.facade.dto.ContextWithKey;
import de.adorsys.opba.protocol.facade.dto.KeyDTO;
import de.adorsys.opba.protocol.facade.config.EncryptionProperties;
import de.adorsys.opba.protocol.facade.utils.ArrUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

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
    private final FacadeEncryptionService facadeEncryptionService;
    private final EncryptionProperties properties;
    private final SecretKeyService secretKeyService;

    @Transactional
    @SneakyThrows
    public <T extends FacadeServiceableGetter> ServiceContext<T> provide(T request) {
        FacadeServiceableRequest facadeServiceable = request.getFacadeServiceable();
        if (null == facadeServiceable) {
            throw new IllegalArgumentException("No serviceable body");
        }

        UUID serviceSessionId = facadeServiceable.getServiceSessionId();
        String sessionPassword = facadeServiceable.getSessionPassword();
        KeyDTO keyDTO = getSessionSecretKey(serviceSessionId, sessionPassword);
        EncryptionService encryptionService = facadeEncryptionService.provideEncryptionService(keyDTO.getKey());

        AuthSession authSession = extractAndValidateAuthSession(request);

        Supplier<ContextWithKey> supplier = () -> getEncryptedContext(facadeServiceable, encryptionService, keyDTO);
        ServiceSession session = extractOrCreateServiceSession(request, authSession, supplier);

        return ServiceContext.<T>builder()
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

    @NotNull
    @SneakyThrows
    private ContextWithKey getEncryptedContext(FacadeServiceableRequest facadeServiceable,
                                               EncryptionService encryptionService,
                                               KeyDTO keyDTO) {
        String encryptedContext = new String(encryptionService.encrypt(MAPPER.writeValueAsBytes(facadeServiceable)));
        return new ContextWithKey(encryptedContext, keyDTO);
    }

    private <T extends FacadeServiceableGetter> ServiceSession extractOrCreateServiceSession(
            T request,
            AuthSession authSession,
            Supplier<ContextWithKey> encryptedContext
    ) {
        if (null != authSession) {
            return authSession.getParent();
        } else {
            return findServiceSessionByIdOrCreate(request, encryptedContext);
        }
    }

    @NotNull
    @SneakyThrows
    private <T extends FacadeServiceableGetter> ServiceSession findServiceSessionByIdOrCreate(
            T request,
            Supplier<ContextWithKey> contextWithKey
    ) {
        FacadeServiceableRequest facadeServiceable = request.getFacadeServiceable();
        UUID serviceSessionId = facadeServiceable.getServiceSessionId();

        ServiceSession session = new ServiceSession();
        if (null != serviceSessionId) {
            session.setId(serviceSessionId);
        }

        session.setContext(contextWithKey.get().getEncryptedContext());
        session.setFintechOkUri(facadeServiceable.getFintechRedirectUrlOk());
        session.setFintechNokUri(facadeServiceable.getFintechRedirectUrlNok());
        session.setSecretKey(secretKeyService.encrypt(contextWithKey.get().getKeyDTO().getKey()));
        session.setAlgo(properties.getAlgorithm());
        session.setSalt(contextWithKey.get().getKeyDTO().getSalt());
        session.setIterCount(properties.getIterationCount());
        return serviceSessions.save(session);
    }

    private KeyDTO getSessionSecretKey(UUID serviceSessionId, String sessionPassword) {

        // new secret key
        if (null == serviceSessionId) {
            if (Strings.isNullOrEmpty(sessionPassword)) {
                throw new RuntimeException("No password. Can't generate secret key");
            }
            byte[] salt = getNewSalt(properties.getSaltLength());
            byte[] key = secretKeyService.generateKey(sessionPassword, salt);
            return new KeyDTO(key, salt);
        }

        Optional<ServiceSession> existingSession = serviceSessions.findById(serviceSessionId);
        if (!existingSession.isPresent()) {
            throw new RuntimeException("Session not found");
        }

        // existing saved key
        byte[] secretKey = existingSession.get().getSecretKey();
        if (!ArrUtils.isEmpty(secretKey)) {
            byte[] decryptedKey = secretKeyService.decrypt(secretKey);
            return new KeyDTO(decryptedKey, null);
        }

        // recreate deleted key from password with parameters from db
        ServiceSession session = existingSession.get();
        byte[] key = secretKeyService.generateKey(sessionPassword, session.getAlgo(), session.getSalt(), session.getIterCount());
        return new KeyDTO(key, null);
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
