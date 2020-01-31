package de.adorsys.opba.protocol.facade.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Strings;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
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
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    private final ServiceSessionRepository serviceSessions;

    @Transactional
    @SneakyThrows
    public <T extends FacadeServiceableGetter> ServiceContext<T> provide(T request) {
        ServiceSession session = extractOrCreateServiceSession(request);
        AuthSession authSession = extractAndValidateAuthSession(request, session);

        return ServiceContext.<T>builder()
                .facadeServiceable(request.getFacadeServiceable())
                .serviceSessionId(session.getId())
                .authSessionId(null == authSession ? null : authSession.getId())
                .request(request)
                .build();
    }

    @SneakyThrows
    private <T extends FacadeServiceableGetter> ServiceSession extractOrCreateServiceSession(T request) {
        if (null != request.getFacadeServiceable().getServiceSessionId()) {
            UUID sessionId = UUID.fromString(request.getFacadeServiceable().getServiceSessionId());
            return serviceSessions.findById(sessionId).
                    orElseThrow(() -> new IllegalStateException("No service session " + sessionId));
        } else {
            ServiceSession session = new ServiceSession();
            session.setContext(MAPPER.writeValueAsString(request));
            return serviceSessions.save(session);
        }
    }

    @SneakyThrows
    private <T extends FacadeServiceableGetter> AuthSession extractAndValidateAuthSession(
            T request,
            ServiceSession session) {
        if (null == session.getAuthSession()) {
            return handleNoAuthSession(request);
        }

        return validateAuthSession(request, session.getAuthSession());
    }

    private <T extends FacadeServiceableGetter> AuthSession handleNoAuthSession(T request) {
        if (!Strings.isNullOrEmpty(request.getFacadeServiceable().getRedirectCode())) {
            throw new IllegalArgumentException("Unexpected redirect code as no auth session is present");
        }

        return null;
    }

    private <T extends FacadeServiceableGetter> AuthSession validateAuthSession(T request, AuthSession session) {
        if (Strings.isNullOrEmpty(request.getFacadeServiceable().getRedirectCode())) {
            throw new IllegalArgumentException("Missing redirect code");
        }

        if (!Objects.equals(session.getRedirectCode(), request.getFacadeServiceable().getRedirectCode())) {
            throw new IllegalArgumentException("Wrong redirect code");
        }

        return session;
    }
}
