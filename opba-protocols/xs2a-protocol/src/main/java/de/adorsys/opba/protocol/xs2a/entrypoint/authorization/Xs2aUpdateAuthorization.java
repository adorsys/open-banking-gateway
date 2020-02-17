package de.adorsys.opba.protocol.xs2a.entrypoint.authorization;

import de.adorsys.opba.protocol.api.authorization.UpdateAuthorization;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.xs2a.entrypoint.ExtendWithServiceContext;
import de.adorsys.opba.protocol.xs2a.service.ContextUpdateService;
import de.adorsys.opba.protocol.xs2a.service.json.JsonPathBasedObjectUpdater;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service("xs2aUpdateAuthorization")
@RequiredArgsConstructor
public class Xs2aUpdateAuthorization implements UpdateAuthorization {

    private final AuthorizationContinuationService continuationService;
    private final JsonPathBasedObjectUpdater updater;
    private final ContextUpdateService ctxUpdater;
    private final ExtendWithServiceContext extender;

    @Override
    public CompletableFuture<Result<UpdateAuthBody>> execute(ServiceContext<AuthorizationRequest> serviceContext) {
        String executionId = serviceContext.getAuthContext();

        ctxUpdater.updateContext(
                executionId,
                (Xs2aContext toUpdate) -> {
                    toUpdate = updater.updateObjectUsingJsonPath(
                        toUpdate,
                        serviceContext.getRequest().getScaAuthenticationData()
                    );
                    toUpdate = extender.extend(toUpdate, serviceContext);
                    return toUpdate;
                }
        );

        return continuationService.handleAuthorizationProcessContinuation(executionId);
    }
}
