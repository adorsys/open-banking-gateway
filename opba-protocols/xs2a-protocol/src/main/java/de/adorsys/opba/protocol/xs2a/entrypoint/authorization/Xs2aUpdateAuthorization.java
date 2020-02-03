package de.adorsys.opba.protocol.xs2a.entrypoint.authorization;

import de.adorsys.opba.consentapi.model.generated.InlineResponse200;
import de.adorsys.opba.protocol.api.UpdateAuthorization;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.authentication.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.Result;
import de.adorsys.opba.protocol.xs2a.service.json.JsonPathBasedObjectUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service("xs2aUpdateAuthorization")
@RequiredArgsConstructor
public class Xs2aUpdateAuthorization implements UpdateAuthorization {

    private final JsonPathBasedObjectUpdater updater;

    @Override
    public CompletableFuture<Result<InlineResponse200>> execute(ServiceContext<AuthorizationRequest> serviceContext) {
        return null;
    }
}
