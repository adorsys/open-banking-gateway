package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.protocol.api.ais.GetAisAuthorizationStatus;
import de.adorsys.opba.protocol.api.dto.request.accounts.AisAuthorizationStatusRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AisAuthorizationStatusBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
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

import static de.adorsys.opba.protocol.api.common.ProtocolAction.GET_AIS_AUTHORIZATION_STATUS;
import static de.adorsys.opba.protocol.facade.services.context.ServiceContextProviderForFintech.FINTECH_CONTEXT_PROVIDER;

/**
 * Unlike other types of Facade services, this one does not require protocol implementation available.
 */
@Service
public class GetAisAuthorizationStatusService extends FacadeOptionalService<AisAuthorizationStatusRequest, AisAuthorizationStatusBody, GetAisAuthorizationStatus> {

    public GetAisAuthorizationStatusService(
            Map<String, ? extends GetAisAuthorizationStatus> actionProviders,
            ProtocolSelector selector,
            @Qualifier(FINTECH_CONTEXT_PROVIDER) ServiceContextProvider provider,
            ProtocolResultHandler handler,
            TransactionTemplate txTemplate) {
        super(GET_AIS_AUTHORIZATION_STATUS, actionProviders, selector, provider, handler, txTemplate);
    }

    @Override
    protected CompletableFuture<FacadeResult<AisAuthorizationStatusBody>> handleProtocolResult(
            AisAuthorizationStatusRequest aisAuthorizationStatusRequest,
            ProtocolWithCtx<GetAisAuthorizationStatus, AisAuthorizationStatusRequest> protocolWithCtx,
            CompletableFuture<Result<AisAuthorizationStatusBody>> result
    ) {
        return super.handleProtocolResult(aisAuthorizationStatusRequest, protocolWithCtx, result);
    }
}
