package de.adorsys.opba.protocol.api.ais;

import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;

/**
 * Called to get PSU account list from ASPSP using AIS (Account Information Service).
 *
 * Typical outcomes:
 * <ul>
 *     <li>
 *         {@link de.adorsys.opba.protocol.api.dto.result.fromprotocol.ok.SuccessResult}
 *         <p>Returned when request was successful. Contains requested account list.</p>
 *     </li>
 *     <li>
 *         {@link de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.ValidationErrorResult}
 *         <p>Returned when PSU input is required (or consent was missing). Causes redirection to Consent authorization.</p>
 *     </li>
 *     <li>
 *         {@link de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.AuthorizationRequiredResult}
 *         <p>Returned when no matching consent was found for the request and PSU needs to authorize new consent.
 *         Causes redirection to Consent authorization.</p>
 *     </li>
 * </ul>
 */
@FunctionalInterface
public interface ListAccounts extends Action<ListAccountsRequest, AccountListBody> {
}
