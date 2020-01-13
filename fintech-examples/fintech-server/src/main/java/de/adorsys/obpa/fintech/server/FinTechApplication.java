package de.adorsys.obpa.fintech.server;

import de.adorsys.opba.fintech.impl.config.EnableFinTechImplConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFinTechImplConfig
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Springboot starter class is not an utility class
public class FinTechApplication {
    public static void main(String[] args) {
        SpringApplication.run(FinTechApplication.class, args);
    }

}
