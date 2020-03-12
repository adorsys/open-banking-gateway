package de.adorsys.opba.protocol.xs2a.entrypoint.authorization;

import de.adorsys.opba.protocol.api.authorization.GetAuthorizationState;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AuthStateBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.xs2a.entrypoint.dto.ContextBasedValidationErrorResult;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.BaseContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.LastRedirectionTarget;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.LastViolations;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.LAST_REDIRECTION_TARGET;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.LAST_VALIDATION_ISSUES;

@Service("xs2aGetAuthorizationState")
@RequiredArgsConstructor
public class Xs2aGetAuthorizationState implements GetAuthorizationState {

    private final RuntimeService runtimeService;

    @Override
    public CompletableFuture<Result<AuthStateBody>> execute(ServiceContext<AuthorizationRequest> serviceContext) {
        String executionId = serviceContext.getAuthContext();
        BaseContext ctx = (BaseContext) runtimeService.getVariable(executionId, CONTEXT);
        // Whatever is non-null - that takes precedence
        LastViolations issues = null == ctx.getViolations() || ctx.getViolations().isEmpty()
            ? (LastViolations) runtimeService.getVariable(executionId, LAST_VALIDATION_ISSUES)
            : new LastViolations(ctx.getViolations());

        LastRedirectionTarget redirectTo = null == ctx.getLastRedirection()
            ? (LastRedirectionTarget) runtimeService.getVariable(executionId, LAST_REDIRECTION_TARGET)
            : ctx.getLastRedirection();

        URI redirectToAsUri = null == redirectTo ? null : URI.create(redirectTo.getRedirectTo());

        return CompletableFuture.completedFuture(
            new ContextBasedValidationErrorResult<>(redirectToAsUri, executionId, issues.getViolations())
        );
    }
}
