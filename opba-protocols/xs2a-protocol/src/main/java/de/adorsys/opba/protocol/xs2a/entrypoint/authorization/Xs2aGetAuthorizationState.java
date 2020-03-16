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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricActivityInstance;
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
    private final HistoryService historyService;

    @Override
    public CompletableFuture<Result<AuthStateBody>> execute(ServiceContext<AuthorizationRequest> serviceContext) {
        String executionId = serviceContext.getAuthContext();

        ContextResult result;
        if (null != runtimeService.createExecutionQuery().executionId(executionId).singleResult()) {
            result = readFromRuntime(executionId);
        } else {
            result = readFromHistory(executionId);
        }

        URI redirectToAsUri =
            null == result.getRedirect() || null == result.getRedirect().getRedirectTo()
            ? null
            : URI.create(result.getRedirect().getRedirectTo());

        return CompletableFuture.completedFuture(
            new ContextBasedValidationErrorResult<>(redirectToAsUri, executionId, result.getIssues().getViolations())
        );
    }

    private ContextResult readFromRuntime(String executionId) {
        BaseContext ctx = (BaseContext) runtimeService.getVariable(executionId, CONTEXT);

        // Whatever is non-null - that takes precedence
        return new ContextResult(
            null == ctx.getViolations() || ctx.getViolations().isEmpty()
                ? (LastViolations) runtimeService.getVariable(executionId, LAST_VALIDATION_ISSUES)
                : new LastViolations(ctx.getViolations()),
            null == ctx.getLastRedirection()
                ? (LastRedirectionTarget) runtimeService.getVariable(executionId, LAST_REDIRECTION_TARGET)
                : ctx.getLastRedirection()
        );
    }

    private ContextResult readFromHistory(String executionId) {
        // Ended processes has very coarse information:
        HistoricActivityInstance finished = historyService.createHistoricActivityInstanceQuery()
            .executionId(executionId)
            .finished()
            .listPage(0, 1)
            .get(0);

        BaseContext ctx = (BaseContext) historyService.createHistoricVariableInstanceQuery()
            .processInstanceId(finished.getProcessInstanceId())
            .variableName(CONTEXT)
            .singleResult()
            .getValue();

        return new ContextResult(new LastViolations(ctx.getViolations()), ctx.getLastRedirection());
    }

    @Data
    @AllArgsConstructor
    private static class ContextResult {

        private LastViolations issues;
        private LastRedirectionTarget redirect;
    }
}
