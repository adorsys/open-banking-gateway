package de.adorsys.opba.protocol.api.authorization;

import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.dto.request.authorization.OnLoginRequest;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;

/**
 * Called right after user is authorized in OBG, so that login window may redirect user elsewhere.
 *
 * Typical outcomes:
 * <ul>
 *     <li>
 *         {@link de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.RedirectionResult}
 *         <p>Redirection to some next page.</p>
 *     </li>
 * </ul>
 */
@FunctionalInterface
public interface OnLogin extends Action<OnLoginRequest, UpdateAuthBody> {
}
