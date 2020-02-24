package de.adorsys.opba.protocol.api.dto.request.authorization;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AisConsent {

    private AisAccountAccess access;
    private Integer frequencyPerDay;
    private Boolean recurringIndicator;
    private Boolean combinedServiceIndicator;
    private String psuId;
    private LocalDate validUntil;
}
