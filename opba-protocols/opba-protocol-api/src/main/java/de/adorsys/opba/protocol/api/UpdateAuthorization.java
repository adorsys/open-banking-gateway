package de.adorsys.opba.protocol.api;

import de.adorsys.opba.protocol.api.dto.request.authentication.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;

@FunctionalInterface
public interface UpdateAuthorization extends Action<AuthorizationRequest, UpdateAuthBody> {
}
