package de.adorsys.opba.protocol.hbci.service.protocol.ais.dto;

import lombok.Data;

import java.util.Map;

/**
 * Contains cached HBCI result values, since HBCI does not provide any kind of consent.
 */
@Data
public class HbciResultCache {

    /**
     * Last account list result.
     */
    private AisListAccountsResult accounts;

    /**
     * Last transaction list result.
     */
    private Map<String, AisListTransactionsResult> transactionsByIban;
}
