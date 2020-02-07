package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.db.domain.entity.BankProtocol;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.AuthenticationSessionRepository;
import de.adorsys.opba.db.repository.jpa.BankProtocolRepository;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.RedirectionResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.ok.SuccessResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeRedirectResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeSuccessResult;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.UUID;

import static de.adorsys.opba.db.domain.entity.ProtocolAction.UPDATE_AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class ProtocolResultHandler {

    private final BankProtocolRepository protocolRepository;
    private final EntityManager entityManager;
    private final AuthenticationSessionRepository authenticationSessions;

    @Transactional
    public <O> FacadeResult<O> handleResult(Result<O> result, UUID xRequestId, ServiceContext session) {
        if (result instanceof SuccessResult) {
            return handleSuccess((SuccessResult<O>) result, xRequestId, session);
        }

        if (result instanceof RedirectionResult) {
            return handleRedirect(result, xRequestId, session);
        }

        throw new IllegalStateException("Can't handle protocol result: " + result.getClass());
    }

    @NotNull
    private <O> FacadeResult<O> handleSuccess(SuccessResult<O> result, UUID xRequestId, ServiceContext session) {
        FacadeSuccessResult<O> mappedResult =
                (FacadeSuccessResult<O>) FacadeSuccessResult.FROM_PROTOCOL.map(result);
        mappedResult.setServiceSessionId(session.getServiceSessionId().toString());
        mappedResult.setXRequestId(xRequestId);
        return mappedResult;
    }

    private <O> FacadeResult<O> handleRedirect(Result<O> result, UUID xRequestId, ServiceContext session) {
        AuthSession authSession = updateAuthContext(result, session);

        FacadeRedirectResult<O> mappedResult =
                (FacadeRedirectResult<O>) FacadeRedirectResult.FROM_PROTOCOL.map((RedirectionResult) result);

        mappedResult.setAuthorizationSessionId(authSession.getId().toString());
        mappedResult.setServiceSessionId(authSession.getParent().getId().toString());
        mappedResult.setXRequestId(xRequestId);
        mappedResult.setRedirectCode(authSession.getRedirectCode());

        return mappedResult;
    }

    private <O> AuthSession updateAuthContext(Result<O> result, ServiceContext session) {
        // Auth session is 1-1 to service session, using id as foreign key
        return authenticationSessions.findByParentId(session.getServiceSessionId())
                .map(it -> updateExistingAuthSession(result, it))
                .orElseGet(() -> createNewAuthSession(result, session));
    }

    @NotNull
    private <O> AuthSession createNewAuthSession(Result<O> result, ServiceContext session) {
        BankProtocol authProtocol = protocolRepository
                .findByBankProfileUuidAndAction(session.getBankId(), UPDATE_AUTHORIZATION)
                .orElseThrow(
                        () -> new IllegalStateException("Missing update authorization handler for " + session.getBankId())
                );

        return authenticationSessions.save(
                AuthSession.builder()
                        .parent(entityManager.find(ServiceSession.class, session.getServiceSessionId()))
                        .protocol(authProtocol)
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
