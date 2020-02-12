package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.db.domain.entity.BankProtocol;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.AuthenticationSessionRepository;
import de.adorsys.opba.db.repository.jpa.BankProtocolRepository;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.ConsentAcquiredResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.RedirectionResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.error.ErrorResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.ok.SuccessResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeRedirectErrorResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeRedirectResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeResultRedirectable;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeStartAuthorizationResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.staticres.FacadeSuccessResult;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static de.adorsys.opba.db.domain.entity.ProtocolAction.UPDATE_AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class ProtocolResultHandler {

    private final BankProtocolRepository protocolRepository;
    private final EntityManager entityManager;
    private final AuthenticationSessionRepository authenticationSessions;

    @Transactional
    public <O, R extends FacadeServiceableGetter> FacadeResult<O> handleResult(Result<O> result, UUID xRequestId, ServiceContext<R> session) {
        if (result instanceof SuccessResult) {
            return handleSuccess((SuccessResult<O>) result, xRequestId, session);
        }

        if (result instanceof ConsentAcquiredResult) {
            return handleConsentAcquired((ConsentAcquiredResult<O>) result, xRequestId, session);
        }

        if (result instanceof ErrorResult) {
            return handleError((ErrorResult<O>) result, xRequestId, session);
        }

        if (result instanceof RedirectionResult) {
            return handleRedirect((RedirectionResult<O>) result, xRequestId, session);
        }

        throw new IllegalStateException("Can't handle protocol result: " + result.getClass());
    }

    @NotNull
    private <O, R extends FacadeServiceableGetter> FacadeResult<O> handleSuccess(
        SuccessResult<O> result, UUID xRequestId, ServiceContext<R> session
    ) {
        FacadeSuccessResult<O> mappedResult =
                (FacadeSuccessResult<O>) FacadeSuccessResult.FROM_PROTOCOL.map(result);
        mappedResult.setServiceSessionId(session.getServiceSessionId().toString());
        mappedResult.setXRequestId(xRequestId);
        return mappedResult;
    }

    private <O, R extends FacadeServiceableGetter> FacadeResult<O> handleError(
        ErrorResult<O> result, UUID xRequestId, ServiceContext<R> session
    ) {
        FacadeRedirectErrorResult<O> mappedResult =
            (FacadeRedirectErrorResult<O>) FacadeRedirectErrorResult.ERROR_FROM_PROTOCOL.map(result);

        addAuthorizationSessionData(result, xRequestId, session, mappedResult);
        mappedResult.setRedirectionTo(URI.create(session.getFintechRedirectNokUri()));
        return mappedResult;
    }

    private <O, R extends FacadeServiceableGetter> FacadeResult<O> handleConsentAcquired(
        ConsentAcquiredResult<O> result, UUID xRequestId, ServiceContext<R> session
    ) {
        FacadeRedirectResult<O> mappedResult = (FacadeRedirectResult<O>) FacadeRedirectResult.FROM_PROTOCOL.map(result);

        addAuthorizationSessionData(result, xRequestId, session, mappedResult);
        mappedResult.setRedirectionTo(URI.create(session.getFintechRedirectOkUri()));
        return mappedResult;
    }

    private <O, R extends FacadeServiceableGetter> FacadeResult<O> handleRedirect(
        RedirectionResult<O> result, UUID xRequestId, ServiceContext<R> session
    ) {
        if (!authSessionFromDb(session).isPresent()) {
            return handleAuthorizationStart(result, xRequestId, session);
        }

        return defaultHandleRedirect(result, xRequestId, session);
    }

    private <O> FacadeResult<O> handleAuthorizationStart(RedirectionResult<O> result, UUID xRequestId, ServiceContext session) {
        FacadeStartAuthorizationResult<O> mappedResult =
            (FacadeStartAuthorizationResult<O>) FacadeStartAuthorizationResult.FROM_PROTOCOL.map(result);

        addAuthorizationSessionData(result, xRequestId, session, mappedResult);
        return mappedResult;
    }

    private <O> FacadeResult<O> defaultHandleRedirect(RedirectionResult<O> result, UUID xRequestId, ServiceContext session) {
        FacadeRedirectResult<O> mappedResult = (FacadeRedirectResult<O>) FacadeRedirectResult.FROM_PROTOCOL.map(result);

        addAuthorizationSessionData(result, xRequestId, session, mappedResult);
        return mappedResult;
    }

    private <O> void addAuthorizationSessionData(Result<O> result, UUID xRequestId, ServiceContext session,
                                                 FacadeResultRedirectable<O> mappedResult) {
        AuthSession authSession = updateAuthContext(result, session);
        mappedResult.setAuthorizationSessionId(authSession.getId().toString());
        mappedResult.setServiceSessionId(authSession.getParent().getId().toString());
        mappedResult.setXRequestId(xRequestId);
        mappedResult.setRedirectCode(authSession.getRedirectCode());
    }

    private <O> AuthSession updateAuthContext(Result<O> result, ServiceContext session) {
        // Auth session is 1-1 to service session, using id as foreign key
        return authSessionFromDb(session)
                .map(it -> updateExistingAuthSession(result, session, it))
                .orElseGet(() -> createNewAuthSession(result, session));
    }

    private Optional<AuthSession> authSessionFromDb(ServiceContext session) {
        return authenticationSessions.findByParentId(session.getServiceSessionId());
    }

    @NotNull
    private <O> AuthSession createNewAuthSession(Result<O> result, ServiceContext session) {
        BankProtocol authProtocol = protocolRepository
                .findByBankProfileUuidAndAction(session.getBankID(), UPDATE_AUTHORIZATION)
                .orElseThrow(
                        () -> new IllegalStateException("Missing update authorization handler for " + session.getBankID())
                );

        return authenticationSessions.save(
                AuthSession.builder()
                        .parent(entityManager.find(ServiceSession.class, session.getServiceSessionId()))
                        .protocol(authProtocol)
                        .context(result.authContext())
                        .redirectCode(session.getFutureRedirectCode().toString())
                        .build()
        );
    }

    @NotNull
    private <O> AuthSession updateExistingAuthSession(Result<O> result, ServiceContext session, AuthSession it) {
        it.setRedirectCode(session.getFutureRedirectCode().toString());
        it.setContext(result.authContext());
        return authenticationSessions.save(it);
    }
}
