package de.adorsys.opba.protocol.facade.services;

import com.google.common.base.Strings;
import de.adorsys.opba.api.security.internal.config.TppTokenProperties;
import de.adorsys.opba.api.security.internal.service.TokenBasedAuthService;
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
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.ConsentIncompatibleResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.RedirectToAspspResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.RedirectionResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.ValidationErrorResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.error.ErrorResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.ok.SuccessResult;
import de.adorsys.opba.protocol.facade.config.encryption.SecretKeyWithIv;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeRedirectErrorResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeRedirectResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeResultRedirectable;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeRuntimeErrorResult;
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
    private final AuthSessionHandler authSessionHandler;
    private final ServiceSessionRepository sessions;
    private final AuthorizationSessionRepository authorizationSessions;
    private final TokenBasedAuthService authService;
    private final TppTokenProperties tppTokenProperties;

    /**
     * This class must ensure that it is separate transaction - so it won't join any other as is used with
     * CompletableFuture.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <RESULT, REQUEST extends FacadeServiceableGetter> FacadeResult<RESULT> handleResult(Result<RESULT> result, FacadeServiceableRequest request, ServiceContext<REQUEST> session) {
        SecretKeyWithIv sessionKey = provider.deregister(session.getRequestScoped()).getKey();
        return doHandleResult(result, request, session, sessionKey);
    }

    private <RESULT, REQUEST extends FacadeServiceableGetter> FacadeResult<RESULT> doHandleResult(
            Result<RESULT> result,
            FacadeServiceableRequest request,
            ServiceContext<REQUEST> session,
            SecretKeyWithIv sessionKey
    ) {
        if (result instanceof SuccessResult) {
            return handleSuccess((SuccessResult<RESULT>) result, request.getRequestId(), session);
        }

        if (result instanceof ConsentAcquiredResult) {
            return handleConsentAcquired((ConsentAcquiredResult<RESULT, ?>) result);
        }

        if (result instanceof ErrorResult) {
            return handleError((ErrorResult<RESULT>) result, request.getRequestId(), session, request);
        }

        if (result instanceof RedirectionResult) {
            return handleRedirect((RedirectionResult<RESULT, ?>) result, request, session, sessionKey);
        }

        throw new IllegalStateException("Can't handle protocol result: " + result.getClass());
    }

    @NotNull
    protected <RESULT, REQUEST extends FacadeServiceableGetter> FacadeResult<RESULT> handleSuccess(
            SuccessResult<RESULT> result, UUID xRequestId, ServiceContext<REQUEST> session
    ) {
        FacadeSuccessResult<RESULT> mappedResult =
                (FacadeSuccessResult<RESULT>) FacadeSuccessResult.FROM_PROTOCOL.map(result);
        mappedResult.setServiceSessionId(session.getServiceSessionId().toString());
        mappedResult.setXRequestId(xRequestId);
        return mappedResult;
    }

    protected <RESULT, REQUEST extends FacadeServiceableGetter> FacadeResult<RESULT> handleError(
            ErrorResult<RESULT> result, UUID xRequestId, ServiceContext<REQUEST> session, FacadeServiceableRequest request
    ) {
        if (Strings.isNullOrEmpty(request.getFintechRedirectUrlNok()) || !result.isCanRedirectBackToFintech()) {
            return handleNonRedirectableError(result, xRequestId, session);
        }

        return handleRedirectableError(result, xRequestId, session, request);
    }

    protected <RESULT, REQUEST extends FacadeServiceableGetter> FacadeResult<RESULT> handleNonRedirectableError(
            ErrorResult<RESULT> result, UUID xRequestId, ServiceContext<REQUEST> session
    ) {
        FacadeRuntimeErrorResult<RESULT> mappedResult = (FacadeRuntimeErrorResult<RESULT>) FacadeRuntimeErrorResult.ERROR_FROM_PROTOCOL.map(result);
        mappedResult.setServiceSessionId(session.getServiceSessionId().toString());

        mappedResult.setXRequestId(xRequestId);
        return mappedResult;
    }

    protected <RESULT, REQUEST extends FacadeServiceableGetter> FacadeResult<RESULT> handleRedirectableError(
            ErrorResult<RESULT> result, UUID xRequestId, ServiceContext<REQUEST> session, FacadeServiceableRequest request
    ) {
        FacadeRedirectErrorResult<RESULT, AuthStateBody> mappedResult =
                (FacadeRedirectErrorResult<RESULT, AuthStateBody>) FacadeRedirectErrorResult.ERROR_FROM_PROTOCOL.map(result);
        mappedResult.setServiceSessionId(session.getServiceSessionId().toString());
        mappedResult.setRedirectionTo(URI.create(request.getFintechRedirectUrlNok()));
        mappedResult.setXRequestId(xRequestId);
        addAuthorizationSessionDataIfAvailable(result, request, session, mappedResult);
        return mappedResult;
    }

    protected <RESULT> FacadeResult<RESULT> handleConsentAcquired(ConsentAcquiredResult<RESULT, ?> result) {
        FacadeRedirectResult<RESULT, AuthStateBody> mappedResult =
            (FacadeRedirectResult<RESULT, AuthStateBody>) FacadeRedirectResult.FROM_PROTOCOL.map(result);
        mappedResult.setRedirectionTo(result.getRedirectionTo());
        return mappedResult;
    }

    protected <RESULT, REQUEST extends FacadeServiceableGetter> FacadeResultRedirectable<RESULT, AuthStateBody> handleRedirect(
            RedirectionResult<RESULT, ?> result, FacadeServiceableRequest request, ServiceContext<REQUEST> session, SecretKeyWithIv sessionKey
    ) {
        if (result instanceof AuthorizationDeniedResult) {
            return doHandleAbortAuthorization(result, request.getRequestId(), session);
        }

        Optional<AuthSession> authSession = authorizationSessions.findByParentId(session.getServiceSessionId());

        return authSession
                .map(it -> handleExistingAuthSession(it, result, request, session, sessionKey))
                .orElseGet(() -> handleNewAuthSession(result, request, session, sessionKey));
    }

    @NotNull
    private <RESULT, REQUEST extends FacadeServiceableGetter> FacadeResultRedirectable<RESULT, AuthStateBody> handleExistingAuthSession(
            AuthSession session,
            RedirectionResult<RESULT, ?> result,
            FacadeServiceableRequest request,
            ServiceContext<REQUEST> context,
            SecretKeyWithIv sessionKey
    ) {
        if (result instanceof ConsentIncompatibleResult) {
            return handleAuthRequiredForExistingAuthSession(result, request, context, sessionKey, session);
        }

        return handleExistingAuthSessionForAuthContinuation(result, request, context, session);
    }

    protected <RESULT> FacadeRedirectResult<RESULT, AuthStateBody> doHandleAbortAuthorization(
            RedirectionResult<RESULT, ?> result, UUID xRequestId, ServiceContext session
    ) {
        FacadeRedirectResult<RESULT, AuthStateBody> mappedResult =
                (FacadeRedirectResult<RESULT, AuthStateBody>) FacadeRedirectResult.FROM_PROTOCOL.map(result);

        if (sessions.findById(session.getServiceSessionId()).isPresent()) {
            sessions.deleteById(session.getServiceSessionId());
        }

        mappedResult.setCause(mapCause(result));
        mappedResult.setXRequestId(xRequestId);
        return mappedResult;
    }


    protected <RESULT> void setAspspRedirectCodeIfRequired(RedirectionResult<RESULT, ?> result, AuthSession session, ServiceContext context) {
        if (result instanceof AuthorizationRequiredResult) {
            session.setAspspRedirectCode(context.getFutureAspspRedirectCode().toString());
        }
    }

    protected <REQUEST, RESULT extends FacadeServiceableGetter> void addAuthorizationSessionDataIfAvailable(
            Result<REQUEST> result, FacadeServiceableRequest request, ServiceContext<RESULT> session, FacadeResultRedirectable mappedResult) {
        Optional<AuthSession> authSession = authorizationSessions.findByParentId(session.getServiceSessionId());
        if (!authSession.isPresent()) {
            return;
        }

        addAuthorizationSessionData(result, authSession.get(), request, session, mappedResult);
    }

    protected <RESULT> AuthSession addAuthorizationSessionData(
            Result<RESULT> result,
            AuthSession authSession,
            FacadeServiceableRequest request,
            ServiceContext session,
            FacadeResultRedirectable<RESULT, ?> mappedResult
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
    private <RESULT, REQUEST extends FacadeServiceableGetter> FacadeResultRedirectable<RESULT, AuthStateBody> handleExistingAuthSessionForAuthContinuation(
            RedirectionResult<RESULT, ?> result,
            FacadeServiceableRequest request,
            ServiceContext<REQUEST> session,
            AuthSession authSession) {
        FacadeRedirectResult<RESULT, AuthStateBody> mappedResult =
                (FacadeRedirectResult<RESULT, AuthStateBody>) FacadeRedirectResult.FROM_PROTOCOL.map(result);

        if (result instanceof RedirectToAspspResult) {
            setAspspRedirectTokenIfRequired(request.getAuthorizationKey(), mappedResult);
        }

        addAuthorizationSessionData(result, authSession, request, session, mappedResult);
        mappedResult.setCause(mapCause(result));
        setAspspRedirectCodeIfRequired(result, authSession, session);
        return mappedResult;
    }

    @NotNull
    private <RESULT, REQUEST extends FacadeServiceableGetter> FacadeResultRedirectable<RESULT, AuthStateBody> handleNewAuthSession(
            RedirectionResult<RESULT, ?> result,
            FacadeServiceableRequest request,
            ServiceContext<REQUEST> session,
            SecretKeyWithIv sessionKey
    ) {
        FacadeStartAuthorizationResult<RESULT, AuthStateBody> mappedResult =
                (FacadeStartAuthorizationResult<RESULT, AuthStateBody>) FacadeStartAuthorizationResult.FROM_PROTOCOL.map(result);
        AuthSession newAuthSession = authSessionHandler.createNewAuthSessionAndEnhanceResult(request, sessionKey, session, mappedResult);
        addAuthorizationSessionData(result, newAuthSession, request, session, mappedResult);
        mappedResult.setCause(mapCause(result));
        setAspspRedirectCodeIfRequired(result, newAuthSession, session);
        return mappedResult;
    }

    @NotNull
    private <RESULT, REQUEST extends FacadeServiceableGetter> FacadeResultRedirectable<RESULT, AuthStateBody> handleAuthRequiredForExistingAuthSession(
            RedirectionResult<RESULT, ?> result,
            FacadeServiceableRequest request,
            ServiceContext<REQUEST> session,
            SecretKeyWithIv sessionKey,
            AuthSession authSession
    ) {
        FacadeStartAuthorizationResult<RESULT, AuthStateBody> mappedResult =
                (FacadeStartAuthorizationResult<RESULT, AuthStateBody>) FacadeStartAuthorizationResult.FROM_PROTOCOL.map(result);
        AuthSession updatedSession = authSessionHandler.reuseAuthSessionAndEnhanceResult(authSession, sessionKey, session, mappedResult);
        addAuthorizationSessionData(result, updatedSession, request, session, mappedResult);
        mappedResult.setCause(mapCause(result));
        setAspspRedirectCodeIfRequired(result, updatedSession, session);
        return mappedResult;
    }

    private void setAspspRedirectTokenIfRequired(String authKey, FacadeRedirectResult mappedResult) {
        if (Strings.isNullOrEmpty(authKey)) {
            return;
        }

        String toAspspRedirectToken = authService.generateToken(authKey, tppTokenProperties.getRedirectTokenValidityDuration());
        mappedResult.setToken(toAspspRedirectToken);
    }

    protected AuthStateBody mapCause(RedirectionResult result) {
        if (result instanceof ValidationErrorResult && null != result.getCause()) {
            return (AuthStateBody) result.getCause();
        }

        return null;
    }
}
