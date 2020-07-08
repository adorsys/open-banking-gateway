package de.adorsys.opba.protocol.sandbox.hbci;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Springboot starter class is not an utility class
public class HbciServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HbciServerApplication.class, args);
    }
}
