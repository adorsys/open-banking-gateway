package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
import de.adorsys.opba.protocol.api.ais.GetAisAuthorizationStatus;
import de.adorsys.opba.protocol.api.dto.request.accounts.AisAuthorizationStatusRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AisAuthorizationStatusBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.ok.SuccessResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.services.GetAuthorizationStatusService;
import de.adorsys.opba.protocol.facade.services.ProtocolResultHandler;
import de.adorsys.opba.protocol.facade.services.ProtocolSelector;
import de.adorsys.opba.protocol.facade.services.ProtocolWithCtx;
import de.adorsys.opba.protocol.facade.services.context.ServiceContextProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.protocol.api.common.ProtocolAction.GET_AIS_AUTHORIZATION_STATUS;
import static de.adorsys.opba.protocol.facade.services.context.ServiceContextProviderForFintech.FINTECH_CONTEXT_PROVIDER;

/**
 * Returns consent authorization status.
 * Unlike other types of Facade services, this one does not require protocol implementation available.
 */
@Slf4j
@Service
public class GetAisAuthorizationStatusService extends GetAuthorizationStatusService<AisAuthorizationStatusRequest, AisAuthorizationStatusBody, GetAisAuthorizationStatus> {

    private final ServiceSessionRepository svcSessions;
    private final AuthorizationSessionRepository sessions;

    public GetAisAuthorizationStatusService(
            ServiceSessionRepository svcSessions,
            AuthorizationSessionRepository sessions,
            Map<String, ? extends GetAisAuthorizationStatus> actionProviders,
            ProtocolSelector selector,
            @Qualifier(FINTECH_CONTEXT_PROVIDER) ServiceContextProvider provider,
            ProtocolResultHandler handler,
            TransactionTemplate txTemplate) {
        super(GET_AIS_AUTHORIZATION_STATUS, actionProviders, selector, provider, handler, txTemplate);
        this.svcSessions = svcSessions;
        this.sessions = sessions;
    }

    @Override
    protected CompletableFuture<FacadeResult<AisAuthorizationStatusBody>> handleProtocolResult(
            AisAuthorizationStatusRequest aisAuthorizationStatusRequest,
            ProtocolWithCtx<GetAisAuthorizationStatus, AisAuthorizationStatusRequest> protocolWithCtx,
            CompletableFuture<Result<AisAuthorizationStatusBody>> result
    ) {
        var dbSvcSession = svcSessions.findById(protocolWithCtx.getServiceContext().getServiceSessionId()).orElseThrow();
        var statusResult = result.thenApply(it -> {
            var dbAuthSession = sessions.findByParentId(protocolWithCtx.getServiceContext().getServiceSessionId()).orElse(null);
            var status = it;
            if (!(it instanceof SuccessResult)) {
                log.error("[{}] Unexpected result type from protocol", aisAuthorizationStatusRequest.getFacadeServiceable().getRequestId());
                status = new SuccessResult<>(new AisAuthorizationStatusBody());
            }
            updateStatusFromDb(dbSvcSession, dbAuthSession, status);
            return status;
        });

        return super.handleProtocolResult(aisAuthorizationStatusRequest, protocolWithCtx, statusResult);
    }
}
