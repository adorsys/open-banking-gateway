package de.adorsys.opba.db.config;

import de.adorsys.opba.tppbankingapi.config.EnableBankingApi;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableBankingApi
@EntityScan(basePackages = "de.adorsys.opba.db.domain.entity")
@EnableJpaRepositories(basePackages = "de.adorsys.opba.db.repository.jpa")
@ComponentScan(basePackages = "de.adorsys.opba.db")
public class BankingPersistenceConfig {
}
