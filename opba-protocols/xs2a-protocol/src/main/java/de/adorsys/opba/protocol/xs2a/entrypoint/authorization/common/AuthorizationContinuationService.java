package de.adorsys.opba.protocol.xs2a.entrypoint.authorization.common;

import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.xs2a.entrypoint.OutcomeMapper;
import de.adorsys.opba.protocol.xs2a.service.eventbus.ProcessEventHandlerRegistrar;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class AuthorizationContinuationService {

    private final ProcessEventHandlerRegistrar registrar;
    private final RuntimeService runtimeService;

    public <T> CompletableFuture<Result<T>> handleAuthorizationProcessContinuation(
        String executionId,
        Function<CompletableFuture<Result<T>>, OutcomeMapper<T>> mapperFactory
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
            res -> new OutcomeMapper<>(res, resp -> new UpdateAuthBody())
        );
    }
}
