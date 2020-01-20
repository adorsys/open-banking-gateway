package de.adorsys.obpa.starter;

import de.adorsys.opba.core.protocol.config.EnableBankingProtocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.adorsys.opba.tppbanking.api.config.EnableBankingApi;
import de.adorsys.opba.consentapi.config.EnableConsentApi;

@SpringBootApplication
@EnableBankingApi
@EnableConsentApi
@EnableBankingProtocol
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Springboot starter class is not an utility class
public class OpenBankingEmbeddedApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenBankingEmbeddedApplication.class, args);
    }

}
