package de.adorsys.opba.protocol.api.services.scoped.aspsp;

import de.adorsys.opba.protocol.api.common.CurrentBankProfile;

/**
 * Protocol facing current ASPSP profile access object. Provides access to bank profile that is used for execution.
 */
public interface UsesCurrentAspspProfile {

    /**
     * @return Bank profile (ASPSP profile {@code de.adorsys.opba.db.domain.entity.BankProfile}) that was selected for
     * current protocol execution.
     */
    CurrentBankProfile aspspProfile();
}
