package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.AuthenticationSessionRepository;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.RedirectionResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.SuccessResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeRedirectResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeSuccessResult;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProtocolResultHandler {

    private final EntityManager entityManager;
    private final AuthenticationSessionRepository authenticationSessions;

    @Transactional
    public <O> FacadeResult<O> handleResult(Result<O> result, UUID serviceSessionId) {
        if (result instanceof SuccessResult) {
            return (FacadeSuccessResult<O>) FacadeSuccessResult.FROM_PROTOCOL.map((SuccessResult) result);
        }

        if (result instanceof RedirectionResult) {
            return handleRedirect(result, serviceSessionId);
        }

        throw new IllegalStateException("Can't handle protocol result: " + result.getClass());
    }

    private <O> FacadeResult<O> handleRedirect(Result<O> result, UUID serviceSessionId) {
        updateAuthContext(result, serviceSessionId);
        return (FacadeRedirectResult<O>) FacadeRedirectResult.FROM_PROTOCOL.map((RedirectionResult) result);
    }

    private <O> void updateAuthContext(Result<O> result, UUID serviceSessionId) {
        // Auth session is 1-1 to service session, using id as foreign key
        authenticationSessions.findByParentId(serviceSessionId)
                .map(it -> updateExistingAuthSession(result, it))
                .orElseGet(() -> createNewAuthSession(result, serviceSessionId));
    }

    @NotNull
    private <O> AuthSession createNewAuthSession(Result<O> result, UUID serviceSessionId) {
        return authenticationSessions.save(
                AuthSession.builder()
                        .parent(entityManager.find(ServiceSession.class, serviceSessionId))
                        .context(result.authContext())
                        .redirectCode(UUID.randomUUID().toString())
                        .build()
        );
    }

    @NotNull
    private <O> AuthSession updateExistingAuthSession(Result<O> result, AuthSession it) {
        it.setRedirectCode(UUID.randomUUID().toString());
        it.setContext(result.authContext());
        return authenticationSessions.save(it);
    }
}
