package de.adorsys.opba.fintech.server;

import de.adorsys.opba.fintech.impl.config.EnableFinTechImplConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFinTechImplConfig
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Springboot starter class is not an utility class
public class FinTechServer {
    public static void main(String[] args) {
        SpringApplication.run(FinTechServer.class, args);
    }

}
