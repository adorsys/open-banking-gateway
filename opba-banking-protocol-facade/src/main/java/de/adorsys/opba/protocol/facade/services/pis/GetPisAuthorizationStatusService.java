package de.adorsys.opba.protocol.facade.services.pis;

import de.adorsys.opba.protocol.api.dto.request.payments.PisAuthorizationStatusRequest;
import de.adorsys.opba.protocol.api.dto.result.body.PisAuthorizationStatusBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.pis.GetPisAuthorizationStatus;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.services.FacadeOptionalService;
import de.adorsys.opba.protocol.facade.services.ProtocolResultHandler;
import de.adorsys.opba.protocol.facade.services.ProtocolSelector;
import de.adorsys.opba.protocol.facade.services.ProtocolWithCtx;
import de.adorsys.opba.protocol.facade.services.context.ServiceContextProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.protocol.api.common.ProtocolAction.GET_PIS_AUTHORIZATION_STATUS;
import static de.adorsys.opba.protocol.facade.services.context.ServiceContextProviderForFintech.FINTECH_CONTEXT_PROVIDER;

/**
 * Unlike other types of Facade services, this one does not require protocol implementation available.
 */
@Service
public class GetPisAuthorizationStatusService extends FacadeOptionalService<PisAuthorizationStatusRequest, PisAuthorizationStatusBody, GetPisAuthorizationStatus> {

    public GetPisAuthorizationStatusService(
            Map<String, ? extends GetPisAuthorizationStatus> actionProviders,
            ProtocolSelector selector,
            @Qualifier(FINTECH_CONTEXT_PROVIDER) ServiceContextProvider provider,
            ProtocolResultHandler handler,
            TransactionTemplate txTemplate) {
        super(GET_PIS_AUTHORIZATION_STATUS, actionProviders, selector, provider, handler, txTemplate);
    }

    @Override
    protected CompletableFuture<FacadeResult<PisAuthorizationStatusBody>> handleProtocolResult(
            PisAuthorizationStatusRequest pisAuthorizationStatusRequest, ProtocolWithCtx<GetPisAuthorizationStatus,
            PisAuthorizationStatusRequest> protocolWithCtx,
            CompletableFuture<Result<PisAuthorizationStatusBody>> result
    ) {
        return super.handleProtocolResult(pisAuthorizationStatusRequest, protocolWithCtx, result);
    }
}
