package de.adorsys.opba.tppbanking.impl.config;

import de.adorsys.opba.tppbanking.api.config.EnableBankingApi;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableBankingApi
@EntityScan(basePackages = "de.adorsys.opba.tppbanking.impl.domain.entity")
@EnableJpaRepositories(basePackages = "de.adorsys.opba.tppbanking.impl.repository.jpa")
public class BankingPersistenceConfig {
}
