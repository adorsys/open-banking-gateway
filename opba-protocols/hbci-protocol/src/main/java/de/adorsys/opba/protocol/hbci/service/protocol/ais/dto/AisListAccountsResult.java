package de.adorsys.opba.protocol.hbci.service.protocol.ais.dto;

import de.adorsys.multibanking.domain.BankAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AisListAccountsResult {

    private List<BankAccount> accounts;
    private Instant cachedAt = Instant.now();
}
