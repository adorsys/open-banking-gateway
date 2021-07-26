package de.adorsys.opba.protocol.xs2a.service;

import de.adorsys.opba.protocol.bpmnshared.service.SafeCacheSerDeUtil;
import de.adorsys.opba.protocol.bpmnshared.service.cache.CachedResultAccessor;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.context.Xs2aResultCache;
import de.adorsys.xs2a.adapter.api.model.AccountList;
import de.adorsys.xs2a.adapter.api.model.TransactionsResponse200Json;
import org.springframework.stereotype.Service;

@Service
public class Xs2aCachedResultAccessor extends CachedResultAccessor<String, AccountList, TransactionsResponse200Json, Xs2aResultCache, Xs2aContext> {

    public Xs2aCachedResultAccessor(SafeCacheSerDeUtil safeCacheSerDe) {
        super(safeCacheSerDe);
    }
}
