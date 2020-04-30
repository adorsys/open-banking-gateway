package de.adorsys.opba.protocol.xs2a.context.ais;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * XS2A context for transaction list. Represents general knowledge about currently executed request, for example, contains
 * outcome results from previous requests as well as user input.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TransactionListXs2aContext extends Xs2aAisContext {

    /**
     * This consent is associated with following account IBAN.
     */
    private String iban;

    /**
     * This consent is associated with following account internal id in the ASPSP.
     */
    private String resourceId;

    /**
     * This consent is associated with following account currency (same IBAN may have multiple currencies yielding
     * different accounts)
     */
    private String currency;

    /**
     * Transaction booking status - executed (BOOKED) or not (PENDING).
     */
    private String bookingStatus;

    /**
     * List transactions from date.
     */
    private LocalDate dateFrom;

    /**
     * List transactions to date.
     */
    private LocalDate dateTo;
}
