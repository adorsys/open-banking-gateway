package de.adorsys.opba.protocol.api.ais;

import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;

/**
 * Called to get PSU transaction list from ASPSP using AIS (Account Information Service).
 *
 * Typical outcomes:
 * <ul>
 *     <li>
 *         {@link de.adorsys.opba.protocol.api.dto.result.fromprotocol.ok.SuccessResult}<br/>
 *         Returned when request was successful. Contains requested transaction list.
 *     </li>
 *     <li>
 *         {@link de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.ValidationErrorResult}<br/>
 *         Returned when PSU input is required (or consent was missing). Causes redirection to Consent authorization.
 *     </li>
 *     <li>
 *         {@link de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.AuthorizationRequiredResult}<br/>
 *         Returned when no matching consent was found for the request and PSU needs to authorize new consent.
 *         Causes redirection to Consent authorization.
 *     </li>
 * </ul>
 */
@FunctionalInterface
public interface ListTransactions extends Action<ListTransactionsRequest, TransactionsResponseBody> {
}
