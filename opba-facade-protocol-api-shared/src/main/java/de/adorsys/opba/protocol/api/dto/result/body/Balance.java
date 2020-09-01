package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Value
@Builder
public class Balance {
    private Amount balanceAmount;
    private String balanceType;
    private OffsetDateTime lastChangeDateTime;
    private LocalDate referenceDate;
    private String lastCommittedTransaction;
}
