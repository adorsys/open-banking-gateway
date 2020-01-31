package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.AuthenticationSessionRepository;
import de.adorsys.opba.protocol.api.dto.result.RedirectionResult;
import de.adorsys.opba.protocol.api.dto.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResultHandler {

    private final EntityManager entityManager;
    private final AuthenticationSessionRepository authenticationSessions;

    @Transactional
    public <R extends Result<?>> R handleResult(R result, UUID serviceSessionId) {
        if (!(result instanceof RedirectionResult)) {
            return result;
        }

        return handleRedirect(result, serviceSessionId);
    }

    private <R extends Result<?>> R handleRedirect(R result, UUID serviceSessionId) {
        // Auth session is 1-1 to service session, using id as foreign key
        AuthSession session = authenticationSessions.findByParentId(serviceSessionId)
                .map(it -> {
                    it.setRedirectCode(UUID.randomUUID().toString());
                    return authenticationSessions.save(it);
                })
                .orElseGet(() ->
                        authenticationSessions.save(
                                AuthSession.builder()
                                        .parent(entityManager.find(ServiceSession.class, serviceSessionId))
                                        .redirectCode(UUID.randomUUID().toString())
                                        .build()
                        )
                );

        return result;
    }
}
