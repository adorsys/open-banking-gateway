package de.adorsys.opba.protocol.hbci.service.protocol.ais.dto;

import de.adorsys.multibanking.hbci.model.HbciConsent;
import de.adorsys.opba.protocol.bpmnshared.dto.context.ProtocolResultCache;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
public class HbciResultCache implements ProtocolResultCache<HbciConsent, AisListAccountsResult, AisListTransactionsResult> {

    private HbciConsent consent;
    private AisListAccountsResult accounts;
    private Map<String, AisListTransactionsResult> transactionsById;
    private Instant cachedAt;
}
