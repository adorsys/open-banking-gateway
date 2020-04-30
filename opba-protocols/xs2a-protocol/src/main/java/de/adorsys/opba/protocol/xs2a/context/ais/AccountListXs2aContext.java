package de.adorsys.opba.protocol.xs2a.context.ais;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * XS2A context for account list. Represents general knowledge about currently executed request, for example, contains
 * outcome results from previous requests as well as user input.
 */
// TODO - Make immutable, modify only with toBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountListXs2aContext extends Xs2aAisContext {

    /**
     * Is this consent for account list with account balances.
     */
    // Optional consent-specific
    private Boolean withBalance;
}
