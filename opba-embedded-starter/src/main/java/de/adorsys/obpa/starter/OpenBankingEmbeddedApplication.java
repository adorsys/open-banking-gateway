package de.adorsys.obpa.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.adorsys.opba.tppbankingapi.config.EnableBankingApi;
import de.adorsys.opba.consentapi.config.EnableConsentApi;

@SpringBootApplication
@EnableBankingApi
@EnableConsentApi
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Springboot starter class is not an utility class
public class OpenBankingEmbeddedApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenBankingEmbeddedApplication.class, args);
    }

}
