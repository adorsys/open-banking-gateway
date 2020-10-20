package de.adorsys.opba.protocol.xs2a.entrypoint.authorization.common;

import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;
import de.adorsys.opba.protocol.api.dto.result.body.ValidationError;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.service.eventbus.ProcessEventHandlerRegistrar;
import de.adorsys.opba.protocol.xs2a.entrypoint.Xs2aOutcomeMapper;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Handler class to continue authorization process by triggering it (triggering BPMN process that is associated
 * with current request).
 */
@Service
@RequiredArgsConstructor
public class AuthorizationContinuationService {

    private final ProcessEventHandlerRegistrar registrar;
    private final RuntimeService runtimeService;
    private final DtoMapper<Set<ValidationIssue>, Set<ValidationError>> errorMapper;

    public <T> CompletableFuture<Result<T>> handleAuthorizationProcessContinuation(
            String executionId,
            Function<CompletableFuture<Result<T>>, Xs2aOutcomeMapper<T>> mapperFactory
    ) {
        CompletableFuture<Result<T>> result = new CompletableFuture<>();
        registrar.addHandler(
                runtimeService.createExecutionQuery().executionId(executionId).singleResult().getRootProcessInstanceId(),
                mapperFactory.apply(result)
        );

        runtimeService.trigger(executionId);

        return result;
    }

    public CompletableFuture<Result<UpdateAuthBody>> handleAuthorizationProcessContinuation(String executionId) {
        return handleAuthorizationProcessContinuation(
                executionId,
                res -> new Xs2aOutcomeMapper<>(res, resp -> new UpdateAuthBody(), errorMapper)
        );
    }
}
