package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AuthStateBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.AuthorizationDeniedResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.AuthorizationRequiredResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.ConsentAcquiredResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.RedirectionResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.ValidationErrorResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.error.ErrorResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.ok.SuccessResult;
import de.adorsys.opba.protocol.facade.config.encryption.SecretKeyWithIv;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeRedirectErrorResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeRedirectResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeResultRedirectable;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeStartAuthorizationResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.staticres.FacadeSuccessResult;
import de.adorsys.opba.protocol.facade.services.scoped.RequestScopedProvider;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProtocolResultHandler {

    private final RequestScopedProvider provider;
    private final NewAuthSessionHandler newAuthSessionHandler;
    private final ServiceSessionRepository sessions;
    private final AuthorizationSessionRepository authorizationSessions;

    /**
     * This class must ensure that it is separate transaction - so it won't join any other as is used with
     * CompletableFuture.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <O, R extends FacadeServiceableGetter> FacadeResult<O> handleResult(Result<O> result, FacadeServiceableRequest request, ServiceContext<R> session) {
        SecretKeyWithIv sessionKey = provider.deregister(session.getRequestScoped()).getKey();
        return doHandleResult(result, request, session, sessionKey);
    }

    private <O, R extends FacadeServiceableGetter> FacadeResult<O> doHandleResult(
            Result<O> result,
            FacadeServiceableRequest request,
            ServiceContext<R> session,
            SecretKeyWithIv sessionKey
    ) {
        if (result instanceof SuccessResult) {
            return handleSuccess((SuccessResult<O>) result, request.getRequestId(), session);
        }

        if (result instanceof ConsentAcquiredResult) {
            return handleConsentAcquired((ConsentAcquiredResult<O, ?>) result);
        }

        if (result instanceof ErrorResult) {
            return handleError((ErrorResult<O>) result, request.getRequestId(), session, request);
        }

        if (result instanceof RedirectionResult) {
            return handleRedirect((RedirectionResult<O, ?>) result, request, session, sessionKey);
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
            ErrorResult<O> result, UUID xRequestId, ServiceContext<R> session, FacadeServiceableRequest request
    ) {
        FacadeRedirectErrorResult<O, AuthStateBody> mappedResult =
            (FacadeRedirectErrorResult<O, AuthStateBody>) FacadeRedirectErrorResult.ERROR_FROM_PROTOCOL.map(result);
        mappedResult.setServiceSessionId(session.getServiceSessionId().toString());
        mappedResult.setRedirectionTo(URI.create(request.getFintechRedirectUrlNok()));
        mappedResult.setXRequestId(xRequestId);
        addAuthorizationSessionDataIfAvailable(result, request, session, mappedResult);
        return mappedResult;
    }

    protected <O> FacadeResult<O> handleConsentAcquired(ConsentAcquiredResult<O, ?> result) {
        FacadeRedirectResult<O, AuthStateBody> mappedResult =
            (FacadeRedirectResult<O, AuthStateBody>) FacadeRedirectResult.FROM_PROTOCOL.map(result);
        mappedResult.setRedirectionTo(result.getRedirectionTo());
        return mappedResult;
    }

    protected <O, R extends FacadeServiceableGetter> FacadeResultRedirectable<O, AuthStateBody> handleRedirect(
        RedirectionResult<O, ?> result, FacadeServiceableRequest request, ServiceContext<R> session, SecretKeyWithIv sessionKey
    ) {
        if (result instanceof AuthorizationDeniedResult) {
            return doHandleAbortAuthorization(result, request.getRequestId(), session);
        }

        Optional<AuthSession> authSession = authorizationSessions.findByParentId(session.getServiceSessionId());

        return authSession
                .map(value -> handleExistingAuthSession(result, request, session, value))
                .orElseGet(() -> handleNewAuthSession(result, request, session, sessionKey));
    }

    protected <O> FacadeRedirectResult<O, AuthStateBody> doHandleAbortAuthorization(
            RedirectionResult<O, ?> result, UUID xRequestId, ServiceContext session
    ) {
        FacadeRedirectResult<O, AuthStateBody> mappedResult =
                (FacadeRedirectResult<O, AuthStateBody>) FacadeRedirectResult.FROM_PROTOCOL.map(result);

        if (sessions.findById(session.getServiceSessionId()).isPresent()) {
            sessions.deleteById(session.getServiceSessionId());
        }

        mappedResult.setCause(mapCause(result));
        mappedResult.setXRequestId(xRequestId);
        return mappedResult;
    }


    protected <O> void setAspspRedirectCodeIfRequired(RedirectionResult<O, ?> result, AuthSession session, ServiceContext context) {
        if (result instanceof AuthorizationRequiredResult) {
            session.setAspspRedirectCode(context.getFutureAspspRedirectCode().toString());
        }
    }

    protected <O, R extends FacadeServiceableGetter> void addAuthorizationSessionDataIfAvailable(
            Result<O> result, FacadeServiceableRequest request, ServiceContext<R> session, FacadeResultRedirectable mappedResult) {
        Optional<AuthSession> authSession = authorizationSessions.findByParentId(session.getServiceSessionId());
        if (!authSession.isPresent()) {
            return;
        }

        addAuthorizationSessionData(result, authSession.get(), request, session, mappedResult);
    }

    protected <O> AuthSession addAuthorizationSessionData(
            Result<O> result,
            AuthSession authSession,
            FacadeServiceableRequest request,
            ServiceContext session,
            FacadeResultRedirectable<O, ?> mappedResult
    ) {
        authSession.setRedirectCode(session.getFutureRedirectCode().toString());
        authSession.setContext(result.authContext());
        authorizationSessions.save(authSession);

        mappedResult.setAuthorizationSessionId(authSession.getId().toString());
        mappedResult.setServiceSessionId(authSession.getParent().getId().toString());
        mappedResult.setXRequestId(request.getRequestId());
        mappedResult.setRedirectCode(authSession.getRedirectCode());
        return authSession;
    }


    @NotNull
    private <O, R extends FacadeServiceableGetter> FacadeResultRedirectable<O, AuthStateBody> handleExistingAuthSession(
            RedirectionResult<O, ?> result,
            FacadeServiceableRequest request,
            ServiceContext<R> session,
            AuthSession authSession) {
        FacadeRedirectResult<O, AuthStateBody> mappedResult =
                (FacadeRedirectResult<O, AuthStateBody>) FacadeRedirectResult.FROM_PROTOCOL.map(result);
        addAuthorizationSessionData(result, authSession, request, session, mappedResult);
        mappedResult.setCause(mapCause(result));
        setAspspRedirectCodeIfRequired(result, authSession, session);
        return mappedResult;
    }

    @NotNull
    private <O, R extends FacadeServiceableGetter> FacadeResultRedirectable<O, AuthStateBody> handleNewAuthSession(
            RedirectionResult<O, ?> result,
            FacadeServiceableRequest request,
            ServiceContext<R> session,
            SecretKeyWithIv sessionKey
    ) {
        FacadeStartAuthorizationResult<O, AuthStateBody> mappedResult =
                (FacadeStartAuthorizationResult<O, AuthStateBody>) FacadeStartAuthorizationResult.FROM_PROTOCOL.map(result);
        AuthSession newAuthSession = newAuthSessionHandler.createNewAuthSession(request, sessionKey, session, mappedResult);
        addAuthorizationSessionData(result, newAuthSession, request, session, mappedResult);
        mappedResult.setCause(mapCause(result));
        setAspspRedirectCodeIfRequired(result, newAuthSession, session);
        return mappedResult;
    }


    protected AuthStateBody mapCause(RedirectionResult result) {
        if (result instanceof ValidationErrorResult && null != result.getCause()) {
            return (AuthStateBody) result.getCause();
        }

        return null;
    }
}
