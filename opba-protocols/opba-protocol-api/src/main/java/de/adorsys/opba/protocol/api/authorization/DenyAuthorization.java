package de.adorsys.opba.protocol.api.authorization;

import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.dto.request.authorization.DenyAuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.body.DenyAuthBody;

@FunctionalInterface
public interface DenyAuthorization extends Action<DenyAuthorizationRequest, DenyAuthBody> {
}
