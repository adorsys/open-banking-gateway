package de.adorsys.opba.starter;

import de.adorsys.opba.config.migration.EnableMigration;
import de.adorsys.opba.consentapi.config.EnableConsentApi;
import de.adorsys.opba.tppbankingapi.config.EnableBankingPersistence;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConsentApi
@EnableBankingPersistence
@EnableConfigurationProperties
@EnableMigration
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Springboot starter class is not an utility class
public class OpenBankingEmbeddedApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenBankingEmbeddedApplication.class, args);
    }

}
