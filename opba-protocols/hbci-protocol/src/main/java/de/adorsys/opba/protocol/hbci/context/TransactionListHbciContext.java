package de.adorsys.opba.protocol.hbci.context;

import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.AisListTransactionsResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class TransactionListHbciContext extends HbciContext {

    /**
     * List transactions from date.
     */
    private LocalDate dateFrom;

    /**
     * List transactions to date.
     */
    private LocalDate dateTo;

    /**
     * Account IBAN to list transactions for.
     */
    private String accountIban;

    /**
     * Real-time or cached result of the operation as HBCI protocol does not have support for consent.
     */
    private AisListTransactionsResult response;
}
