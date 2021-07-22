package de.adorsys.opba.protocol.hbci.service.consent;

import de.adorsys.multibanking.hbci.model.HbciConsent;
import de.adorsys.opba.protocol.bpmnshared.service.SafeCacheSerDeUtil;
import de.adorsys.opba.protocol.bpmnshared.service.cache.CachedResultAccessor;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.AisListAccountsResult;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.AisListTransactionsResult;
import org.springframework.stereotype.Service;

@Service
public class HbciCachedResultAccessor extends CachedResultAccessor<HbciContext, HbciConsent, AisListAccountsResult, AisListTransactionsResult> {

    public HbciCachedResultAccessor(SafeCacheSerDeUtil safeCacheSerDe) {
        super(safeCacheSerDe);
    }
}
