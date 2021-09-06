package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.api.common.SessionStatus;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.result.body.AuthorizationStatusBody;
import de.adorsys.opba.protocol.api.dto.result.body.DetailedSessionStatus;
import de.adorsys.opba.protocol.api.dto.result.body.ResultBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.facade.services.context.ServiceContextProvider;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public abstract class GetAuthorizationStatusService<REQUEST extends FacadeServiceableGetter, RESULT extends ResultBody, ACTION extends Action<REQUEST, RESULT>>
        extends FacadeOptionalService<REQUEST, RESULT, ACTION> {

    private static final Set<SessionStatus> STATUSES_COMPLETED = Set.of(SessionStatus.COMPLETED, SessionStatus.ACTIVATED, SessionStatus.ERROR);

    public GetAuthorizationStatusService(
            ProtocolAction action,
            Map<String, ? extends ACTION> actionProviders,
            ProtocolSelector selector,
            ServiceContextProvider provider,
            ProtocolResultHandler handler,
            TransactionTemplate txTemplate
    ) {
        super(action, actionProviders, selector, provider, handler, txTemplate);
    }

    protected void updateStatusFromDb(ServiceSession dbSvcSession, AuthSession dbAuthSession, Result<? extends AuthorizationStatusBody> status) {
        AuthorizationStatusBody statusBody = status.getBody();
        statusBody.setCreatedAt(dbSvcSession.getCreatedAt().atOffset(ZoneOffset.UTC));
        statusBody.setUpdatedAt(dbSvcSession.getModifiedAt().atOffset(ZoneOffset.UTC));
        statusBody.setStatus(SessionStatus.PENDING);
        if (null != dbAuthSession) {
            statusBody.setStatus(SessionStatus.STARTED);
            var detailedStatus = new DetailedSessionStatus();
            if (STATUSES_COMPLETED.contains(dbAuthSession.getStatus())) {
                statusBody.setStatus(dbAuthSession.getStatus());
            }

            detailedStatus.setStatus(dbAuthSession.getStatus());
            detailedStatus.setCreatedAt(dbAuthSession.getCreatedAt().atOffset(ZoneOffset.UTC));
            detailedStatus.setUpdatedAt(dbAuthSession.getModifiedAt().atOffset(ZoneOffset.UTC));

            statusBody.setDetailedStatus(Collections.singletonMap(dbAuthSession.getId(), detailedStatus));
        }
    }
}
