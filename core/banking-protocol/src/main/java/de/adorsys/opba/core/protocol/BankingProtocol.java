package de.adorsys.opba.core.protocol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableRetry
@EntityScan(basePackages = "de.adorsys.opba.core.protocol.domain.entity")
@EnableJpaRepositories(basePackages = "de.adorsys.opba.core.protocol.repository.jpa")
@EnableConfigurationProperties
@EnableTransactionManagement
@SpringBootApplication(
        scanBasePackages = {
                "de.adorsys.opba.core.protocol.config",
                "de.adorsys.opba.core.protocol.controller",
                "de.adorsys.opba.core.protocol.service",
                "de.adorsys.opba.core.protocol.repository"
        }
)
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Spring entry point
public class BankingProtocol {

    public static void main(String[] args) {
        SpringApplication.run(BankingProtocol.class, args);
    }
}
