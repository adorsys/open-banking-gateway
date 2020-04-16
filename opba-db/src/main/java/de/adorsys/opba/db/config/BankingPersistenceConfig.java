package de.adorsys.opba.db.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJpaAuditing
@EnableTransactionManagement
@EntityScan(basePackages = "de.adorsys.opba.db.domain.entity")
@EnableJpaRepositories(basePackages = "de.adorsys.opba.db.repository.jpa")
@ComponentScan(basePackages = "de.adorsys.opba.db")
public class BankingPersistenceConfig {
}
