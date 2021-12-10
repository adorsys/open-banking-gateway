package de.adorsys.opba.protocol.facade.services.pis;

import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
import de.adorsys.opba.protocol.api.dto.request.payments.PisAuthorizationStatusRequest;
import de.adorsys.opba.protocol.api.dto.result.body.PisAuthorizationStatusBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.ok.SuccessResult;
import de.adorsys.opba.protocol.api.pis.GetPisAuthorizationStatus;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.services.GetAuthorizationStatusService;
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
 * Get Payment authorization status action handler.
 * Unlike other types of Facade services, this one does not require protocol implementation available.
 */
@Service
public class GetPisAuthorizationStatusService extends GetAuthorizationStatusService<PisAuthorizationStatusRequest, PisAuthorizationStatusBody, GetPisAuthorizationStatus> {

    private final ServiceSessionRepository svcSessions;
    private final AuthorizationSessionRepository sessions;

    public GetPisAuthorizationStatusService(
            ServiceSessionRepository svcSessions,
            AuthorizationSessionRepository sessions,
            Map<String, ? extends GetPisAuthorizationStatus> actionProviders,
            ProtocolSelector selector,
            @Qualifier(FINTECH_CONTEXT_PROVIDER) ServiceContextProvider provider,
            ProtocolResultHandler handler,
            TransactionTemplate txTemplate) {
        super(GET_AIS_AUTHORIZATION_STATUS, actionProviders, selector, provider, handler, txTemplate);
        this.svcSessions = svcSessions;
        this.sessions = sessions;
    }

    @Override
    protected CompletableFuture<FacadeResult<PisAuthorizationStatusBody>> handleProtocolResult(
            PisAuthorizationStatusRequest pisAuthorizationStatusRequest,
            ProtocolWithCtx<GetPisAuthorizationStatus, PisAuthorizationStatusRequest> protocolWithCtx,
            CompletableFuture<Result<PisAuthorizationStatusBody>> result
    ) {
        var dbSvcSession = svcSessions.findById(protocolWithCtx.getServiceContext().getServiceSessionId()).orElseThrow();
        var statusResult = result.thenApply(it -> {
            var dbAuthSession = sessions.findByParentId(protocolWithCtx.getServiceContext().getServiceSessionId()).orElse(null);
            var status = null == it ? new SuccessResult<>(new PisAuthorizationStatusBody()) : it;
            updateStatusFromDb(dbSvcSession, dbAuthSession, status);
            return status;
        });

        return super.handleProtocolResult(pisAuthorizationStatusRequest, protocolWithCtx, statusResult);
    }
}
