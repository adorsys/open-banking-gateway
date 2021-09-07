package de.adorsys.opba.protocol.api.pis;

import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.dto.request.payments.PisAuthorizationStatusRequest;
import de.adorsys.opba.protocol.api.dto.result.body.PisAuthorizationStatusBody;

/**
 * Called to enhance payment authorization status from Facade (focuses on protocol specific and external status)
 */
public interface GetPisAuthorizationStatus extends Action<PisAuthorizationStatusRequest, PisAuthorizationStatusBody> {
}
