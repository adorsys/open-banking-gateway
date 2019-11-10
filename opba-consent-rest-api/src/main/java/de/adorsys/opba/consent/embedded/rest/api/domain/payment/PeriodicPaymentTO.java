package de.adorsys.opba.consent.embedded.rest.api.domain.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeriodicPaymentTO extends SinglePaymentTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private String executionRule;
    private FrequencyCodeTO frequency;
    private Integer dayOfExecution;
}

