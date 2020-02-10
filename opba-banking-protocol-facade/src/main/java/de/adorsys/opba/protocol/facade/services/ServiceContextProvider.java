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
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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

    @Transactional
    @SneakyThrows
    public <T extends FacadeServiceableGetter> ServiceContext<T> provide(T request) {
        if (null == request.getFacadeServiceable()) {
            throw new IllegalArgumentException("No serviceable body");
        }

        AuthSession authSession = extractAndValidateAuthSession(request);
        ServiceSession session = extractOrCreateServiceSession(request, authSession);

        return ServiceContext.<T>builder()
                .serviceSessionId(session.getId())
                .serviceBankProtocolId(null == authSession ? null : authSession.getParent().getProtocol().getId())
                .authorizationBankProtocolId(null == authSession ? null : authSession.getProtocol().getId())
                .bankId(request.getFacadeServiceable().getBankID())
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

    @SneakyThrows
    private <T extends FacadeServiceableGetter> ServiceSession extractOrCreateServiceSession(
            T request,
            AuthSession authSession
    ) {
        if (null != authSession) {
            return authSession.getParent();
        } else {
            ServiceSession session = new ServiceSession();

            if (null != request.getFacadeServiceable().getServiceSessionId()) {
                session.setId(request.getFacadeServiceable().getServiceSessionId());
            }

            session.setContext(MAPPER.writeValueAsString(request));
            session.setFintechOkUri(request.getFacadeServiceable().getFintechRedirectURLOK());
            session.setFintechNokUri(request.getFacadeServiceable().getFintechRedirectURLNOK());
            return serviceSessions.save(session);
        }
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
