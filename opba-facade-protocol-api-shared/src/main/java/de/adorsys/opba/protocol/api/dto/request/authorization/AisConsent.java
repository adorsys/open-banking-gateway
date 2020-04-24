package de.adorsys.opba.protocol.api.dto.request.authorization;

import lombok.Data;

import java.time.LocalDate;

/**
 * AIS services access consent.
 */
@Data
public class AisConsent {

    /**
     * Account, transactions, balances access specification.
     */
    private AisAccountAccess access;

    /**
     * How frequent per day this consent can be used.
     */
    private Integer frequencyPerDay;

    /**
     * Is this consent for recurring access.
     */
    private Boolean recurringIndicator;

    /**
     * Is this consent for combined service.
     */
    private Boolean combinedServiceIndicator;

    /**
     * Due date of this consent.
     */
    private LocalDate validUntil;
}
