package de.adorsys.opba.tppbankingapi.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableBankingApi
@EntityScan(basePackages = "de.adorsys.opba.db.domain.entity")
@EnableJpaRepositories(basePackages = "de.adorsys.opba.db.repository.jpa")
public class BankingPersistenceConfig {
}
