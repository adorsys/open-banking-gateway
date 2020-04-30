package de.adorsys.opba.protocol.hbci.entrypoint.authorization;

import de.adorsys.opba.protocol.api.authorization.DenyAuthorization;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.authorization.DenyAuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.body.DenyAuthBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Operation that is called to Deny users' consent in Embedded SCA authorization mode.
 */
@Slf4j
@Service("hbciDenyAuthorization")
@RequiredArgsConstructor
public class HbciDenyAuthorization implements DenyAuthorization {

    @Override
    public CompletableFuture<Result<DenyAuthBody>> execute(ServiceContext<DenyAuthorizationRequest> serviceContext) {
        return null;
    }
}
