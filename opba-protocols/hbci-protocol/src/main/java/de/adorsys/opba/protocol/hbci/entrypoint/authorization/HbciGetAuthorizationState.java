package de.adorsys.opba.protocol.hbci.entrypoint.authorization;

import de.adorsys.opba.protocol.api.authorization.GetAuthorizationState;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AuthStateBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Entry point to get list of the required inputs from the user.
 */
@Service("hbciGetAuthorizationState")
@RequiredArgsConstructor
public class HbciGetAuthorizationState implements GetAuthorizationState {

    @Override
    public CompletableFuture<Result<AuthStateBody>> execute(ServiceContext<AuthorizationRequest> serviceContext) {
        return null;
    }
}
