package de.adorsys.opba.protocol.xs2a.context;

import de.adorsys.opba.protocol.bpmnshared.dto.context.ProtocolResultCache;
import de.adorsys.xs2a.adapter.api.model.AccountList;
import de.adorsys.xs2a.adapter.api.model.TransactionsResponse200Json;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
public class Xs2aResultCache implements ProtocolResultCache<String, AccountList, TransactionsResponse200Json> {

    private String consent;
    private AccountList accounts;
    private Map<String, TransactionsResponse200Json> transactionsById;
    private Instant cachedAt;
}
