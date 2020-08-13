package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.protocol.api.dto.context.Context;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Getter
@Builder(toBuilder = true)
@Value
public class InternalContext<REQUEST, ACTION> {
    Context<REQUEST> serviceCtx;
    AuthSession authSession;
    ServiceSession session;
    ACTION action;
}
