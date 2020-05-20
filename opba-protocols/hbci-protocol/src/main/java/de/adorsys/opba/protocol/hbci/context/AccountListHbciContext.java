package de.adorsys.opba.protocol.hbci.context;

import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.AisListAccountsResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccountListHbciContext extends HbciContext {

    /**
     * Real-time or cached result of the operation as HBCI protocol does not have support for consent.
     */
    private AisListAccountsResult response;
}
