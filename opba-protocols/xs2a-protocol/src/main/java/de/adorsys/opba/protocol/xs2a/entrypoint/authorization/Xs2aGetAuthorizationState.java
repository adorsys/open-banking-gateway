package de.adorsys.opba.protocol.xs2a.entrypoint.authorization;

import de.adorsys.opba.protocol.api.authorization.GetAuthorizationState;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AuthStateBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.xs2a.entrypoint.dto.ContextBasedValidationErrorResult;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.LastViolations;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.LAST_REDIRECTION_TARGET;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.LAST_VALIDATION_ISSUES;

@Service("xs2aGetAuthorizationState")
@RequiredArgsConstructor
public class Xs2aGetAuthorizationState implements GetAuthorizationState {

    private final RuntimeService runtimeService;

    @Override
    public CompletableFuture<Result<AuthStateBody>> execute(ServiceContext<AuthorizationRequest> serviceContext) {
        String executionId = serviceContext.getAuthContext();
        LastViolations issues = (LastViolations) runtimeService.getVariable(executionId, LAST_VALIDATION_ISSUES);
        String redirectTo = (String) runtimeService.getVariable(executionId, LAST_REDIRECTION_TARGET);
        URI redirectToAsUri = null == redirectTo ? null : URI.create(redirectTo);

        return CompletableFuture.completedFuture(
            new ContextBasedValidationErrorResult<>(redirectToAsUri, executionId, issues.getViolations())
        );
    }
}
