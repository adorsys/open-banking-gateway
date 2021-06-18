package de.adorsys.opba.protocol.api.ais;

/**
 * Called to remove consent, any cached data and underlying connection that is associated with current session.
 * Is not supported by all interfaces.
 *
 * Typical outcomes:
 * <ul>
 *     <li>
 *         {@link de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.AuthorizationDeniedResult}
 *         <p>Returned when request was successful. Causes consent to be declined if was created already.</p>
 *     </li>
 * </ul>
 */
public interface DeleteConsent {
}
