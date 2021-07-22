package de.adorsys.opba.protocol.bpmnshared.dto.context;

import java.time.Instant;
import java.util.Map;

/**
 * Contains cached protocol result values.
 */
public interface ProtocolResultCache<CONSENT, ACCOUNTS, TRANSACTIONS> {

    CONSENT getConsent();
    void setConsent(CONSENT consent);

    ACCOUNTS getAccounts();
    void setAccounts(ACCOUNTS accounts);

    Map<String, TRANSACTIONS> getTransactionsById();
    void setTransactionsById(Map<String, TRANSACTIONS> transactions);

    Instant getCachedAt();
    void setCachedAt(Instant cachedAt);
}
