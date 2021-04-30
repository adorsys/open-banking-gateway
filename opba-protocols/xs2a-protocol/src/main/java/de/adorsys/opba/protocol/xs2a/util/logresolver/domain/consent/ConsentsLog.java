package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.consent;

import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.NotSensitiveData;
import lombok.Data;

import java.time.LocalDate;


@Data
public class ConsentsLog implements NotSensitiveData {

    private AccountAccessLog access;
    private Boolean recurringIndicator;
    private LocalDate validUntil;
    private Integer frequencyPerDay;
    private Boolean combinedServiceIndicator;

    @Override
    public String getNotSensitiveData() {
        return "ConsentsLog("
                + ")";
    }
}
