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
import de.adorsys.opba.protocol.api.services.ProtocolFacingEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeRedirectErrorResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeRedirectResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeResultRedirectable;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeStartAuthorizationResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.staticres.FacadeSuccessResult;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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

    private final ProtocolFacingEncryptionServiceProvider encryptionServiceProvider;
    private final NewAuthSessionHandler newAuthSessionHandler;
    private final ServiceSessionRepository sessions;
    private final AuthorizationSessionRepository authenticationSessions;

    /**
     * This class must ensure that it is separate transaction - so it won't join any other as is used with
     * CompletableFuture.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <O, R extends FacadeServiceableGetter> FacadeResult<O> handleResult(Result<O> result, FacadeServiceableRequest request, ServiceContext<R> session) {
        encryptionServiceProvider.remove(session.getEncryption());
        if (result instanceof SuccessResult) {
            return handleSuccess((SuccessResult<O>) result, request.getRequestId(), session);
        }

        if (result instanceof ConsentAcquiredResult) {
            return handleConsentAcquired((ConsentAcquiredResult<O, ?>) result, request, session);
        }

        if (result instanceof ErrorResult) {
            return handleError((ErrorResult<O>) result, request, session);
        }

        if (result instanceof RedirectionResult) {
            return handleRedirect((RedirectionResult<O, ?>) result, request, session);
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
        ErrorResult<O> result, FacadeServiceableRequest request, ServiceContext<R> session
    ) {
        FacadeRedirectErrorResult<O, AuthStateBody> mappedResult =
            (FacadeRedirectErrorResult<O, AuthStateBody>) FacadeRedirectErrorResult.ERROR_FROM_PROTOCOL.map(result);

        addAuthorizationSessionData(request, session, mappedResult);
        mappedResult.setRedirectionTo(URI.create(request.getFintechRedirectUrlNok()));
        return mappedResult;
    }

    protected <O, R extends FacadeServiceableGetter> FacadeResult<O> handleConsentAcquired(
            ConsentAcquiredResult<O, ?> result, FacadeServiceableRequest request, ServiceContext<R> session
    ) {
        FacadeRedirectResult<O, AuthStateBody> mappedResult =
            (FacadeRedirectResult<O, AuthStateBody>) FacadeRedirectResult.FROM_PROTOCOL.map(result);

        addAuthorizationSessionData(request, session, mappedResult);
        mappedResult.setRedirectionTo(result.getRedirectionTo());
        return mappedResult;
    }

    protected <O, R extends FacadeServiceableGetter> FacadeResultRedirectable<O, AuthStateBody> handleRedirect(
        RedirectionResult<O, ?> result, FacadeServiceableRequest request, ServiceContext<R> session
    ) {
        if (result instanceof AuthorizationDeniedResult) {
            return doHandleAbortAuthorization(result, request.getRequestId(), session);
        }

        if (!authSessionFromDb(session.getServiceSessionId()).isPresent()) {
            return handleAuthorizationStart(result, request, session);
        }

        return doHandleRedirect(result, request, session);
    }

    @SneakyThrows
    protected <O> FacadeStartAuthorizationResult<O, AuthStateBody> handleAuthorizationStart(
        RedirectionResult<O, ?> result, FacadeServiceableRequest request, ServiceContext session
    ) {
        FacadeStartAuthorizationResult<O, AuthStateBody> mappedResult =
            (FacadeStartAuthorizationResult<O, AuthStateBody>) FacadeStartAuthorizationResult.FROM_PROTOCOL.map(result);

        AuthSession auth = addAuthorizationSessionData(request, session, mappedResult);
        mappedResult.setCause(mapCause(result));
        setAspspRedirectCodeIfRequired(result, auth, session);
        return mappedResult;
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

    protected <O> FacadeRedirectResult<O, AuthStateBody> doHandleRedirect(
        RedirectionResult<O, ?> result, FacadeServiceableRequest request, ServiceContext session
    ) {
        FacadeRedirectResult<O, AuthStateBody> mappedResult =
            (FacadeRedirectResult<O, AuthStateBody>) FacadeRedirectResult.FROM_PROTOCOL.map(result);

        AuthSession auth = addAuthorizationSessionData(request, session, mappedResult);
        mappedResult.setCause(mapCause(result));
        setAspspRedirectCodeIfRequired(result, auth, session);
        return mappedResult;
    }


    protected <O> void setAspspRedirectCodeIfRequired(RedirectionResult<O, ?> result, AuthSession session, ServiceContext context) {
        if (result instanceof AuthorizationRequiredResult) {
            session.setAspspRedirectCode(context.getFutureAspspRedirectCode().toString());
        }
    }

    protected <O> AuthSession addAuthorizationSessionData(FacadeServiceableRequest request, ServiceContext session,
                                                 FacadeResultRedirectable<O, ?> mappedResult) {
        AuthSession authSession = updateAuthContextAndResult(request, session, mappedResult);
        mappedResult.setAuthorizationSessionId(authSession.getId().toString());
        mappedResult.setServiceSessionId(authSession.getParent().getId().toString());
        mappedResult.setXRequestId(request.getRequestId());
        mappedResult.setRedirectCode(authSession.getRedirectCode());
        return authSession;
    }

    protected <O> AuthSession updateAuthContextAndResult(FacadeServiceableRequest request, ServiceContext session, FacadeResultRedirectable<O, ?> result) {
        // Auth session is 1-1 to service session, using id as foreign key
        return authSessionFromDb(session.getServiceSessionId())
                .map(it -> updateExistingAuthSession(session, it))
                .orElseGet(() -> newAuthSessionHandler.createNewAuthSession(request, session, result));
    }

    protected Optional<AuthSession> authSessionFromDb(UUID serviceSessionId) {
        return authenticationSessions.findByParentId(serviceSessionId);
    }

    @NotNull
    protected <O> AuthSession updateExistingAuthSession(ServiceContext session, AuthSession it) {
        it.setRedirectCode(session.getFutureRedirectCode().toString());
        return authenticationSessions.save(it);
    }

    protected AuthStateBody mapCause(RedirectionResult result) {
        if (result instanceof ValidationErrorResult && null != result.getCause()) {
            return (AuthStateBody) result.getCause();
        }

        return null;
    }
}
