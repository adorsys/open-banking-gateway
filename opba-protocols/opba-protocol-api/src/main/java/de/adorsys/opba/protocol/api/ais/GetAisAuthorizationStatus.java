package de.adorsys.opba.protocol.api.ais;

import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.dto.request.accounts.AisAuthorizationStatusRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AisAuthorizationStatusBody;

/**
 * Called to enhance consent authorization status from Facade (focuses on protocol specific and external status)
 */
public interface GetAisAuthorizationStatus extends Action<AisAuthorizationStatusRequest, AisAuthorizationStatusBody> {
}
