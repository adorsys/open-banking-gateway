package de.adorsys.opba.core.protocol;

import de.adorsys.opba.db.config.EnableBankingPersistence;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableRetry
@EnableBankingPersistence
@EnableConfigurationProperties
@EnableTransactionManagement
@SpringBootApplication(
        scanBasePackages = {
                "de.adorsys.opba.core.protocol.config",
                "de.adorsys.opba.core.protocol.controller",
                "de.adorsys.opba.core.protocol.service",
                "de.adorsys.opba.db"
        }
)
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Spring entry point
public class BankingProtocol {

    public static void main(String[] args) {
        SpringApplication.run(BankingProtocol.class, args);
    }
}
