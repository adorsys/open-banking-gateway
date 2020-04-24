package de.adorsys.opba.protocol.api.authorization;

import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.dto.request.authorization.DenyAuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.body.DenyAuthBody;

/**
 * Called within embedded Consent authorization to deny consent.
 *
 * Typical outcomes:
 * <ul>
 *     <li>
 *         {@link de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.AuthorizationDeniedResult}<br/>
 *         Returned when request was successful. Causes consent to be declined if was created already.
 *     </li>
 * </ul>
 */
@FunctionalInterface
public interface DenyAuthorization extends Action<DenyAuthorizationRequest, DenyAuthBody> {
}
