package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.db.domain.entity.BankProtocol;
import de.adorsys.opba.db.domain.entity.ProtocolAction;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.AuthenticationSessionRepository;
import de.adorsys.opba.db.repository.jpa.BankProtocolRepository;
import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.AuthorizationRequiredResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.ConsentAcquiredResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.RedirectionResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.ValidationErrorResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.error.ErrorResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.ok.SuccessResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.Cause;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeRedirectErrorResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeRedirectResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeResultRedirectable;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeStartAuthorizationResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.RedirectionCause;
import de.adorsys.opba.protocol.facade.dto.result.torest.staticres.FacadeSuccessResult;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.net.URI;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static de.adorsys.opba.db.domain.entity.ProtocolAction.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class ProtocolResultHandler {

    private final CauseMapper<ValidationIssue, Cause> validationIssueMapper;
    private final BankProtocolRepository protocolRepository;
    private final EntityManager entityManager;
    private final AuthenticationSessionRepository authenticationSessions;

    /**
     * This class must ensure that it is separate transaction - so it won't join any other as is used with
     * CompletableFuture.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <O, R extends FacadeServiceableGetter> FacadeResult<O> handleResult(Result<O> result, UUID xRequestId, ServiceContext<R> session) {
        if (result instanceof SuccessResult) {
            return handleSuccess((SuccessResult<O>) result, xRequestId, session);
        }

        if (result instanceof ConsentAcquiredResult) {
            return handleConsentAcquired((ConsentAcquiredResult<O, ?>) result, xRequestId, session);
        }

        if (result instanceof ErrorResult) {
            return handleError((ErrorResult<O>) result, xRequestId, session);
        }

        if (result instanceof RedirectionResult) {
            return handleRedirect((RedirectionResult<O, ?>) result, xRequestId, session);
        }

        throw new IllegalStateException("Can't handle protocol result: " + result.getClass());
    }

    @NotNull
    protected <O, R extends FacadeServiceableGetter> FacadeResult<O> handleSuccess(
        SuccessResult<O> result, UUID xRequestId, ServiceContext<R> session
    ) {
        FacadeSuccessResult<O> mappedResult =
                (FacadeSuccessResult<O>) FacadeSuccessResult.FROM_PROTOCOL.map(result);
        mappedResult.setServiceSessionId(session.getServiceSessionId().toString());
        mappedResult.setXRequestId(xRequestId);
        return mappedResult;
    }

    protected <O, R extends FacadeServiceableGetter> FacadeResult<O> handleError(
        ErrorResult<O> result, UUID xRequestId, ServiceContext<R> session
    ) {
        FacadeRedirectErrorResult<O, RedirectionCause> mappedResult =
            (FacadeRedirectErrorResult<O, RedirectionCause>) FacadeRedirectErrorResult.ERROR_FROM_PROTOCOL.map(result);

        addAuthorizationSessionData(result, xRequestId, session, mappedResult);
        mappedResult.setRedirectionTo(URI.create(session.getFintechRedirectNokUri()));
        return mappedResult;
    }

    protected <O, R extends FacadeServiceableGetter> FacadeResult<O> handleConsentAcquired(
        ConsentAcquiredResult<O, ?> result, UUID xRequestId, ServiceContext<R> session
    ) {
        FacadeRedirectResult<O, RedirectionCause> mappedResult =
            (FacadeRedirectResult<O, RedirectionCause>) FacadeRedirectResult.FROM_PROTOCOL.map(result);

        addAuthorizationSessionData(result, xRequestId, session, mappedResult);
        mappedResult.setRedirectionTo(URI.create(session.getFintechRedirectOkUri()));
        return mappedResult;
    }

    protected <O, R extends FacadeServiceableGetter> FacadeResultRedirectable<O, RedirectionCause> handleRedirect(
        RedirectionResult<O, ?> result, UUID xRequestId, ServiceContext<R> session
    ) {
        if (!authSessionFromDb(session.getServiceSessionId()).isPresent()) {
            return handleAuthorizationStart(result, xRequestId, session);
        }

        return doHandleRedirect(result, xRequestId, session);
    }

    protected <O> FacadeStartAuthorizationResult<O, RedirectionCause> handleAuthorizationStart(
        RedirectionResult<O, ?> result, UUID xRequestId, ServiceContext session
    ) {
        FacadeStartAuthorizationResult<O, RedirectionCause> mappedResult =
            (FacadeStartAuthorizationResult<O, RedirectionCause>) FacadeStartAuthorizationResult.FROM_PROTOCOL.map(result);

        AuthSession auth = addAuthorizationSessionData(result, xRequestId, session, mappedResult);
        mappedResult.setCause(mapCause(auth.getParent().getService().getAction(), result));
        setAspspRedirectCodeIfRequired(result, auth, session);
        return mappedResult;
    }

    protected <O> FacadeRedirectResult<O, RedirectionCause> doHandleRedirect(
        RedirectionResult<O, ?> result, UUID xRequestId, ServiceContext session
    ) {
        FacadeRedirectResult<O, RedirectionCause> mappedResult =
            (FacadeRedirectResult<O, RedirectionCause>) FacadeRedirectResult.FROM_PROTOCOL.map(result);

        AuthSession auth = addAuthorizationSessionData(result, xRequestId, session, mappedResult);
        mappedResult.setCause(mapCause(auth.getParent().getService().getAction(), result));
        setAspspRedirectCodeIfRequired(result, auth, session);
        return mappedResult;
    }


    protected <O> void setAspspRedirectCodeIfRequired(RedirectionResult<O, ?> result, AuthSession session, ServiceContext context) {
        if (result instanceof AuthorizationRequiredResult) {
            session.setAspspRedirectCode(context.getFutureAspspRedirectCode().toString());
        }
    }

    protected <O> AuthSession addAuthorizationSessionData(Result<O> result, UUID xRequestId, ServiceContext session,
                                                 FacadeResultRedirectable<O, ?> mappedResult) {
        AuthSession authSession = updateAuthContext(result, session);
        mappedResult.setAuthorizationSessionId(authSession.getId().toString());
        mappedResult.setServiceSessionId(authSession.getParent().getId().toString());
        mappedResult.setXRequestId(xRequestId);
        mappedResult.setRedirectCode(authSession.getRedirectCode());
        return authSession;
    }

    protected <O> AuthSession updateAuthContext(Result<O> result, ServiceContext session) {
        // Auth session is 1-1 to service session, using id as foreign key
        return authSessionFromDb(session.getServiceSessionId())
                .map(it -> updateExistingAuthSession(result, session, it))
                .orElseGet(() -> createNewAuthSession(result, session));
    }

    protected Optional<AuthSession> authSessionFromDb(UUID serviceSessionId) {
        return authenticationSessions.findByParentId(serviceSessionId);
    }

    @NotNull
    protected <O> AuthSession createNewAuthSession(Result<O> result, ServiceContext session) {
        BankProtocol authProtocol = protocolRepository
                .findByBankProfileUuidAndAction(session.getBankId(), AUTHORIZATION)
                .orElseThrow(
                        () -> new IllegalStateException("Missing update authorization handler for " + session.getBankId())
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
    protected <O> AuthSession updateExistingAuthSession(Result<O> result, ServiceContext session, AuthSession it) {
        it.setRedirectCode(session.getFutureRedirectCode().toString());
        it.setContext(result.authContext());
        return authenticationSessions.save(it);
    }

    protected RedirectionCause mapCause(ProtocolAction action, RedirectionResult result) {
        if (result instanceof ValidationErrorResult && null != result.getCause()) {
            Set<ValidationIssue> issues = (Set<ValidationIssue>) result.getCause();
            return new RedirectionCause(
                action,
                issues.stream().map(validationIssueMapper::map).collect(Collectors.toSet())
            );
        }

        return null;
    }
}
