package de.adorsys.opba.protocol.sandbox.hbci;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableConfigurationProperties
@EntityScan(basePackages = "de.adorsys.opba.protocol.sandbox.hbci.domain")
@EnableJpaRepositories(basePackages = "de.adorsys.opba.protocol.sandbox.hbci.repository")
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Springboot starter class is not an utility class
public class HbciServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HbciServerApplication.class, args);
    }
}
