package de.adorsys.opba.protocol.xs2a.entrypoint.authorization;

import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.xs2a.entrypoint.OutcomeMapper;
import de.adorsys.opba.protocol.xs2a.service.eventbus.ProcessEventHandlerRegistrar;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AuthorizationContinuationService {

    private final ProcessEventHandlerRegistrar registrar;
    private final RuntimeService runtimeService;

    CompletableFuture<Result<UpdateAuthBody>> handleAuthorizationProcessContinuation(String executionId) {
        CompletableFuture<Result<UpdateAuthBody>> result = new CompletableFuture<>();
        registrar.addHandler(
            runtimeService.createExecutionQuery().executionId(executionId).singleResult().getRootProcessInstanceId(),
            new OutcomeMapper<>(result, res -> new UpdateAuthBody())
        );

        runtimeService.trigger(executionId);

        return result;
    }
}
