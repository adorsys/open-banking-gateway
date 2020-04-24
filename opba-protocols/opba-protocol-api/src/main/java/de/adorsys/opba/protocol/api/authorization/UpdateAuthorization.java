package de.adorsys.opba.protocol.api.authorization;

import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;

/**
 * Called when PSU provides fields that are necessary to proceed with authorization (PSU ID, password, etc.).
 * Typically is the next action after {@link GetAuthorizationState} is called to show user which fields he needs to provide.
 * Or is a standalone action when simple forms (like password) are filled.
 *
 * Typical outcomes:
 * <ul>
 *     <li>
 *         {@link de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.RedirectionResult}<br/>
 *         Points to the next page that must be shown to the user in order to proceed with authorization.
 *     </li>
 *     <li>
 *         {@link de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.ValidationErrorResult}<br/>
 *         Contains list of fields that are required from PSU and points to the next page that must be shown to the
 *         user in order to proceed with authorization.
 *     </li>
 * </ul>
 */
@FunctionalInterface
public interface UpdateAuthorization extends Action<AuthorizationRequest, UpdateAuthBody> {
}
