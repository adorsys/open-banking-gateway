package de.adorsys.opba.protocol.hbci.service.protocol.ais.dto;

import de.adorsys.multibanking.domain.BalancesReport;
import de.adorsys.multibanking.domain.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AisListTransactionsResult {

    private List<Booking> bookings;
    private BalancesReport balancesReport;
    private Instant cachedAt = Instant.now();
}
