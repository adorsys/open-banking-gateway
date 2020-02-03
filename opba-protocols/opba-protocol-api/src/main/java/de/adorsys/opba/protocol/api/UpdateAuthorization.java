package de.adorsys.opba.protocol.api;

import de.adorsys.opba.consentapi.model.generated.InlineResponse200;
import de.adorsys.opba.protocol.api.dto.request.authentication.AuthorizationRequest;

@FunctionalInterface
public interface UpdateAuthorization extends Action<AuthorizationRequest, InlineResponse200> {
}
