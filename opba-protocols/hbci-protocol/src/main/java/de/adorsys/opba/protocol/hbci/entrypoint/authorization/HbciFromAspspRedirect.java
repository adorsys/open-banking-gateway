package de.adorsys.opba.protocol.hbci.entrypoint.authorization;

import de.adorsys.opba.protocol.api.authorization.FromAspspRedirect;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.authorization.fromaspsp.FromAspspRequest;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Operation that is called when user returns back from ASPSP either with OK or NOK status in Redirect SCA
 * authorization mode.
 */
@Service("hbciFromAspspRedirect")
@RequiredArgsConstructor
public class HbciFromAspspRedirect implements FromAspspRedirect {

    @Override
    public CompletableFuture<Result<UpdateAuthBody>> execute(ServiceContext<FromAspspRequest> serviceContext) {
        return null;
    }
}
