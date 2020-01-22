package de.adorsys.opba.starter;

import de.adorsys.opba.config.EnableMigration;
import de.adorsys.opba.consentapi.config.EnableConsentApi;
import de.adorsys.opba.tppbankingapi.config.EnableBankingPersistence;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConsentApi
@EnableBankingPersistence
@EnableConfigurationProperties
@EnableMigration
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Springboot starter class is not an utility class
public class OpenBankingEmbeddedApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(OpenBankingEmbeddedApplication.class)
                .properties("spring.comfig.name:application,application-migration",
                        "spring.config.location:classpath:application.yml,classpath:application-migration.yml")
                .build().run(args);
    }

}
