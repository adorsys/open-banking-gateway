package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
import de.adorsys.opba.protocol.api.ais.GetAisAuthorizationStatus;
import de.adorsys.opba.protocol.api.common.SessionStatus;
import de.adorsys.opba.protocol.api.dto.request.accounts.AisAuthorizationStatusRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AisAuthorizationStatusBody;
import de.adorsys.opba.protocol.api.dto.result.body.DetailedSessionStatus;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.ok.SuccessResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.services.FacadeOptionalService;
import de.adorsys.opba.protocol.facade.services.ProtocolResultHandler;
import de.adorsys.opba.protocol.facade.services.ProtocolSelector;
import de.adorsys.opba.protocol.facade.services.ProtocolWithCtx;
import de.adorsys.opba.protocol.facade.services.context.ServiceContextProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.protocol.api.common.ProtocolAction.GET_AIS_AUTHORIZATION_STATUS;
import static de.adorsys.opba.protocol.facade.services.context.ServiceContextProviderForFintech.FINTECH_CONTEXT_PROVIDER;

/**
 * Unlike other types of Facade services, this one does not require protocol implementation available.
 */
@Service
public class GetAisAuthorizationStatusService extends FacadeOptionalService<AisAuthorizationStatusRequest, AisAuthorizationStatusBody, GetAisAuthorizationStatus> {

    private static final Set<SessionStatus> STATUSES_COMPLETED = Set.of(SessionStatus.COMPLETED, SessionStatus.ACTIVATED, SessionStatus.ERROR);

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
            var dbAuthSession = sessions.findByParentId(protocolWithCtx.getServiceContext().getServiceSessionId());
            if (null == it) {
                var status = new AisAuthorizationStatusBody();
                status.setCreatedAt(dbSvcSession.getCreatedAt().atOffset(ZoneOffset.UTC));
                status.setUpdatedAt(dbSvcSession.getModifiedAt().atOffset(ZoneOffset.UTC));
                status.setStatus(SessionStatus.PENDING);
                if (dbAuthSession.isPresent()) {
                    status.setStatus(SessionStatus.STARTED);
                    var detailedStatus = new DetailedSessionStatus();
                    var authSession = dbAuthSession.get();
                    if (STATUSES_COMPLETED.contains(authSession.getStatus())) {
                        status.setStatus(authSession.getStatus());
                    }

                    detailedStatus.setStatus(authSession.getStatus());
                    detailedStatus.setCreatedAt(authSession.getCreatedAt().atOffset(ZoneOffset.UTC));
                    detailedStatus.setUpdatedAt(authSession.getModifiedAt().atOffset(ZoneOffset.UTC));

                    status.setDetailedStatus(Collections.singletonMap(dbAuthSession.get().getId(), detailedStatus));
                }
                return new SuccessResult<>(status);
            }
            return it;
        });
        return super.handleProtocolResult(aisAuthorizationStatusRequest, protocolWithCtx, statusResult);
    }
}
