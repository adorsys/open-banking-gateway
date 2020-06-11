package de.adorsys.opba.protocol.hbci.context;

import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.AisListTransactionsResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TransactionListHbciContext extends HbciContext {

    /**
     * Account IBAN to list transactions for.
     */
    private String accountIban;

    /**
     * Real-time or cached result of the operation as HBCI protocol does not have support for consent.
     */
    private AisListTransactionsResult response;
}
