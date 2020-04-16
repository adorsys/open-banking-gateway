package de.adorsys.opba.fintech.impl.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableFeignClients(basePackages = "de.adorsys.opba.fintech.impl.tppclients")
@EnableJpaRepositories(basePackages = {"de.adorsys.opba.fintech.impl.database.repositories"})
@ComponentScan("de.adorsys.opba.fintech.impl")
@EntityScan("de.adorsys.opba.fintech.impl.database.entities")
public class FinTechImplConfig {
}
