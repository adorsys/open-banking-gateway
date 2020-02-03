package de.adorsys.opba.protocol.xs2a.entrypoint.authorization;

import de.adorsys.opba.consentapi.model.generated.InlineResponse200;
import de.adorsys.opba.protocol.api.UpdateAuthorization;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.authentication.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.Result;
import de.adorsys.opba.protocol.xs2a.entrypoint.OutcomeMapper;
import de.adorsys.opba.protocol.xs2a.service.ContextUpdateService;
import de.adorsys.opba.protocol.xs2a.service.eventbus.ProcessEventHandlerRegistrar;
import de.adorsys.opba.protocol.xs2a.service.json.JsonPathBasedObjectUpdater;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service("xs2aUpdateAuthorization")
@RequiredArgsConstructor
public class Xs2aUpdateAuthorization implements UpdateAuthorization {

    private final ProcessEventHandlerRegistrar registrar;
    private final RuntimeService runtimeService;
    private final JsonPathBasedObjectUpdater updater;
    private final ContextUpdateService ctxUpdater;

    @Override
    public CompletableFuture<Result<InlineResponse200>> execute(ServiceContext<AuthorizationRequest> serviceContext) {
        String executionId = serviceContext.getAuthContext();

        ctxUpdater.updateContext(
                executionId,
                toUpdate -> updater.updateObjectUsingJsonPath(
                        toUpdate,
                        serviceContext.getRequest().getScaAuthenticationData()
                )
        );

        CompletableFuture<Result<InlineResponse200>> result = new CompletableFuture<>();

        registrar.addHandler(
                runtimeService.createExecutionQuery().executionId(executionId).singleResult().getProcessInstanceId(),
                new OutcomeMapper<>(result, res -> new InlineResponse200())
        );

        return result;
    }
}
