package de.adorsys.obpa.fintech.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.adorsys.opba.fintechapi.config.EnableFinTechApi;

@SpringBootApplication(scanBasePackages = {})
@EnableFinTechApi
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Springboot starter class is not an utility class
public class FinTechApplication {
    public static void main(String[] args) {
        SpringApplication.run(FinTechApplication.class, args);
    }
}
