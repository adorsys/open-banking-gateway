package de.adorsys.opba.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.adorsys.opba.tppbanking.api.config.EnableBankingApi;
import de.adorsys.opba.consentapi.config.EnableConsentApi;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableBankingApi
@EnableConsentApi
@EntityScan(basePackages = "de.adorsys.opba.tppbanking.impl.domain.entity")
@EnableJpaRepositories(basePackages = "de.adorsys.opba.tppbanking.impl.repository.jpa")
@EnableConfigurationProperties
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Springboot starter class is not an utility class
public class OpenBankingEmbeddedApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenBankingEmbeddedApplication.class, args);
    }

}
