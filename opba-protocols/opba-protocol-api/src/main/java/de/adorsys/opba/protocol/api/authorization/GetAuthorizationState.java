package de.adorsys.opba.protocol.api.authorization;

import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AuthStateBody;

/**
 * Called within embedded Consent authorization to get fields that are necessary to be provided by user at current stage.
 * Is used to show the user which fields he needs to provide for complex forms.
 *
 * Typical outcomes:
 * <ul>
 *     <li>
 *         {@link de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.ValidationErrorResult}
 *         <p>Contains list of fields that are required from PSU.</p>
 *     </li>
 * </ul>
 */
@FunctionalInterface
public interface GetAuthorizationState extends Action<AuthorizationRequest, AuthStateBody> {
}
