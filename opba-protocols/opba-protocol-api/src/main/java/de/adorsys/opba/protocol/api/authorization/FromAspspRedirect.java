package de.adorsys.opba.protocol.api.authorization;

import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.dto.request.authorization.fromaspsp.FromAspspRequest;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;

/**
 * Called when returning from ASPSP in Redirect consent authorization (handles both OK and NOK redirect cases).
 * <p>
 * Typical outcomes:
 * <ul>
 *     <li>
 *         {@link de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.RedirectionResult}
 *         <p>Returned when request was successful. Points to page showing Consent acquired/Acquisition failed
 *         and respective FinTech OK / FinTech NOK urls associated with that page.</p>
 *         For OAuth2 consent/payment authentication/authorization is used as the entrypoint to receive {@code code}
 *         that will be exchanged to token.
 *     </li>
 * </ul>
 */
@FunctionalInterface
public interface FromAspspRedirect extends Action<FromAspspRequest, UpdateAuthBody> {
}
