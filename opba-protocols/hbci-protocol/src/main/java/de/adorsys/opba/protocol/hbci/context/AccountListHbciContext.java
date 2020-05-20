package de.adorsys.opba.protocol.hbci.context;

import de.adorsys.multibanking.domain.response.AccountInformationResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccountListHbciContext extends HbciContext {

    /**
     * Real-time result of the operation as HBCI protocol does not have support for consent.
     */
    private AccountInformationResponse response;

    /**
     * Cached result of the operation as HBCI protocol does not have support for consent.
     */
    private AccountInformationResponse cachedResult;
}
