package de.adorsys.opba.protocol.api.authorization;

import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.dto.request.authorization.fromaspsp.FromAspspRequest;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;

/**
 * Called when returning from ASPSP in Redirect consent authorization (handles both OK and NOK redirect cases).
 *
 * Typical outcomes:
 * <ul>
 *     <li>
 *         {@link de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.RedirectionResult}<br/>
 *         Returned when request was successful. Points to page showing Consent acquired/Acquisition failed
 *         and respective FinTech OK / FinTech NOK urls associated with that page.
 *     </li>
 * </ul>
 */
@FunctionalInterface
public interface FromAspspRedirect extends Action<FromAspspRequest, UpdateAuthBody> {
}
