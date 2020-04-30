package de.adorsys.opba.protocol.xs2a.entrypoint.authorization;

import de.adorsys.opba.protocol.api.authorization.GetAuthorizationState;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AuthStateBody;
import de.adorsys.opba.protocol.api.dto.result.body.ValidationError;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.xs2a.domain.dto.forms.ScaMethod;
import de.adorsys.opba.protocol.xs2a.entrypoint.dto.ContextBasedValidationErrorResult;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.LastRedirectionTarget;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.LastViolations;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.LAST_REDIRECTION_TARGET;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.LAST_VALIDATION_ISSUES;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

/**
 * Entry point to get list of the required inputs from the user. BPMN engine and process is not touched.
 */
@Service("xs2aGetAuthorizationState")
@RequiredArgsConstructor
public class Xs2aGetAuthorizationState implements GetAuthorizationState {

    private final RuntimeService runtimeService;
    private final HistoryService historyService;
    private final ViolationsMapper violationsMapper;
    private final ScaMethodsMapper scaMethodsMapper;

    @Override
    public CompletableFuture<Result<AuthStateBody>> execute(ServiceContext<AuthorizationRequest> serviceContext) {
        String executionId = serviceContext.getAuthContext();

        AuthStateBody result;
        if (null != runtimeService.createExecutionQuery().executionId(executionId).singleResult()) {
            result = readFromRuntime(executionId);
        } else {
            result = readFromHistory(executionId);
        }

        return CompletableFuture.completedFuture(
            new ContextBasedValidationErrorResult<>(
                    null == result.getRedirectTo() ? null : URI.create(result.getRedirectTo()),
                    executionId,
                    result
            )
        );
    }

    private AuthStateBody readFromRuntime(String executionId) {
        Xs2aContext ctx = (Xs2aContext) runtimeService.getVariable(executionId, CONTEXT);

        // Whatever is non-null - that takes precedence
        return buildBody(
            ctx.getAction(),
            null == ctx.getViolations() || ctx.getViolations().isEmpty()
                ? (LastViolations) runtimeService.getVariable(executionId, LAST_VALIDATION_ISSUES)
                : new LastViolations(ctx.getViolations()),
            ctx.getAvailableSca(),
            null == ctx.getLastRedirection()
                ? (LastRedirectionTarget) runtimeService.getVariable(executionId, LAST_REDIRECTION_TARGET)
                : ctx.getLastRedirection()
        );
    }

    private AuthStateBody readFromHistory(String executionId) {
        // Ended processes has very coarse information:
        HistoricActivityInstance finished = historyService.createHistoricActivityInstanceQuery()
            .executionId(executionId)
            .finished()
            .listPage(0, 1)
            .get(0);

        Xs2aContext ctx = (Xs2aContext) historyService.createHistoricVariableInstanceQuery()
            .processInstanceId(finished.getProcessInstanceId())
            .variableName(CONTEXT)
            .singleResult()
            .getValue();

        return buildBody(ctx.getAction(), new LastViolations(ctx.getViolations()), ctx.getAvailableSca(), ctx.getLastRedirection());
    }

    private AuthStateBody buildBody(ProtocolAction action,
                                    LastViolations issues,
                                    List<ScaMethod> scaMethods,
                                    LastRedirectionTarget redirectionTarget) {
        String redirectTo = null == redirectionTarget ? null : redirectionTarget.getRedirectTo();

        return new AuthStateBody(
            action.name(),
            violationsMapper.map(issues.getViolations()),
            scaMethodsMapper.map(scaMethods),
            redirectTo
        );
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface ViolationsMapper extends DtoMapper<Set<ValidationIssue>, Set<ValidationError>> {
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface ScaMethodsMapper extends DtoMapper<List<ScaMethod>, Set<de.adorsys.opba.protocol.api.dto.result.body.ScaMethod>> {
    }
}
